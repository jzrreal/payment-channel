package com.merchant.external.api.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.merchant.external.api.dto.MerchantResponse;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenRequest;
import com.merchant.external.api.dto.mandiriopenapi.AccessTokenResponse;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryMerchantRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryRequest;
import com.merchant.external.api.dto.mandiriopenapi.BalanceInquiryResponse;
import com.merchant.external.api.enums.DateFormat;
import com.merchant.external.api.service.HttpService;
import com.merchant.external.api.service.MandiriOpenApiService;
import com.merchant.external.api.utilities.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
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

    @Override
    public MerchantResponse<BalanceInquiryResponse> inquiryBalance(BalanceInquiryMerchantRequest inMsg)
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException,
    UnsupportedEncodingException, SignatureException {
        MerchantResponse<BalanceInquiryResponse> outMsg = new MerchantResponse<>();
        BalanceInquiryRequest request = new BalanceInquiryRequest();
        request.setAccountNo(inMsg.getAccountNo());

        // Thread safety must declare new
        HttpHeaders headers = new HttpHeaders();
        headers = generateDefaultHeaders(
            generateExternalId(""),
            "0001",
            "POST",
            balanceInquiryUrl.split(baseUrl)[0],
            CommonUtil.hexSha256(request),
            requestAccessToken().getTransactionDetail().getAccessToken()
        );

        HttpService<BalanceInquiryRequest> httpService = new HttpService<>();
        BalanceInquiryResponse response = httpService
            .sendRequest(request, headers, balanceInquiryUrl, HttpMethod.POST, BalanceInquiryResponse.class);
        return outMsg.successTemplate(response);
    }

    @Override
    public MerchantResponse<Object> transfer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'transfer'");
    }

    @Override
    public MerchantResponse<AccessTokenResponse> requestAccessToken()
    throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException,
    SignatureException, JsonProcessingException {
        log.info(">> Requesting access token");
        MerchantResponse<AccessTokenResponse> outMsg = new MerchantResponse<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.MANDIRI_FORMAT.value());
        String timestamp = dateFormat.format(new Date());
        // timestamp = CommonUtil.generateTimestamp(timestamp, dateFormat.toPattern());
        String dataToEncrypt = CommonUtil.stringAppend(clientId,"|",timestamp);
        log.info("{}", timestamp);
        // String signature = CommonUtil.sha256withRsa(clientId, timestamp, privateKey);
        String signature = CommonUtil.sign(null, "a123", "mitraa", "a123", "SHA256withRSA", dataToEncrypt);
        AccessTokenRequest request = new AccessTokenRequest("client_credentials");
        log.info(">> Generating headers");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CLIENT-KEY", clientId);
        headers.set("X-TIMESTAMP", timestamp);
        headers.set("X-SIGNATURE", signature);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // headers.set("Content-Type", "application/x-www-form-urlencoded");
        log.info(">> X-CLIENT-KEY: {}", clientId);
        log.info(">> X-TIMESTAMP: {}", timestamp);
        log.info(">> X-SIGNATURE: {}", signature);
        
        HttpService<AccessTokenRequest> httpService = new HttpService<>();
        AccessTokenResponse response = httpService.sendRequest(request, headers, accessTokenUrl, HttpMethod.POST, AccessTokenResponse.class);
        return outMsg.successTemplate(response);
    }

    protected HttpHeaders generateDefaultHeaders(
        String externalId, String channelId, String httpMethod,
        String url, String hexSha256RequestBodyString, String accessToken
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.MANDIRI_FORMAT.value());
        String timestamp = dateFormat.format(new Date());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-TIMESTAMP", dateFormat.format(new Date()));
        headers.set(
            "SIGNATURE",
            CommonUtil.generateMandiriSignatureHeader(
                httpMethod, url, hexSha256RequestBodyString, timestamp, accessToken, clientId)
            );
        headers.set("X-PARTNER-ID", partnerId);
        headers.set("X-EXTERNAL-ID", externalId);
        headers.set("CHANNEL-ID", channelId);
        return headers;
    }

    protected String generateExternalId(String transactionId) {
        UUID uuid = new UUID(System.currentTimeMillis(), transactionId.length() * 123456789);
        return uuid.toString().substring(0, 18);
    }    
}
