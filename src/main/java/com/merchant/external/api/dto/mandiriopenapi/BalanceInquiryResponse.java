package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;

@Data
public class BalanceInquiryResponse {
    private Integer responseCode;
    private String responseMessage;
    private String accountNo;
    private String name;
    private String status;
    private Double availableBalance;
    private String currency;
    private Double holdAmound;
    private Double ledgerBalance;
}
