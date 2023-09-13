package com.merchant.external.api.dto.mandiriopenapi;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccessTokenResponse extends MandiriResponse{
    private String accessToken;
    private String tokenType;
    private String expiresIn;
}
