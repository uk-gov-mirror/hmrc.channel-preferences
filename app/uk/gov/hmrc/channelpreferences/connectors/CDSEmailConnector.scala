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

import uk.gov.hmrc.channelpreferences.hub.cds.model.EmailVerification
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import play.api.http.Status.{ BAD_GATEWAY, OK }
import play.api.libs.json.{ JsSuccess, Json }
import play.api.{ Configuration, Logger, LoggerLike }
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.{ Inject, Singleton }
import scala.util.{ Failure, Success, Try }

@SuppressWarnings(Array("org.wartremover.warts.All"))
@Singleton
class CDSEmailConnector @Inject()(config: Configuration, httpClient: HttpClient)(implicit ec: ExecutionContext)
    extends ServicesConfig(config) {
  private val log: LoggerLike = Logger(this.getClass)
  val serviceUrl: String = baseUrl("customs-data-store")

  private def parseCDSVerifiedEmailResp(body: String): Either[Int, EmailVerification] =
    Try(Json.parse(body)) match {
      case Success(v) =>
        v.validate[EmailVerification] match {
          case JsSuccess(ev, _) => Right(ev)
          case _ =>
            log.warn(s"unable to parse $body")
            Left(BAD_GATEWAY)
        }
      case Failure(e) =>
        log.error(s"cds response was invalid Json", e)
        Left(BAD_GATEWAY)
    }

  def getVerifiedEmail(taxId: String)(implicit hc: HeaderCarrier): Future[Either[Int, EmailVerification]] =
    httpClient.doGet(s"$serviceUrl/customs-data-store/eori/$taxId/verified-email").map { resp =>
      resp.status match {
        case OK => parseCDSVerifiedEmailResp(resp.body)
        case s  => Left(s)
      }
    }
}
