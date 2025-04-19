package com.openapi.paypal_krpost.domain.dto.common

import com.fasterxml.jackson.annotation.JsonProperty

data class PurchaseUnit(
    val amount: Amount
)

data class Amount(
    @JsonProperty("currency_code")
    val currencyCode: String,
    val value: String
)
