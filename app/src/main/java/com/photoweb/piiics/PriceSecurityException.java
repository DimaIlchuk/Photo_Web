package com.photoweb.piiics;

/**
 * Created by thomas on 08/08/2017.
 */

public class PriceSecurityException extends Exception {

    public PriceSecurityException() { super(); }
    public PriceSecurityException(String message) { super(message); }
    public PriceSecurityException(String message, Throwable cause) { super(message, cause); }
    public PriceSecurityException(Throwable cause) { super(cause); }


    public static String getErrorTitle() {
        return "Erreur";
    }

    public static String getErrorMessage() {
        return "Une erreur interne est survenue, merci de réessayer plus tard";
    }
}
