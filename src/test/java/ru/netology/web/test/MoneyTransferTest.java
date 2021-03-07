package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.*;
import ru.netology.web.page.LoginPageV2;
import ru.netology.web.data.DataHelper.AuthInfo;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    DashboardPage singIn(AuthInfo authInfo){
        open("http://localhost:9999");
        val loginPage = new LoginPageV2();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
        return dashboardPage;
    }

    @ParameterizedTest(name = "{index} {0}")
    //@CsvSource({"amount = 0, 0",
    @CsvSource({"amount = 1, 1",
      "amount = 100, 100"})
    void transferMoneyToFirstCardTest(String testName, Integer amount) {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = singIn(authInfo);

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        assertEquals(firstCardBalance + amount, dashboardPage.checkFirstCardBalance());
        assertEquals(secondCardBalance - amount, dashboardPage.checkSecondCardBalance());

        moneyTransferPage = dashboardPage.clickSecondCardButton();
        moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));//возвращаем перевод
    }

    @Test
    void transferMoneyToFirstCardEqualLimitTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = singIn(authInfo);

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer amount = dashboardPage.checkSecondCardBalance();

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        assertEquals(firstCardBalance + amount, dashboardPage.checkFirstCardBalance());
        assertEquals(200, dashboardPage.checkSecondCardBalance());

        moneyTransferPage = dashboardPage.clickSecondCardButton();
        moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));//возвращаем перевод
    }

/*    @Test
    void transferMoneyToFirstCardOverLimitTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = singIn(authInfo);

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();
        Integer amount = dashboardPage.checkSecondCardBalance() + 1;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));


    }*/

    @Test
    void transferMoneyToSecondCardTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = singIn(authInfo);

        Integer firstCardBalance = dashboardPage.checkFirstCardBalance();
        Integer secondCardBalance = dashboardPage.checkSecondCardBalance();
        Integer amount = 1200;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickSecondCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));

        assertEquals(firstCardBalance - amount, dashboardPage.checkFirstCardBalance());
        assertEquals(secondCardBalance + amount, dashboardPage.checkSecondCardBalance());

        moneyTransferPage = dashboardPage.clickFirstCardButton();
        moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));//возвращаем перевод
    }
}