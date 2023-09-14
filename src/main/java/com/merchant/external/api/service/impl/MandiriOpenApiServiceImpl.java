package com.merchant.external.api.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenRequest;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenResponse;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryMerchantRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryResponse;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferRequest;
import com.merchant.external.api.dto.mandiriopenapi.InterBankTransferResponse;
import com.merchant.external.api.enums.DateFormat;
import com.merchant.external.api.service.HttpService;
import com.merchant.external.api.service.MandiriOpenApiService;
import com.merchant.external.api.utilities.CommonUtil;
import com.merchant.external.api.utilities.MandiriUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MandiriOpenApiServiceImpl implements MandiriOpenApiService {

    @Value("${mandiri.private.key}")
    private String privateKey;
    @Value("${mandiri.partner.id}")
    private String partnerId;
    @Value("${mandiri.client.secret}")
    private String clientSecret;
    @Value("${mandiri.client.id}")
    private String clientId;

    @Value("${mandiri.url.base}")
    private String baseUrl;
    @Value("${mandiri.url.access.token}")
    private String accessTokenUrl;
    @Value("${mandiri.url.balance.inquiry}")
    private String balanceInquiryUrl;
    @Value("${mandiri.url.interbank.transfer}")
    private String interbankTransferUrl;

    private final ObjectMapper objectMapper;

    @Override
    public synchronized MerchantResponse<BalanceInquiryResponse> inquiryBalance(BalanceInquiryMerchantRequest inMsg)
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException,
    UnsupportedEncodingException, SignatureException {

        MerchantResponse<BalanceInquiryResponse> outMsg = new MerchantResponse<>();
        BalanceInquiryRequest request = new BalanceInquiryRequest();
        request.setAccountNo(inMsg.getAccountNo());
        HttpMethod httpMethod = HttpMethod.POST;
        // Thread safety must declare new
        HttpHeaders headers = new HttpHeaders();
        headers = MandiriUtil.generateDefaultHeaders(
            MandiriUtil.generateExternalId(inMsg.getTransactionId()),
            "0001",
            httpMethod.name(),
            balanceInquiryUrl.split(baseUrl)[1],
            CommonUtil.hexSha256(objectMapper.writeValueAsString(request)),
            requestAccessToken().getTransactionDetail().getAccessToken(),
            clientSecret,
            partnerId,
            MandiriUtil.formatTimestamp(DateFormat.MANDIRI_FORMAT, System.currentTimeMillis())
        );

        HttpService<BalanceInquiryRequest> httpService = new HttpService<>();
        BalanceInquiryResponse response = httpService
            .sendRequest(request, headers, balanceInquiryUrl, httpMethod, BalanceInquiryResponse.class);
        return outMsg.successTemplate(response);
    }

    @Override
    public synchronized MerchantResponse<InterBankTransferResponse> interbankTransfer(InterBankTransferRequest inMsg)
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException,
    UnsupportedEncodingException, SignatureException {
        MerchantResponse<InterBankTransferResponse> outMsg = new MerchantResponse<>();
        InterBankTransferRequest request = new InterBankTransferRequest();
        HttpMethod httpMethod = HttpMethod.POST;
        String timestamp = MandiriUtil.formatTimestamp(DateFormat.MANDIRI_FORMAT, System.currentTimeMillis());
        inMsg.setTransactionDate(timestamp);
        log.info(objectMapper.writeValueAsString(inMsg));

        HttpHeaders headers = new HttpHeaders();
        headers = MandiriUtil.generateDefaultHeaders(
            MandiriUtil.generateExternalId(""),
            "0002",
            httpMethod.name(),
            interbankTransferUrl.split(baseUrl)[1],
            CommonUtil.hexSha256(objectMapper.writeValueAsString(inMsg)),
            requestAccessToken().getTransactionDetail().getAccessToken(),
            clientSecret,
            partnerId,
            timestamp
        );

        HttpService<InterBankTransferRequest> httpService = new HttpService<>();
        InterBankTransferResponse response = httpService
            .sendRequest(inMsg, headers, interbankTransferUrl, httpMethod, InterBankTransferResponse.class);
        return outMsg.successTemplate(response);
    }

    @Override
    public synchronized MerchantResponse<AccessTokenResponse> requestAccessToken()
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException,
    SignatureException, JsonProcessingException {
        
        log.info(">> Requesting access token");
        MerchantResponse<AccessTokenResponse> outMsg = new MerchantResponse<>();
        // SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.MANDIRI_FORMAT.value());
        // String timestamp = dateFormat.format(new Date());
        String timestamp = MandiriUtil.formatTimestamp(DateFormat.MANDIRI_FORMAT, System.currentTimeMillis());
        // timestamp = CommonUtil.generateTimestamp(timestamp, dateFormat.toPattern());
        String dataToEncrypt = CommonUtil.stringAppend(clientId,"|",timestamp);
        // String signature = CommonUtil.sha256withRsa(clientId, timestamp, privateKey);
        String signature = MandiriUtil.generateAccessTokenSignature("API_Portal.jks", "a123", "mitraa", "a123", "SHA256withRSA", dataToEncrypt);
        AccessTokenRequest request = new AccessTokenRequest("client_credentials");
        log.info(">> Generating headers");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CLIENT-KEY", clientId);
        headers.set("X-TIMESTAMP", timestamp);
        headers.set("X-SIGNATURE", signature);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpService<AccessTokenRequest> httpService = new HttpService<>();
        AccessTokenResponse response = httpService.sendRequest(request, headers, accessTokenUrl, HttpMethod.POST, AccessTokenResponse.class);
        return outMsg.successTemplate(response);
    }

}
