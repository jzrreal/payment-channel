package com.merchant.external.api.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenResponse;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryMerchantRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryResponse;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferRequest;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferResponse;

@Service
public interface MandiriOpenApiService {

    public MerchantResponse<BalanceInquiryResponse> inquiryBalance(BalanceInquiryMerchantRequest inMsg)
        throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException,
        UnsupportedEncodingException, SignatureException;

    public MerchantResponse<InterBankTransferResponse> interbankTransfer(InterBankTransferRequest inMsg)
        throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException,
        UnsupportedEncodingException, SignatureException;

    public MerchantResponse<AccessTokenResponse> requestAccessToken()
        throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException,
        SignatureException, JsonProcessingException;

}
