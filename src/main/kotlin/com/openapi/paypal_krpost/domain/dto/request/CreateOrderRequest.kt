package com.openapi.paypal_krpost.domain.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.openapi.paypal_krpost.domain.dto.common.PurchaseUnit

data class CreateOrderRequest (
    val intent: String,
    @JsonProperty("purchase_units")
    val purchaseUnits: List<PurchaseUnit>
)
