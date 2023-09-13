package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;

@Data
public class AccessTokenRequest {
    private String grantType;

    public AccessTokenRequest(String grantType) {
        this.grantType = grantType;
    }
}
