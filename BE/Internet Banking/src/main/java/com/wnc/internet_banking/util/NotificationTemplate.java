package com.wnc.internet_banking.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Getter
@AllArgsConstructor
public class NotificationTemplate {
    // Title and content properties
    private final String title;
    private final String content;

    private static String formatCurrency(Double rawAmount) {
        if (rawAmount == null) return "₫0";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        return "₫" + formatter.format(rawAmount);
    }

    public static NotificationTemplate debtReminderCreated(String creditorFullName, Double amount) {
        return new NotificationTemplate(
                "New Debt Payment Request",
                String.format("You have received a new debt payment request of %s from %s. Please review and complete the payment.", formatCurrency(amount), creditorFullName));
    }

    public static NotificationTemplate debtPaymentCompleted(String debtorFullName, Double amount) {
        return new NotificationTemplate(
                "Debt Payment Completed",
                String.format("Debt payment of %s from %s has been completed successfully.", formatCurrency(amount), debtorFullName));
    }

    public static NotificationTemplate debtReminderCancelledToDebtor(String creditorFullName, String content) {
        return new NotificationTemplate(
                "Debt Reminder Cancelled",
                String.format("The debt reminder sent by %s to you has been cancelled. \n Reason: %s", creditorFullName, content));
    }

    public static NotificationTemplate debtReminderCancelledToCreditor(String debtorFullName, String content) {
        return new NotificationTemplate(
                "Debt Reminder Cancelled",
                String.format("The debt reminder sent to %s has been cancelled. \n Reason: %s", debtorFullName, content));
    }
}
