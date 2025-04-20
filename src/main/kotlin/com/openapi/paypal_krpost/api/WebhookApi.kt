package com.openapi.paypal_krpost.api

import com.openapi.paypal_krpost.domain.dto.response.PayPalInvoicePaidWebhookDTO
import com.openapi.paypal_krpost.service.PaymentsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/paypal/webhook")
class WebhookApi(
    private val paymentsService: PaymentsService
) {

    @PostMapping
    fun receiveWebhook(@RequestBody payload: PayPalInvoicePaidWebhookDTO) {
        val eventType = payload.eventType
        val resource = payload.resource
        val invoiceId = resource.invoice.id

        if (eventType == "INVOICING.INVOICE.PAID" && invoiceId != null) {
            println("Invoice Paid 확인됨! invoiceId: $invoiceId")
            paymentsService.captureInvoice(payload)
        }
    }
}
