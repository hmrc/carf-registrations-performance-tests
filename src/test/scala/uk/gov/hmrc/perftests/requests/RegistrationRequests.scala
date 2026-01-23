/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.requests

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object RegistrationRequests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("carf-registration-frontend")
  val route: String       = "/register-for-carf"
  val baseUrlAuth: String = baseUrlFor("auth-frontend")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def getAuthLoginPage: ChainBuilder =
    exec(
      http("Get Auth login page")
        .get(baseUrlAuth + "/auth-login-stub/gg-sign-in")
        .check(status.is(200))
    )

  val postOrganisationWithCtUtrAuthLoginPage: ChainBuilder =
    exec(
      http("Enter Auth login credentials")
        .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
        .formParam("authorityId", "")
        .formParam("credentialStrength", "strong")
//        .formParam("excludeGnapToken", "false")
        .formParam("confidenceLevel", "50")
        .formParam("affinityGroup", "Organisation")
//        .formParam("email", "user@test.com")
//        .formParam("credentialRole", "User")
        .formParam("presets-dropdown", "IR-CT")
        .formParam("enrolment[4].name", "IR-CT")
        .formParam("enrolment[4].taxIdentifier[0].name", "UTR")
        .formParam("enrolment[4].taxIdentifier[0].value", "1112345678")
        .formParam("enrolment[4].state", "Activated")
        .formParam("redirectionUrl", baseUrl + route)
        .check(status.is(303))
        .check(header("Location").find.saveAs("redirectLocation"))
        .check(status.saveAs("responseStatus"))
        .check(header("Location").is(baseUrl + route).saveAs("AuthLoginForCarf"))
        .check(bodyString.saveAs("responseBodyForAuthLogin"))
        .check(header("Set-Cookie").saveAs("setCookieHeader"))
    )

  val postOrganisationWithCtUtrAuthLoginPageWithPrint: ChainBuilder =
    exec(postOrganisationWithCtUtrAuthLoginPage)
      .exec { session =>
        println(s"🔍 DEBUG1: Location header = ${session("redirectLocation").asOption[String].getOrElse("NOT FOUND")}")
        println(s"🔍 DEBUG2: Response status = ${session("responseStatus").asOption[Int].getOrElse(-1)}")
        println(
          s"🔍 DEBUG2: Response body = ${session("responseBodyForAuthLogin").asOption[String].getOrElse("AUTH RESPONSE BODY NOT FOUND")}"
        )
        println("🔍 Set-Cookie Header = " + session("setCookieHeader").asOption[String].getOrElse("NOT FOUND"))
        println("&&&&&&&&&&&&&&&&&&&&&&&&" + session)
        println("Test URL = " + baseUrl + route)
        session
      }
  val postIndividualLoginPage: ChainBuilder                         =
    exec(
      http("Enter Auth login credentials for Individual")
        .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
        .formParam("redirectionUrl", baseUrl + route)
        .formParam("authorityId", "")
        .formParam("credentialStrength", "strong")
        .formParam("excludeGnapToken", "false")
        .formParam("confidenceLevel", "50")
        .formParam("affinityGroup", "Individual")
        .formParam("email", "user@test.com")
        .formParam("credentialRole", "User")
        .check(status.is(303))
        .check(header("Location").find.saveAs("redirectLocation"))
    )
  val getIndividualRegistrationType: ChainBuilder                   =
    exec(
      http("Get Individual Registration Type Page")
        .get(baseUrl + route + "/register/individual-registration-type")
        .check(status.is(303))
        .check(header("Location").find.saveAs("redirectLocationForIndividualRegistrationType"))
    )

  val getIndividualRegistrationTypeWithPrint: ChainBuilder =
    exec(getIndividualRegistrationType)
      .exec { session =>
        println(
          s"🔍 DEBUG: Location header = ${session("redirectLocationForIndividualRegistrationType").asOption[String].getOrElse("HEADER NOT FOUND")}"
        )
        println("Individual block executed")
        session
      }

  val getIsThisYourBusinessPage: ChainBuilder =
    exec(
      http("Get Is This Your Business Page")
        .get(baseUrl + route + "/register/is-this-your-business")
        .check(status.is(303))
        .check(header("Location").find.saveAs("redirectLocationForIsYourBusiness"))
        .check(bodyString.saveAs("responseBodyForBusinessPage"))
        .check(status.saveAs("responseStatus"))
        .check(header("Set-Cookie").saveAs("setCookieHeader"))

//        .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
    )
  val getIsThisYourBusinessPageWithPrint: ChainBuilder =
    exec(getIsThisYourBusinessPage)
      .exec { session =>
        println(
          s"🔍 DEBUG: Location header = ${session("redirectLocationForIsYourBusiness").asOption[String].getOrElse("HEADER NOT FOUND")}"
        )
        println("****************" + session)
        println("Test URL2 = " + baseUrl + route + "/register/is-this-your-business")
        println(
          s"🔍 DEBUG2: Response body = ${session("responseBodyForBusinessPage").asOption[String].getOrElse("RESPONSE BODY NOT FOUND")}"
        )
        println("🔍 Response Status = " + session("responseStatus").asOption[Int].getOrElse(-1))
        println(s"Set-Cookie Header = ${session("setCookieHeader").asOption[String].getOrElse("2 COOKIE NOT FOUND")}")

        session
      }
}
