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

package uk.gov.hmrc.channelpreferences.hub.cds.model

import org.joda.time.DateTime
import uk.gov.hmrc.emailaddress.EmailAddress
import play.api.libs.json._
import play.api.libs.json.Reads._

import play.api.libs.json.JodaWrites.JodaDateTimeWrites
import play.api.libs.json.JodaReads.DefaultJodaDateTimeReads

import scala.util.{ Failure, Success, Try }

final case class EmailVerification(address: EmailAddress, timestamp: DateTime)

@SuppressWarnings(Array("org.wartremover.warts.All"))
object EmailVerification {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit object EmailAddressReads extends Reads[EmailAddress] {
    def reads(json: JsValue) = json match {
      case JsString(s) =>
        Try(EmailAddress(s)) match {
          case Success(v) => JsSuccess(v)
          case Failure(e) => JsError(e.getMessage)
        }
      case _ => JsError("Uable to parse email address")
    }
  }

  implicit object EmailAddressWrites extends Writes[EmailAddress] {
    def writes(e: EmailAddress) = JsString(e.value)
  }

  implicit val emailAddressFormat = Format(EmailAddressReads, EmailAddressWrites)
  implicit val dateTimeFormat = Format[DateTime](DefaultJodaDateTimeReads, JodaDateTimeWrites)

  implicit val emailVerificationFormat = Json.format[EmailVerification]

}
