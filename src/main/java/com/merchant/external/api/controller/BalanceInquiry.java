package com.merchant.external.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryMerchantRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryResponse;
import com.merchant.external.api.service.MandiriOpenApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class BalanceInquiry {

    private final MandiriOpenApiService mandiriOpenApiService;

    @PostMapping("mandiri/balance-inquiry")
    public ResponseEntity<Object> mandiriBalanceInquiry(@RequestBody BalanceInquiryMerchantRequest inMsg) throws Exception {
        MerchantResponse<BalanceInquiryResponse> outMsg = mandiriOpenApiService.inquiryBalance(inMsg);
        return ResponseEntity.ok(outMsg);
    }
}
