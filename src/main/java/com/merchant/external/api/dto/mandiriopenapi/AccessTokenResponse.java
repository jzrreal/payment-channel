package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String responseCode;
    private String responseMessage;
    private String accessToken;
    private String tokenType;
    private String expiresIn;
}
