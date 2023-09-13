package com.merchant.external.api.dto;

import lombok.Data;

@Data
public class MerchantResponse <T> {
    private String responseCode;
    private String responseMessage;
    private T transactionDetail;

    public MerchantResponse<T> successTemplate(T responseBody) {
        MerchantResponse<T> outMsg = new MerchantResponse<>();
        outMsg.setResponseCode("00");
        outMsg.setResponseMessage("Transaction Success");
        outMsg.setTransactionDetail(responseBody);
        return outMsg;
    }

    public MerchantResponse<T> failedTemplate() {
        MerchantResponse<T> outMsg = new MerchantResponse<>();
        outMsg.setResponseCode("01");
        outMsg.setResponseMessage("Transaction Failed");
        return outMsg;
    }
}
