package com.merchant.external.api.service;

import org.springframework.stereotype.Service;

import com.merchant.external.api.dto.MerchantResponse;

@Service
public interface ExternalCashFlowService {
    public MerchantResponse<Object> checkPaymentStatus();
    public MerchantResponse<Object> transferToClient();
}
