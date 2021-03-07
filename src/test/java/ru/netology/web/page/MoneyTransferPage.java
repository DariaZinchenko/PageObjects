package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class MoneyTransferPage {
    private SelenideElement amountField = $("[data-test-id='amount'] .input__control");
    private SelenideElement cardFrom = $("[data-test-id='from'] .input__control");
    private SelenideElement cardTo = $("[data-test-id='to'] .input__control");
    private SelenideElement buttonPost = $("[data-test-id='action-transfer']");
    private SelenideElement buttonCancel = $("[data-test-id='action-cancel']");

    public MoneyTransferPage() {
        //heading.shouldBe(visible);
    }

    public DashboardPage topUpCardBalance(Integer amount, DataHelper.UserCard card) {
        amountField.sendKeys(Keys.chord(Keys.CONTROL, "A"), Keys.DELETE);
        amountField.setValue(Integer.toString(amount));
        cardFrom.sendKeys(Keys.chord(Keys.CONTROL, "A"), Keys.DELETE);
        cardFrom.setValue(card.getCard());
        buttonPost.click();

        return new DashboardPage();
    }
}
