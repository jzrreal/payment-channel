package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InterBankTransferResponse extends MandiriResponse {
    private String sourceAccountNo;
    private String beneficiaryAccountNo;
    private String referenceNo;
    private String partnerReferenceNo;
    private String transactionDate;
    private InterBankAmountResponse amount;

    @Data
    public static class InterBankAmountResponse {
        private String value;
        private String currency;
    }
}
