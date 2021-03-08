package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement cardField;
    private SelenideElement cardButton;

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public MoneyTransferPage clickCardButton(String id) {
        cardField = $("[data-test-id='" + id + "']");
        cardButton = cardField.find("[data-test-id='action-deposit']");
        cardButton.click();
        return new MoneyTransferPage();
    }

    public Integer checkCardBalance(String id) {
        cardField = $("[data-test-id='" + id + "']");
        String text = cardField.getText();
        String amount = text.replaceAll("(.*баланс: )|( р\\.[\\s\\S]*)", "");
        return  Integer.valueOf(amount);
    }
}