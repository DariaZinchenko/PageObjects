package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement firstCardField = $("[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0']");
    private SelenideElement secondCardField = $("[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d']");
    private SelenideElement firstCardButton = firstCardField.find("[data-test-id='action-deposit']");
    private SelenideElement secondCardButton = secondCardField.find("[data-test-id='action-deposit']");

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public MoneyTransferPage clickFirstCardButton() {
        firstCardButton.click();
        return new MoneyTransferPage();
    }

    public MoneyTransferPage clickSecondCardButton() {
        secondCardButton.click();
        return new MoneyTransferPage();
    }

    public Integer checkFirstCardBalance() {
        String text = firstCardField.getText();
        String amount = text.replaceAll("(.*баланс: )|( р\\.[\\s\\S]*)", "");
        return  Integer.valueOf(amount);
    }

    public Integer checkSecondCardBalance() {
        String text = secondCardField.getText();
        String amount = text.replaceAll("(.*баланс: )|( р\\.[\\s\\S]*)", "");
        return  Integer.valueOf(amount);
    }
}