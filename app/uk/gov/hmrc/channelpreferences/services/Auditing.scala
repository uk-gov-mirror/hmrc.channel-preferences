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

package uk.gov.hmrc.channelpreferences.services

import uk.gov.hmrc.emailaddress.EmailAddress
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.EventTypes
import scala.concurrent.ExecutionContext

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
trait Auditing {
  def auditConnector: AuditConnector
  protected val txnName = "transactionName"
  protected val retrieveEmailTxnName: (String, String) = txnName -> "Retrieve Email Address from customs-data-store"

  def auditRetrieveEmail(emailAddress: EmailAddress)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit =
    auditConnector.sendExplicitAudit(EventTypes.Succeeded, Map(retrieveEmailTxnName, "email" -> emailAddress.value))
}
