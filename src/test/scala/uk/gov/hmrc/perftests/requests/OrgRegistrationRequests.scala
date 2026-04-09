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

  def postAuthLoginPage(userType: String): HttpRequestBuilder = {
    val (requestName, affinityGroup, hasCtUtr) = userType match {
      case "automatched" => ("Post Auth login page for Auto matched Org", "Organisation", true)
      case "otherOrg" => ("Post Auth login page for Non Auto matched Org with CT UTR", "Organisation", false)
      case "individual" => ("Post Auth login page", "Individual", false)
      case _ => ("Post Auth login page", "Individual", false)
    }

    val baseRequest = http(requestName)
      .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("credentialStrength", "strong")
      .formParam("excludeGnapToken", "false")
      .formParam("confidenceLevel", "50")
      .formParam("credentialRole", "User")
      .formParam("additionalInfo.emailVerified", "N/A")
      .formParam("email", "user@test.com")
      .formParam("affinityGroup", affinityGroup)
      .formParam("redirectionUrl", baseUrl + route)

    val finalRequest = if (hasCtUtr) {
      baseRequest
        .formParam("enrolment[4].name", "IR-CT")
        .formParam("enrolment[4].taxIdentifier[0].name", "UTR")
        .formParam("enrolment[4].taxIdentifier[0].value", "12345")
        .formParam("enrolment[4].state", "Activated")
    } else {
      baseRequest
    }

    finalRequest
      .check(status.is(303))
      .check(header("Location").is(baseUrl + route).saveAs("AuthLoginForCarf"))
  }

  val getIndexPage: HttpRequestBuilder =
    http("Get Index Page")
      .get(baseUrl + route)
      .check(status.is(303))

  val getOrganisationRegistrationTypePage: HttpRequestBuilder =
    http("Get Organisation Registration Type Page")
      .get(baseUrl + route + "/register/organisation-registration-type")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postOrganisationRegistrationTypePage: HttpRequestBuilder =
    http("Post Organisation Registration Type Page")
      .post(baseUrl + route + "/register/organisation-registration-type")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "OrganisationLimitedCompany")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/registered-address-in-uk").saveAs("RegisteredAddressInUk"))

  val getRegisteredAddressInUkPage: HttpRequestBuilder =
    http("Get Registered Address In Uk Page")
      .get(baseUrl + "#{RegisteredAddressInUk}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postRegisteredAddressInUkPage: HttpRequestBuilder =
    http("Post Registered Address In Uk Page")
      .post(baseUrl + "#{RegisteredAddressInUk}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "false")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-utr").saveAs("HaveUtr"))

  val getHaveUtrPage: HttpRequestBuilder =
    http("Get Have UTR Page")
      .get(baseUrl + "#{HaveUtr}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  def postHaveUtrPage(answer: Boolean): HttpRequestBuilder = {
    val expectedRedirect = if (answer) route + "/register/utr" else route + "/register/business-without-id/business-name"
    val redirectPage = if (answer) "Utr" else "BusinessWithoutIDBusinessName"

    http("Post Have UTR Page")
      .post(baseUrl + "#{HaveUtr}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", answer)
      .check(status.is(303))
      .check(header("Location").is(expectedRedirect).saveAs(redirectPage))
  }

  val getBusinessWithoutIDBusinessName: HttpRequestBuilder =
    http("Get Business Without ID Business Name Page")
      .get(baseUrl + "#{BusinessWithoutIDBusinessName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postBusinessWithoutIDBusinessName: HttpRequestBuilder =
    http("Post Business Without ID Business Name Page")
      .post(baseUrl + "#{BusinessWithoutIDBusinessName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Disney Land Limited")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/business-without-id/have-trading-name").saveAs("HaveTradingName"))

  val getHaveTradingName: HttpRequestBuilder =
    http("Get Business Without ID Have Trading Name Page")
      .get(baseUrl + "#{HaveTradingName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveTradingName: HttpRequestBuilder =
    http("Post Business Without ID Have Trading Name Page")
      .post(baseUrl + "#{HaveTradingName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/business-without-id/trading-name").saveAs("TradingName"))

  val getTradingName: HttpRequestBuilder =
    http("Get Business Without ID Trading Name Page")
      .get(baseUrl + "#{TradingName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postTradingName: HttpRequestBuilder =
    http("Post Business Without ID Trading Name Page")
      .post(baseUrl + "#{TradingName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Disney Land Limited")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/business-without-id/business-address").saveAs("BusinessAddress"))

  val getBusinessAddress: HttpRequestBuilder =
    http("Get Business Address Page")
      .get(baseUrl + "#{BusinessAddress}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postBusinessAddress: HttpRequestBuilder =
    http("Post Business Address Page")
      .post(baseUrl + "#{BusinessAddress}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("addressLine1", "Line 1")
      .formParam("townOrCity", "Fantasy Town")
      .formParam("country", "AL")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/your-contact-details").saveAs("YourContactDetails"))

  val getUtrPage: HttpRequestBuilder =
    http("Get UTR Page")
      .get(baseUrl + "#{Utr}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  def postUtrPage(answer: String): HttpRequestBuilder = {
    val expectedRedirect = if (answer == "org") route + "/register/business-name" else route + "/register/your-name"
    val redirectPage = if (answer == "org") "BusinessName" else "YourName"

    http("Post UTR Page")
      .post(baseUrl + "#{Utr}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890k")
      .check(status.is(303))
      .check(header("Location").is(expectedRedirect).saveAs(redirectPage))
  }

  val getBusinessNamePage: HttpRequestBuilder =
    http("Get Business Name Page")
      .get(baseUrl + "#{BusinessName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postBusinessNamePage: HttpRequestBuilder =
    http("Post Business Name Page")
      .post(baseUrl + "#{BusinessName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Test company")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/is-this-your-business").saveAs("IsThisYourBusiness"))

  val getYourNamePage: HttpRequestBuilder =
    http("Get Your Name Page")
      .get(baseUrl + "#{YourName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postYourNamePage: HttpRequestBuilder =
    http("Post Your Name Page")
      .post(baseUrl + "#{YourName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("firstName", "Carf")
      .formParam("lastName", "Tester")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/is-this-your-business").saveAs("IsThisYourBusiness"))

  val getIsThisYourBusinessPage: HttpRequestBuilder =
    http("Get Is This Your Business Page")
      .get(baseUrl + route + "/register/is-this-your-business")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  def postIsThisYourBusinessPage(answer: String): HttpRequestBuilder = {
    val expectedRedirect = if (answer == "org") route + "/register/your-contact-details" else route + "/register/individual-email"
    val redirectPage = if (answer == "org") "YourContactDetails" else "IndividualEmailPage"

    http("Post Is This Your Business Page")
      .post(baseUrl + route + "/register/is-this-your-business")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(expectedRedirect).saveAs(redirectPage))
  }

  val getYourContactDetailsPage: HttpRequestBuilder =
    http("Get Your Contact Details Page")
      .get(baseUrl + "#{YourContactDetails}")
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
      .get(baseUrl + "#{Email}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postEmailPage: HttpRequestBuilder =
    http("Post Email Page")
      .post(baseUrl + "#{Email}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "carfteam@gmail.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-phone").saveAs("HavePhone"))

  val getHavePhonePage: HttpRequestBuilder =
    http("Get Have Phone Page")
      .get(baseUrl + "#{HavePhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHavePhonePage: HttpRequestBuilder =
    http("Post Have Phone Page")
      .post(baseUrl + "#{HavePhone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/phone").saveAs("Phone"))

  val getPhonePage: HttpRequestBuilder =
    http("Get Phone Page")
      .get(baseUrl + "#{Phone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postPhonePage: HttpRequestBuilder =
    http("Post Phone Page")
      .post(baseUrl + "#{Phone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-second-contact").saveAs("HaveSecondContact"))

  val getHaveSecondContactPage: HttpRequestBuilder =
    http("Get Have Second Contact Page")
      .get(baseUrl + "#{HaveSecondContact}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveSecondContactPage: HttpRequestBuilder =
    http("Post Have Second Contact Page")
      .post(baseUrl + "#{HaveSecondContact}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-name").saveAs("SecondContactName"))

  val getSecondContactNamePage: HttpRequestBuilder =
    http("Get Second Contact Name Page")
      .get(baseUrl + "#{SecondContactName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactNamePage: HttpRequestBuilder =
    http("Post Second Contact Name Page")
      .post(baseUrl + "#{SecondContactName}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Test Second")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-email").saveAs("SecondContactEmail"))

  val getSecondContactEmailPage: HttpRequestBuilder =
    http("Get Second Contact Email Page")
      .get(baseUrl + "#{SecondContactEmail}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactEmailPage: HttpRequestBuilder =
    http("Post Second Contact Email Page")
      .post(baseUrl + "#{SecondContactEmail}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "Carftestsecond@gmail.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-have-phone").saveAs("SecondContactHavePhone"))

  val getSecondContactHavePhonePage: HttpRequestBuilder =
    http("Get Second Contact Have Phone Page")
      .get(baseUrl + "#{SecondContactHavePhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactHavePhonePage: HttpRequestBuilder =
    http("Post Second Contact Have Phone Page")
      .post(baseUrl + "#{SecondContactHavePhone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-phone").saveAs("SecondContactPhone"))

  val getSecondContactPhonePage: HttpRequestBuilder =
    http("Get Second Contact Phone Page")
      .get(baseUrl + "#{SecondContactPhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactPhonePage: HttpRequestBuilder =
    http("Post Second Contact Phone Page")
      .post(baseUrl + "#{SecondContactPhone}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/check-answers").saveAs("CheckAnswers"))

  val getCheckAnswerPage: HttpRequestBuilder =
    http("Get Check Answer Page")
      .get(baseUrl + "#{CheckAnswers}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckAnswerPage: HttpRequestBuilder =
    http("Post Check Answer Page")
      .post(baseUrl + "#{CheckAnswers}")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/confirm-registration").saveAs("ConfirmRegistration"))

  val getConfirmRegistrationPage: HttpRequestBuilder =
    http("Get Confirm Registration Page")
      .get(baseUrl + route + "/register/confirm-registration")
      .check(status.is(200))
}
