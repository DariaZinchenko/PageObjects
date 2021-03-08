package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.*;
import ru.netology.web.data.DataHelper.AuthInfo;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    AuthInfo authInfo;
    DashboardPage dashboardPage;

    void singIn(){
        open("http://localhost:9999");
        authInfo = DataHelper.getAuthInfo();
        val loginPage = new LoginPageV2();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    void equalizeCardsBalance(){
        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();

        if (firstCardBalance > secondCardBalance){
            Integer amount = (firstCardBalance + secondCardBalance)/2 - secondCardBalance;
            val moneyTransferPage = dashboardPage.clickSecondCardButton();
            moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));
        }

        if (firstCardBalance < secondCardBalance){
            Integer amount = (secondCardBalance + firstCardBalance)/2 - firstCardBalance;
            val moneyTransferPage = dashboardPage.clickFirstCardButton();
            moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));
        }
    }

    @ParameterizedTest(name = "{index} {0}")
    //@CsvSource({"amount = 0, 0",
    @CsvSource({"amount = 1, 1",
      "amount = 100, 100"})
    void transferMoneyToFirstCardTest(String testName, Integer amount) {
        singIn();
        equalizeCardsBalance();

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        assertEquals(firstCardBalance + amount, dashboardPage.checkFirstCardBalance());
        assertEquals(secondCardBalance - amount, dashboardPage.checkSecondCardBalance());
    }

    @Test
    void transferMoneyToFirstCardEqualLimitTest() {
        singIn();
        equalizeCardsBalance();

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer amount = dashboardPage.checkSecondCardBalance();

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        assertEquals(firstCardBalance + amount, dashboardPage.checkFirstCardBalance());
        assertEquals(0, dashboardPage.checkSecondCardBalance());
    }

/*    @Test
    void transferMoneyToFirstCardOverLimitTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        singIn();
        equalizeCardsBalance();

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();
        Integer amount = dashboardPage.checkSecondCardBalance() + 1;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));


    }*/

    @Test
    void transferMoneyToSecondCardTest() {
        singIn();
        equalizeCardsBalance();

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();
        Integer amount = 1200;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickSecondCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));

        assertEquals(firstCardBalance - amount, dashboardPage.checkFirstCardBalance());
        assertEquals(secondCardBalance + amount, dashboardPage.checkSecondCardBalance());
    }
}