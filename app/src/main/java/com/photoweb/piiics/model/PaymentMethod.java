package com.photoweb.piiics.model;

import android.support.annotation.NonNull;

public class PaymentMethod {

    @NonNull
    private String identifier;
    
    @NonNull
    private String paymentName;

    @NonNull
    private String paymentMethodInfos;

    private boolean visibility;
    private boolean defaultPayment;
    
    public PaymentMethod(@NonNull String identifier, @NonNull String paymentName, @NonNull String paymentMethodInfos) {
        this.identifier = identifier;
        this.paymentName = paymentName;
        this.paymentMethodInfos = paymentMethodInfos;
        this.visibility = true;
        this.defaultPayment = false;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(boolean defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @NonNull
    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(@NonNull String paymentName) {
        this.paymentName = paymentName;
    }

    @NonNull
    public String getPaymentMethodInfos() {
        return paymentMethodInfos;
    }

    public void setPaymentMethodInfos(@NonNull String PaymentMethodInfos) {
        this.paymentMethodInfos = PaymentMethodInfos;
    }

}
