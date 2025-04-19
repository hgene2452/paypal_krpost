package com.openapi.paypal_krpost.domain.dto.request

import com.openapi.paypal_krpost.domain.dto.common.PurchaseUnit

data class CreateOrderRequest (
    val intent: String = "CAPTURE",
    val purchaseUnits: List<PurchaseUnit>
)
