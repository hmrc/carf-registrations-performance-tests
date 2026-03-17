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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object IndRegistrationRequests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("carf-registration-frontend")
  val route: String       = "/register-for-cryptoasset-reporting"
  val baseUrlAuth: String = baseUrlFor("auth-frontend")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  /*val postAuthLoginPage: HttpRequestBuilder =
    http("Post Auth login page")
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
      .check(header("Location").is(baseUrl + route).saveAs("AuthLoginForCarf"))*/

  val getIndividualRegistrationType: HttpRequestBuilder =
    http("Get Individual Registration Type Page")
      .get(baseUrl + route + "/register/individual-registration-type")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  def postIndividualRegistrationTypePage(answer: String): HttpRequestBuilder = {
    val registrationType = if (answer == "ind") "IndividualNotConnectedToABusiness" else "IndividualSoleTrader"
    val expectedRedirect = if (answer == "ind") route + "/register/have-ni-number" else route + "/register/registered-address-in-uk"
    val redirectPage = if (answer == "ind") "HaveNiNumber" else "RegisteredAddressInUk"

    http("Post Individual Registration Type Page")
      .post(baseUrl + route + "/register/individual-registration-type")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("individualRegistrationType", registrationType)
      .check(status.is(303))
      .check(header("Location").is(expectedRedirect).saveAs(redirectPage))
  }

  val getHaveNiNumberPage: HttpRequestBuilder =
    http("Get Have Ni Number Page")
      .get(baseUrl + "#{HaveNiNumber}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveNiNumberPage: HttpRequestBuilder =
    http("Post Have Ni Number Page")
      .post(baseUrl + "#{HaveNiNumber}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/ni-number").saveAs("NiNumber"))

  val getNiNumberPage: HttpRequestBuilder =
    http("Get Ni Number Page")
      .get(baseUrl + "#{NiNumber}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postNiNumberPage: HttpRequestBuilder =
    http("Post Ni Number Page")
      .post(baseUrl + "#{NiNumber}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "PB200807C")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/name").saveAs("Name"))

  val getNamePage: HttpRequestBuilder =
    http("Get Name Page")
      .get(baseUrl + "#{Name}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postNamePage: HttpRequestBuilder =
    http("Post Name Page")
      .post(baseUrl + "#{Name}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("firstName", "Carf")
      .formParam("lastName", "Tester")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/date-of-birth").saveAs("DateOfBirth"))

  val getDateOfBirthPage: HttpRequestBuilder =
    http("Get Date of Birth Page")
      .get(baseUrl + "#{DateOfBirth}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postDateOfBirthPage: HttpRequestBuilder =
    http("Post Date of Birth Page")
      .post(baseUrl + "#{DateOfBirth}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value.day", "2")
      .formParam("value.month", "2")
      .formParam("value.year", "2022")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/identity-confirmed").saveAs("IdentityConfirmed"))

  val getIdentityConfirmedPage: HttpRequestBuilder =
    http("Get Identity Confirmed Page")
      .get(baseUrl + "#{IdentityConfirmed}")
      .check(status.is(200))

  val getIndividualEmailPage: HttpRequestBuilder =
    http("Get Individual Email Page")
      .get(baseUrl + route + "/register/individual-email")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIndividualEmailPage: HttpRequestBuilder =
    http("Post Individual Email Page")
      .post(baseUrl + route + "/register/individual-email")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "John.doe@example.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/individual-have-phone").saveAs("IndividualHavePhone"))

  val getIndividualHavePhonePage: HttpRequestBuilder =
    http("Get Individual Have Phone Page")
      .get(baseUrl + "#{IndividualHavePhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIndividualHavePhonePage: HttpRequestBuilder =
    http("Post Individual Have Phone Page")
      .post(baseUrl + "#{IndividualHavePhone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/individual-phone").saveAs("IndividualPhone"))

  val getIndividualPhonePage: HttpRequestBuilder =
    http("Get Individual Phone Page")
      .get(baseUrl + "#{IndividualPhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIndividualPhonePage: HttpRequestBuilder =
    http("Post Individual Phone Page")
      .post(baseUrl + "#{IndividualPhone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/check-answers").saveAs("CheckAnswers"))

  val getCheckAnswersPage: HttpRequestBuilder =
    http("Get Check Answers Page")
      .get(baseUrl + "#{CheckAnswers}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckAnswersPage: HttpRequestBuilder =
    http("Post Check Answers Page")
      .post(baseUrl + "#{CheckAnswers}")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/confirm-registration").saveAs("ConfirmRegistration"))

}
