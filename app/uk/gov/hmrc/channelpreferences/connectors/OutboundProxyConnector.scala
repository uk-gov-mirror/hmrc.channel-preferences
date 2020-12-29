/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.name.Named
import controllers.Assets.Status
import play.api.libs.ws.{ WSClient, WSRequest, WSResponse }
import play.api.mvc._
import play.api.{ Logger, LoggerLike }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
@Singleton
class OutboundProxyConnector @Inject()(@Named("entityResolverUrl") entityResolverHost: String, ws: WSClient)(
  implicit executionContext: ExecutionContext) {

  val log: LoggerLike = Logger(this.getClass)

  private def forward(req: Request[RawBuffer])(url: String) =
    Function
      .chain(
        Seq[WSRequest => WSRequest](
          r => r.withMethod(req.method),
          r => r.withHttpHeaders(req.headers.headers: _*),
          r => req.body.asBytes().fold(r)(r.withBody(_))))(ws.url(url))
      .execute()
      .map {
        case resp =>
          Status(resp.status)(resp.body).withHeaders(headersFrom(resp): _*)
      }

  private def headersFrom(resp: WSResponse) = resp.headers.toIterator.flatMap { case (k, v) => v.map((k, _)) }.toSeq

  def proxy(inboundRequest: Request[RawBuffer]): Future[Result] = {
    val url = entityResolverHost + inboundRequest.uri
    forward(inboundRequest)(url)
  }

}
