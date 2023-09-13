package com.merchant.external.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenRequest;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenResponse;
import com.merchant.external.api.service.MandiriOpenApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/mandiri/auth/")
@RequiredArgsConstructor
public class AccessTokenController {

    private final MandiriOpenApiService mandiriOpenApiService;
    @PostMapping("access-token")
    public ResponseEntity<Object> getAccessToken(AccessTokenRequest inMsg) throws Exception {
        MerchantResponse<AccessTokenResponse> outMsg = new MerchantResponse<>();
        outMsg = mandiriOpenApiService.requestAccessToken();
        return ResponseEntity.ok(outMsg);
    }
}
