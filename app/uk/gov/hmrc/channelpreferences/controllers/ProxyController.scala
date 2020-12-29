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

package uk.gov.hmrc.channelpreferences.controllers

import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.channelpreferences.connectors.OutboundProxyConnector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext
@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
class ProxyController @Inject()(
  outboundConnector: OutboundProxyConnector
)(implicit ec: ExecutionContext, controllerComponents: ControllerComponents)
    extends BackendController(controllerComponents) {

  val log: Logger = Logger(this.getClass)

  def proxy(path: String): Action[RawBuffer] =
    Action.async(controllerComponents.parsers.raw) { implicit request =>
      outboundConnector.proxy(request).recover {
        case ex: Exception =>
          log.error(s"An error occurred proxying $path", ex)
          InternalServerError(ex.getMessage)
      }
    }
}
