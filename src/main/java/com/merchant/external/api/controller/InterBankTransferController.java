package com.merchant.external.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferRequest;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferResponse;
import com.merchant.external.api.service.MandiriOpenApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/mandiri")
@RequiredArgsConstructor
public class InterBankTransferController {
    
    private final MandiriOpenApiService mandiriOpenApiService;

    @PostMapping("interbank-transfer")
    public ResponseEntity<Object> interbankTransfer(@RequestBody InterBankTransferRequest inMsg) throws Exception {
        MerchantResponse<InterBankTransferResponse> outMsg = new MerchantResponse<>();
        outMsg = mandiriOpenApiService.interbankTransfer(inMsg);
        return ResponseEntity.ok(outMsg);
    }
}
