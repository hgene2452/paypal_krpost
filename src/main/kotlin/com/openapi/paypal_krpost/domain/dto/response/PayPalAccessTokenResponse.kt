package com.openapi.paypal_krpost.domain.dto.response

data class PayPalAccessTokenResponse (
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int
)
