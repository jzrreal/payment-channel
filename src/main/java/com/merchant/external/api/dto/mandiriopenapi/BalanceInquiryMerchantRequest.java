package com.merchant.external.api.dto.mandiriopenapi;

import com.merchant.external.api.dto.MerchantRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BalanceInquiryMerchantRequest extends MerchantRequest {
    private String accountNo;
}
