package com.merchant.external.api.service;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class HttpService <REQ>  {

    public <RES> RES sendRequest(REQ request, String url, HttpMethod httpMethod, Class<RES> responseType)
    throws HttpClientErrorException, HttpServerErrorException, UnknownHttpStatusCodeException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        HttpEntity<REQ> requestEntity = new HttpEntity<REQ>(request, headers);
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        ResponseEntity<RES> response = restTemplate.exchange(url, httpMethod, requestEntity, responseType);
        return response.getBody();
    }

    public <RES> RES sendRequest(REQ request, HttpHeaders headers, String url, HttpMethod httpMethod, Class<RES> responseType)
    throws HttpClientErrorException, HttpServerErrorException, UnknownHttpStatusCodeException {
        HttpEntity<REQ> requestEntity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        ResponseEntity<RES> response = restTemplate.exchange(url, httpMethod, requestEntity, responseType);
        return response.getBody();
    }
    
    protected ClientHttpRequestFactory getClientHttpRequestFactory() {
        Integer timeout = 10000;
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .setSocketTimeout(timeout)
            .build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

}
