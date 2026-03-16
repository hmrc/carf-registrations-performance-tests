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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object OrgRegistrationRequests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("carf-registration-frontend")
  val route: String       = "/register-for-cryptoasset-reporting"
  val baseUrlAuth: String = baseUrlFor("auth-frontend")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .check(status.is(200))

  val postAuthLoginPageOrgAutoMatchedCtUtr: HttpRequestBuilder =
    http("Post Auth login page for Auto matched CT UTR")
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
      .check(header("Location").is(baseUrl + route).saveAs("AuthLoginForCarf"))

  val getIndexPage: HttpRequestBuilder =
    http("Get Index Page")
      .get(baseUrl + route)
      .check(status.is(303))

  val getIsThisYourBusinessPage: HttpRequestBuilder =
    http("Get Is This Your Business Page")
      .get(baseUrl + route + "/register/is-this-your-business")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIsThisYourBusinessPage: HttpRequestBuilder =
    http("Post Is This Your Business Page")
      .post(baseUrl + route + "/register/is-this-your-business")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/your-contact-details").saveAs("YourContactDetails"))

  val getYourContactDetailsPage: HttpRequestBuilder =
    http("Get Your Contact Details Page")
      .get(baseUrl + route + "/register/your-contact-details")
      .check(status.is(200))

  val getContactNamePage: HttpRequestBuilder =
    http("Get Contact Name Page")
      .get(baseUrl + route + "/register/contact-name")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postContactNamePage: HttpRequestBuilder =
    http("Post Contact Name Page")
      .post(baseUrl + route + "/register/contact-name")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Test Team")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/email").saveAs("Email"))

  val getEmailPage: HttpRequestBuilder =
    http("Get Email Page")
      .get(baseUrl + route + "/register/email")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postEmailPage: HttpRequestBuilder =
    http("Post Email Page")
      .post(baseUrl + route + "/register/email")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "carfteam@gmail.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-phone").saveAs("HavePhone"))

  val getHavePhonePage: HttpRequestBuilder =
    http("Get Have Phone Page")
      .get(baseUrl + route + "/register/have-phone")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHavePhonePage: HttpRequestBuilder =
    http("Post Have Phone Page")
      .post(baseUrl + route + "/register/have-phone")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/phone").saveAs("Phone"))

  val getPhonePage: HttpRequestBuilder =
    http("Get Phone Page")
      .get(baseUrl + route + "/register/phone")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postPhonePage: HttpRequestBuilder =
    http("Post Phone Page")
      .post(baseUrl + route + "/register/phone")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-second-contact").saveAs("HaveSecondContact"))

  val getHaveSecondContactPage: HttpRequestBuilder =
    http("Get Have Second Contact Page")
      .get(baseUrl + route + "/register/have-second-contact")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveSecondContactPage: HttpRequestBuilder =
    http("Post Have Second Contact Page")
      .post(baseUrl + route + "/register/have-second-contact")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-name").saveAs("SecondContactName"))

  val getSecondContactNamePage: HttpRequestBuilder =
    http("Get Second Contact Name Page")
      .get(baseUrl + route + "/register/second-contact-name")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactNamePage: HttpRequestBuilder =
    http("Post Second Contact Name Page")
      .post(baseUrl + route + "/register/second-contact-name")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Test Second")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-email").saveAs("SecondContactEmail"))

  val getSecondContactEmailPage: HttpRequestBuilder =
    http("Get Second Contact Email Page")
      .get(baseUrl + route + "/register/second-contact-email")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactEmailPage: HttpRequestBuilder =
    http("Post Second Contact Email Page")
      .post(baseUrl + route + "/register/second-contact-email")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Carftestsecond@gmail.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-have-phone").saveAs("SecondContactHavePhone"))

  val getSecondContactHavePhonePage: HttpRequestBuilder =
    http("Get Second Contact Have Phone Page")
      .get(baseUrl + route + "/register/second-contact-have-phone")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactHavePhonePage: HttpRequestBuilder =
    http("Post Second Contact Have Phone Page")
      .post(baseUrl + route + "/register/second-contact-have-phone")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-phone").saveAs("SecondContactPhone"))

  val getSecondContactPhonePage: HttpRequestBuilder =
    http("Get Second Contact Phone Page")
      .get(baseUrl + route + "/register/second-contact-phone")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactPhonePage: HttpRequestBuilder =
    http("Post Second Contact Phone Page")
      .post(baseUrl + route + "/register/second-contact-phone")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/check-answers").saveAs("CheckYourAnswer"))

  val getCheckAnswerPage: HttpRequestBuilder =
    http("Get Check Answer Page")
      .get(baseUrl + route + "/register/check-answers")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckAnswerPage: HttpRequestBuilder =
    http("Post Check Answer Page")
      .post(baseUrl + route + "/register/check-answers")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/confirm-registration").saveAs("ConfirmRegistration"))

  val getConfirmRegistrationPage: HttpRequestBuilder =
    http("Get Confirm Registration Page")
      .get(baseUrl + route + "/register/confirm-registration")
      .check(status.is(200))
}
