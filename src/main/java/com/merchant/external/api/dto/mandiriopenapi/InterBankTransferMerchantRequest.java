package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;

@Data
public class InterBankTransferMerchantRequest {
    private String sourceAccountNo;
        private String beneficiaryAccountNo;
        private String beneficiaryAccountName;
        private String beneficiaryBankCode;
        private String beneficiaryBankName;
        private InterBankAmountRequest amount;

        @Data
        public static class InterBankAmountRequest{
            private String value;
            private String currency;
        }

        private String transactionDate;
        private String partnerReferenceNo;
        private String beneficiaryEmail;
        private String beneficiaryAddress;
        private InterBankTransferRequestAdditionalInfo additionalInfo;

        @Data
        public static class InterBankTransferRequestAdditionalInfo {
            private String switcher;
        }
        private String remark;
}
