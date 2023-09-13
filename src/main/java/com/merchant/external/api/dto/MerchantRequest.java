package com.merchant.external.api.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class MerchantRequest {
    private final String channel = "0001";
    private String requestTimestamp;
    private String requestUnixTimestamp;
    @Setter(AccessLevel.NONE)
    private String transactionId;
    private final String transactionType = "EXT";

    public void init() {
        this.transactionId = requestUnixTimestamp.substring(2, 10);
    }
}
