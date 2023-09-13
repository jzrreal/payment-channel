package com.merchant.external.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.merchant.external.api.dto.MerchantResponse;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> generalException(Exception ex) {
        MerchantResponse<Object> outMsg = new MerchantResponse<>();
        outMsg.setResponseCode("01");
        outMsg.setResponseMessage("TRANSACTION FAILED");
        outMsg.setTransactionDetail(ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(outMsg);
    }
}
