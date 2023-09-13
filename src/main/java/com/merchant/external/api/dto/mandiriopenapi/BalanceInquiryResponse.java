package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BalanceInquiryResponse extends MandiriResponse {
    private String accountNo;
    private String name;
    private String status;
    private Double availableBalance;
    private String currency;
    private Double holdAmound;
    private Double ledgerBalance;
}
