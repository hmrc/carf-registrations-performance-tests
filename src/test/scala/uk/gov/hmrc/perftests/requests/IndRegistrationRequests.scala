/*
 * Copyright 2026 HM Revenue & Customs
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

object IndRegistrationRequests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("carf-registration-frontend")
  val route: String       = "/register-for-cryptoasset-reporting"
  val baseUrlAuth: String = baseUrlFor("auth-frontend")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val postAuthLoginPageIndividualWithNino: HttpRequestBuilder =
    http("Post Auth login page for Individual with NINO")
      .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("credentialStrength", "strong")
      .formParam("excludeGnapToken", "false")
      .formParam("confidenceLevel", "50")
      .formParam("credentialRole", "User")
      .formParam("email", "user@test.com")
      .formParam("affinityGroup", "Individual")
      .formParam("redirectionUrl", baseUrl + route)
      .check(status.is(303))
      .check(header("Location").is(baseUrl + route).saveAs("AuthLoginForCarf"))

  val getIndividualRegistrationType: HttpRequestBuilder =
    http("Get Individual Registration Type Page")
      .get(baseUrl + route + "/register/individual-registration-type")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIndividualRegistrationType: HttpRequestBuilder =
    http("Post Individual Registration Type Page")
      .post(baseUrl + route + "/register/individual-registration-type")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("individualRegistrationType", "IndividualNotConnectedToABusiness")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-ni-number").saveAs("HaveNiNumber"))

  val postIndividualRegistrationTypeWithPrint: ChainBuilder =
    exec(postIndividualRegistrationType)
      .exec { session =>
        println("POST URL *** " + baseUrl + route + "/register/individual-registration-type")
        println(
          "Request Body *** csrfToken=" +
            session("csrfToken").asOption[String].getOrElse("NOT FOUND") +
            ", value=IndividualNotConnectedToABusiness"
        )
        println("Actual Location Header *** " + session("actualLocation").asOption[String].getOrElse("NOT FOUND"))
        println("Expected Location Header *** " + route + "/register/have-ni-number")
        println("Response Body *** " + session("responseBody").asOption[String].getOrElse("NOT FOUND"))
        session
      }

  val getHaveNiNumberPage: HttpRequestBuilder =
    http("Get Have Ni Number Page")
      .get(baseUrl + route + "/register/have-ni-number")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveNiNumberPage: HttpRequestBuilder =
    http("Post Have Ni Number Page")
      .post(baseUrl + route + "/register/have-ni-number")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/ni-number").saveAs("NiNumber"))

  val getNiNumberPage: HttpRequestBuilder =
    http("Get Ni Number Page")
      .get(baseUrl + route + "/register/ni-number")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postNiNumberPage: HttpRequestBuilder =
    http("Post Ni Number Page")
      .post(baseUrl + route + "/register/ni-number")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "PB200807C")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/name").saveAs("Name"))

  val getNamePage: HttpRequestBuilder =
    http("Get Name Page")
      .get(baseUrl + route + "/register/name")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postNamePage: HttpRequestBuilder =
    http("Post Name Page")
      .post(baseUrl + route + "/register/name")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("firstName", "Carf")
      .formParam("lastName", "Tester")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/date-of-birth").saveAs("DateOfBirth"))
}
