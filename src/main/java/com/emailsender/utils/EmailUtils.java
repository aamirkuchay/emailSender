package com.emailsender.utils;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + " ,\n\n Your New Account has been created.Please Click the link below to verify your account. \n\n "
                + getVerificationUrl(host,token) + "\n\n The support Team";
    }

    public static String getVerificationUrl(String host, String token) {
        return host + "/user?token=" + token;
    }
}
