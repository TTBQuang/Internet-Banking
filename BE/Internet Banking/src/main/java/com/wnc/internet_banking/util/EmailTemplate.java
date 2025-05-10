package com.wnc.internet_banking.util;

public class EmailTemplate {
    public static String passwordReset(String fullName, String otpCode) {
        return String.format("""
                <html>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                        <p>Dear %s,</p>
                
                        <p>We received a request to reset your password. Use the OTP code below to proceed:</p>
                
                        <p style="font-size: 24px; font-weight: bold; color: #2E86C1;">%s</p>
                
                        <p>This code will expire in 5 minutes. If you did not request this, please ignore this email.</p>
                    </body>
                </html>
                """, fullName, otpCode);
    }

    public static String confirmTransaction(String fullName, String otpCode) {
        return String.format("""
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <p>Dear %s,</p>
                
                            <p>A transaction has been initiated from your account. To proceed, please enter the OTP code below to confirm:</p>
                
                            <p style="font-size: 24px; font-weight: bold; color: #28a745;">%s</p>
                
                            <p>This code will expire in 5 minutes. If you did not initiate this transaction, please contact our customer support immediately.</p>
                        </body>
                    </html>
                """, fullName, otpCode);
    }
}
