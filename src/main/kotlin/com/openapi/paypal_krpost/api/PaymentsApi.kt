package com.openapi.paypal_krpost.api

import com.openapi.paypal_krpost.service.PaymentsService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class PaymentsApi(
    private val paymentsService: PaymentsService
) {
}
