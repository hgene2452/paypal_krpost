package com.openapi.paypal_krpost.domain.dto.response

import com.openapi.paypal_krpost.domain.dto.common.PurchaseUnit

data class GetOrderDetailsResponse (
    val id: String,
    val status: String,
    val intent: String,
    val purchaseUnits: List<PurchaseUnit>?,
    val payer: Payer?,
    val links: List<LinkDescription>?
)

data class Payer(
    val emailAddress: String?,
    val payerId: String?
)
