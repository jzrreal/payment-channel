package com.merchant.external.api.enums;

public enum DateFormat {
    INTERNAL_FORMAT("yyyy-MM-dd HH:mm:ss"),
    MANDIRI_FORMAT("yyyy-MM-dd'T'HH:mm:ssX:00");

    private String value;

    private DateFormat(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
