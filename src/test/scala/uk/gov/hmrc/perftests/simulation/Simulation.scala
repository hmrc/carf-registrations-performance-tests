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

package uk.gov.hmrc.perftests.simulation

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.requests.IndRegistrationRequests._
import uk.gov.hmrc.perftests.requests.OrgRegistrationRequests._
import uk.gov.hmrc.perftests.requests._

class Simulation extends PerformanceTestRunner {

  setup("OrgWithCtUtrAutoMatched", "Organisation with Id (CT-UTR) Automatched Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("automatched"),
    getIndexPage,
    getIsThisYourBusinessPage,
    postIsThisYourBusinessPage("org"),
    getYourContactDetailsPage,
    getContactNamePage,
    postContactNamePage,
    getEmailPage,
    postEmailPage,
    getHavePhonePage,
    postHavePhonePage,
    getPhonePage,
    postPhonePage,
    getHaveSecondContactPage,
    postHaveSecondContactPage,
    getSecondContactNamePage,
    postSecondContactNamePage,
    getSecondContactEmailPage,
    postSecondContactEmailPage,
    getSecondContactHavePhonePage,
    postSecondContactHavePhonePage,
    getSecondContactPhonePage,
    postSecondContactPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  setup("OrgWithCtUtrNonAutoMatched", "Organisation with Id (CT-UTR) Non-automatched Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("otherOrg"),
    getIndexPage,
    getOrganisationRegistrationTypePage,
    postOrganisationRegistrationTypePage,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage,
    getHaveUtrPage,
    postHaveUtrPage(true),
    getUtrPage,
    postUtrPage("org"),
    getBusinessNamePage,
    postBusinessNamePage,
    getIsThisYourBusinessPage,
    postIsThisYourBusinessPage("org"),
    getYourContactDetailsPage,
    getContactNamePage,
    postContactNamePage,
    getEmailPage,
    postEmailPage,
    getHavePhonePage,
    postHavePhonePage,
    getPhonePage,
    postPhonePage,
    getHaveSecondContactPage,
    postHaveSecondContactPage,
    getSecondContactNamePage,
    postSecondContactNamePage,
    getSecondContactEmailPage,
    postSecondContactEmailPage,
    getSecondContactHavePhonePage,
    postSecondContactHavePhonePage,
    getSecondContactPhonePage,
    postSecondContactPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  setup("OrgWithoutID", "Organisation without ID (CT-UTR) Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("otherOrg"),
    getIndexPage,
    getOrganisationRegistrationTypePage,
    postOrganisationRegistrationTypePage,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage,
    getHaveUtrPage,
    postHaveUtrPage(false),
    getBusinessWithoutIDBusinessName,
    postBusinessWithoutIDBusinessName,
    getHaveTradingName,
    postHaveTradingName,
    getTradingName,
    postTradingName,
    getBusinessAddress,
    postBusinessAddress,
    getYourContactDetailsPage,
    getContactNamePage,
    postContactNamePage,
    getEmailPage,
    postEmailPage,
    getHavePhonePage,
    postHavePhonePage,
    getPhonePage,
    postPhonePage,
    getHaveSecondContactPage,
    postHaveSecondContactPage,
    getSecondContactNamePage,
    postSecondContactNamePage,
    getSecondContactEmailPage,
    postSecondContactEmailPage,
    getSecondContactHavePhonePage,
    postSecondContactHavePhonePage,
    getSecondContactPhonePage,
    postSecondContactPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  setup("IndWithNino", "Individual with Nino Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("individual"),
    getIndexPage,
    getIndividualRegistrationType,
    postIndividualRegistrationTypePage("ind"),
    getHaveNiNumberPage,
    postHaveNiNumberPage(true),
    getNiNumberPage,
    postNiNumberPage,
    getNamePage,
    postNamePage,
    getDateOfBirthPage,
    postDateOfBirthPage,
    getIdentityConfirmedPage,
    getIndividualEmailPage,
    postIndividualEmailPage,
    getIndividualHavePhonePage,
    postIndividualHavePhonePage,
    getIndividualPhonePage,
    postIndividualPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  setup("IndWithoutNinoInUK", "Individual without Nino UK Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("individual"),
    getIndexPage,
    getIndividualRegistrationType,
    postIndividualRegistrationTypePage("ind"),
    getHaveNiNumberPage,
    postHaveNiNumberPage(false),
    getIndividualWithoutIdNamePage,
    postIndividualWithoutIdNamePage,
    getIndividualWithoutIdDateOfBirthPage,
    postIndividualWithoutIdDateOfBirthPage,
    getIndividualWithoutIdWhereDoYouLivePage,
    postIndividualWithoutIdWhereDoYouLivePage(true),
    getIndividualWithoutIdFindAddressPage,
    postIndividualWithoutIdFindAddressPage, //TODO: Check if postcode can be picked dynamically based on env
    getIndividualWithoutIdReviewAddressPage,
    getIndividualEmailPage,
    postIndividualEmailPage,
    getIndividualHavePhonePage,
    postIndividualHavePhonePage,
    getIndividualPhonePage,
    postIndividualPhonePage/*,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage*/
  )

  setup("IndWithoutNinoOutsideUK", "Individual without Nino Outside UK Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("individual"),
    getIndexPage,
    getIndividualRegistrationType,
    postIndividualRegistrationTypePage("ind"),
    getHaveNiNumberPage,
    postHaveNiNumberPage(false),
    getIndividualWithoutIdNamePage,
    postIndividualWithoutIdNamePage,
    getIndividualWithoutIdDateOfBirthPage,
    postIndividualWithoutIdDateOfBirthPage,
    getIndividualWithoutIdWhereDoYouLivePage,
    postIndividualWithoutIdWhereDoYouLivePage(false),
    getIndividualWithoutIdAddressNonUk,
    postIndividualWithoutIdAddressNonUk,
    getIndividualEmailPage,
    postIndividualEmailPage,
    getIndividualHavePhonePage,
    postIndividualHavePhonePage,
    getIndividualPhonePage,
    postIndividualPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  setup("SoleTraderWithUtr", "Sole Trader with Utr Journey").withChainedActions(
    getAuthLoginPage,
    postAuthLoginPage("individual"),
    getIndexPage,
    getIndividualRegistrationType,
    postIndividualRegistrationTypePage("st"),
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage,
    getHaveUtrPage,
    postHaveUtrPage(true),
    getUtrPage,
    postUtrPage("st"),
    getYourNamePage,
    postYourNamePage,
    getIsThisYourBusinessPage,
    postIsThisYourBusinessPage("st"),
    getIndividualEmailPage,
    postIndividualEmailPage,
    getIndividualHavePhonePage,
    postIndividualHavePhonePage,
    getIndividualPhonePage,
    postIndividualPhonePage,
    getCheckAnswerPage,
    postCheckAnswerPage,
    getConfirmRegistrationPage
  )

  runSimulation()
}
