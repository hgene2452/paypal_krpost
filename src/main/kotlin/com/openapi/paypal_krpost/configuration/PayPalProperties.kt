package com.openapi.paypal_krpost.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "paypal")
class PayPalProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var baseUrl: String
}
