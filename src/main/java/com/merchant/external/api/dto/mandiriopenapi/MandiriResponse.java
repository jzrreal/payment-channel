package com.merchant.external.api.dto.mandiriopenapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MandiriResponse {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String responseCode;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String responseMessage;
}
