package com.openapi.paypal_krpost.domain.dto.common

data class PurchaseUnit(
    val amount: Amount
)

data class Amount(
    val currencyCode: String,
    val value: String
)
