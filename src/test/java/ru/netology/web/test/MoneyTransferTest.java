package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.DataHelper.AuthInfo;
import ru.netology.web.page.*;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    private AuthInfo authInfo = DataHelper.getAuthInfo();
    private DashboardPage dashboardPage;
    private Integer firstCardBalance;
    private Integer secondCardBalance;
    private String firstCardId = DataHelper.getFirstCard(authInfo).getId();
    private String secondCardId = DataHelper.getSecondCard(authInfo).getId();

    private void singIn(){
        open("http://localhost:9999");
        //authInfo = DataHelper.getAuthInfo();
        val loginPage = new LoginPageV2();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    private void equalizeCardsBalance(){
        firstCardBalance = dashboardPage.checkCardBalance(firstCardId);
        secondCardBalance = dashboardPage.checkCardBalance(secondCardId);

        if (firstCardBalance > secondCardBalance){
            Integer amount = (firstCardBalance + secondCardBalance)/2 - secondCardBalance;
            val moneyTransferPage = dashboardPage.clickCardButton(secondCardId);
            moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));
        }

        if (firstCardBalance < secondCardBalance){
            Integer amount = (secondCardBalance + firstCardBalance)/2 - firstCardBalance;
            val moneyTransferPage = dashboardPage.clickCardButton(firstCardId);
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

        firstCardBalance = dashboardPage.checkCardBalance(firstCardId);
        secondCardBalance = dashboardPage.checkCardBalance(secondCardId);

        MoneyTransferPage moneyTransferPage = dashboardPage.clickCardButton(firstCardId);
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));

        assertEquals(firstCardBalance + amount, dashboardPage.checkCardBalance(firstCardId));
        assertEquals(secondCardBalance - amount, dashboardPage.checkCardBalance(secondCardId));
    }

    @Test
    void transferMoneyToFirstCardEqualLimitTest() {
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(firstCardId);
        Integer amount = dashboardPage.checkCardBalance(secondCardId);

        MoneyTransferPage moneyTransferPage = dashboardPage.clickCardButton(firstCardId);
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));
        
        assertEquals(firstCardBalance + amount, dashboardPage.checkCardBalance(firstCardId));
        assertEquals(0, dashboardPage.checkCardBalance(secondCardId));
    }

/*    @Test
    void transferMoneyToFirstCardOverLimitTest() {
        AuthInfo authInfo = DataHelper.getAuthInfo();
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(firstCardId);
        secondCardBalance = dashboardPage.checkCardBalance(secondCardId);
        Integer amount = dashboardPage.checkSecondCardBalance() + 1;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickCardButton(firstCardId);

        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getSecondCard(authInfo));


    }*/

    @Test
    void transferMoneyToSecondCardTest() {
        singIn();
        equalizeCardsBalance();

        firstCardBalance = dashboardPage.checkCardBalance(firstCardId);
        secondCardBalance = dashboardPage.checkCardBalance(secondCardId);
        Integer amount = 1200;

        MoneyTransferPage moneyTransferPage = dashboardPage.clickCardButton(secondCardId);
        dashboardPage = moneyTransferPage.topUpCardBalance(amount, DataHelper.getFirstCard(authInfo));
        
        assertEquals(firstCardBalance - amount, dashboardPage.checkCardBalance(firstCardId));
        assertEquals(secondCardBalance + amount, dashboardPage.checkCardBalance(secondCardId));
    }
}