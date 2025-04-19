package com.openapi.paypal_krpost.domain.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PayPalAccessTokenResponse (
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Int
)
