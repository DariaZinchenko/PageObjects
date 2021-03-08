package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.DataHelper.AuthInfo;
import ru.netology.web.page.*;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    AuthInfo authInfo;
    DashboardPage dashboardPage;
    Integer firstCardBalance;
    Integer secondCardBalance;

    void singIn(){
        open("http://localhost:9999");
        authInfo = DataHelper.getAuthInfo();
        val loginPage = new LoginPageV2();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    void equalizeCardsBalance(){
        firstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        secondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

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

        firstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        secondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        Integer actualFirstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        Integer actualSecondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

        assertEquals(firstCardBalance + amount, actualFirstCardBalance);
        assertEquals(secondCardBalance - amount, actualSecondCardBalance);
    }

    @Test
    void transferMoneyToFirstCardEqualLimitTest() {
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        Integer amount = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        Integer actualFirstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        Integer actualSecondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

        assertEquals(firstCardBalance + amount, actualFirstCardBalance);
        assertEquals(0, actualSecondCardBalance);
    }

/*    @Test
    void transferMoneyToFirstCardOverLimitTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        secondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());
        Integer amount = dashboardPage.checkSecondCardBalance() + 1;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickFirstCardButton();

        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));


    }*/

    @Test
    void transferMoneyToSecondCardTest() {
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        secondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());
        Integer amount = 1200;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickSecondCardButton();
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));

        Integer actualFirstCardBalance = dashboardPage.checkCardBalance(DataHelper.getFirstCard(authInfo).getId());
        Integer actualSecondCardBalance = dashboardPage.checkCardBalance(DataHelper.getSecondCard(authInfo).getId());

        assertEquals(firstCardBalance - amount, actualFirstCardBalance);
        assertEquals(secondCardBalance + amount, actualSecondCardBalance);
    }
}