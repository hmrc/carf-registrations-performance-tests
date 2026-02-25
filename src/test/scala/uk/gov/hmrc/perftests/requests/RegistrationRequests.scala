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
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object RegistrationRequests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("carf-registration-frontend")
  val route: String       = "/register-for-carf"
  val baseUrlAuth: String = baseUrlFor("auth-frontend")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .check(status.is(200))

  val postAuthLoginPage: HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("credentialStrength", "strong")
      .formParam("excludeGnapToken", "false")
      .formParam("confidenceLevel", "50")
      .formParam("credentialRole", "User")
      .formParam("additionalInfo.emailVerified", "N/A")
      .formParam("enrolment[4].name", "IR-CT")
      .formParam("enrolment[4].taxIdentifier[0].name", "UTR")
      .formParam("enrolment[4].taxIdentifier[0].value", "12345")
      .formParam("enrolment[4].state", "Activated")
      .formParam("email", "user@test.com")
      .formParam("affinityGroup", "Organisation")
      .formParam("redirectionUrl", baseUrl + route)
      .check(status.is(303))
      .check(
        header("Location")
          .is(baseUrl + route)
          .saveAs("AuthLoginForCRS")
      )

  val getIsThisYourBusinessPage: HttpRequestBuilder =
    http("Get Is This Your Business")
      .get(baseUrl + route + "/register/is-this-your-business")
      .check(status.is(200))
//      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val getIndexPage: HttpRequestBuilder =
    http("Get Index Page")
      .get(baseUrl + route)
      .check(status.is(303))
//      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIsThisYourBusinessPage: HttpRequestBuilder =
    http("Post Is This Your Business")
      .post(baseUrl + route + "/register/is-this-your-business")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/your-contact-details").saveAs("YourContactDetails"))

  val postIndividualLoginPage: ChainBuilder       =
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
  val getIndividualRegistrationType: ChainBuilder =
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
