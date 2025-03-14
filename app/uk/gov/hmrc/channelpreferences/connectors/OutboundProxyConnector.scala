/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.channelpreferences.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.scaladsl.Source
import akka.util.ByteString
import controllers.Assets.CONTENT_TYPE

import javax.inject.{ Inject, Singleton }
import play.api.http.HeaderNames._
import play.api.http.HttpEntity.Streamed
import play.api.mvc._
import play.api.{ Configuration, Logger, LoggerLike }
import uk.gov.hmrc.play.bootstrap.config.{ ServicesConfig }
import uk.gov.hmrc.play.http.logging.Mdc.preservingMdc

import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
@Singleton
class OutboundProxyConnector @Inject()(config: Configuration)(
  implicit system: ActorSystem,
  executionContext: ExecutionContext)
    extends ServicesConfig(config) {

  import OutboundProxyConnector._

  val serviceUrl: String = baseUrl("entity-resolver")

  val log: LoggerLike = Logger(this.getClass)

  private val http = Http(system)

  def proxy(inboundRequest: Request[Source[ByteString, _]]): Future[Result] = {
    val request: HttpRequest = buildOutboundRequest(inboundRequest)

    logRequest(request)

    preservingMdc(
      http
        .singleRequest(request = request)
    ).map { response =>
      val flattenedHeaders = processResponseHeaders(response.headers)
      val contentType = flattenedHeaders.get(CONTENT_TYPE)
      val contentLength = flattenedHeaders.get(CONTENT_LENGTH).map(_.toLong)

      logResponse(inboundRequest, response)

      Result(
        ResponseHeader(response.status.intValue(), flattenedHeaders),
        Streamed(response.entity.dataBytes, contentLength, contentType)
      )
    }
  }

  private def buildOutboundRequest(inboundRequest: Request[Source[ByteString, _]]): HttpRequest = {
    val uri = Uri(serviceUrl + inboundRequest.uri)
    val headers = processInboundHeaders(inboundRequest.headers)
    val method = HttpMethod.custom(inboundRequest.method)
    val contentType = inboundRequest.headers.toSimpleMap
      .get(CONTENT_TYPE)
      .flatMap(ContentType.parse(_).right.toOption)
      .getOrElse(ContentTypes.`text/plain(UTF-8)`)
    val contentLength = inboundRequest.headers.toSimpleMap.get(CONTENT_LENGTH).map(_.toLong)

    val entity = contentLength match {
      case Some(cl) => HttpEntity(contentType, cl, inboundRequest.body)
      case None     => HttpEntity(contentType, inboundRequest.body)
    }

    HttpRequest().withMethod(method).withUri(uri).withHeaders(headers: _*).withEntity(entity)
  }

  private def logRequest(request: HttpRequest): Unit =
    log.debug(
      s"Outbound Request: ${request.method.value} ${request.uri.path} Content-Type(${request.entity.contentType}) Headers(${loggedHeaders(request.headers)})"
    )

  private def logResponse(request: Request[_], response: HttpResponse): Unit =
    log.debug(
      s"Response: ${request.method} ${fullPath(request)} (${response.status}) Headers(${loggedHeaders(response.headers)})"
    )

}

object OutboundProxyConnector {

  private val outboundHeaderBlackList =
    Set(
      CONNECTION,
      CONTENT_LENGTH,
      HOST,
      PROXY_AUTHENTICATE,
      PROXY_AUTHORIZATION,
      TE,
      TRANSFER_ENCODING,
      TRAILER,
      UPGRADE,
      CONTENT_TYPE,
      USER_AGENT
    )

  val outboundHeadersFilter: ((String, String)) => Boolean = {
    case (key, _) => !outboundHeaderBlackList.contains(key)
  }

  val loggedHeaderBlacklist: Set[String] = Set("Ocp-Apim-Subscription-Key", AUTHORIZATION)

  val loggedHeadersFilter: ((String, String)) => Boolean = {
    case (key, _) => !loggedHeaderBlacklist.contains(key)
  }

  private def processInboundHeaders(inboundHeaders: Headers): Seq[RawHeader] = {
    val filteredInboundHeaders: Seq[(String, String)] =
      flattenToSeq(inboundHeaders.toMap).filter(outboundHeadersFilter)

    filteredInboundHeaders
      .map({ case (name, value) => RawHeader(name, value) })
  }

  private def flattenToSeq(map: Map[String, Seq[String]]): Seq[(String, String)] =
    map.toSeq.flatMap(entry => entry._2.map(value => (entry._1, value)))

  private def processResponseHeaders(headers: Seq[HttpHeader]): Map[String, String] =
    expandToMap(headers).filter(_._1 != CONTENT_TYPE)

  private def fullPath(request: Request[_]): String =
    if (request.rawQueryString.nonEmpty) s"${request.path}?${request.rawQueryString}" else request.path

  private def loggedHeaders(headers: Seq[HttpHeader]): Map[String, String] =
    expandToMap(headers).filter(loggedHeadersFilter)

  private def expandToMap(headers: Seq[HttpHeader]): Map[String, String] =
    headers.map(h => (h.name(), h.value())).groupBy(_._1).mapValues(_.map(_._2).mkString(","))
}
