package com.openapi.paypal_krpost.domain.dto.response

data class CreateOrderResponse (
    val id: String,
    val status: String,
    val links: List<LinkDescription>
)

data class LinkDescription(
    val href: String,
    val rel: String,
    val method: String
)
