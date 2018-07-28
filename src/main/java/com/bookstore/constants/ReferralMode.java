package com.bookstore.constants;

public enum ReferralMode {
    EMAIL("email"), FACEBOOK("facebook"), TWITTER("twitter");

    private String value;

    ReferralMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
