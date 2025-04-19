package com.openapi.paypal_krpost.service

import com.openapi.paypal_krpost.client.paypal.PayPalWebClient
import com.openapi.paypal_krpost.domain.dto.request.CreateOrderRequest
import com.openapi.paypal_krpost.domain.entity.PaymentStatus
import com.openapi.paypal_krpost.domain.entity.Payments
import com.openapi.paypal_krpost.repository.PaymentsRepository
import com.openapi.paypal_krpost.repository.UsersRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PaymentsService(
    private val paymentsRepository: PaymentsRepository,
    private val usersRepository: UsersRepository,
    private val payPalWebClient: PayPalWebClient
) {
    @Transactional
    fun createOrder(userId: Long, request: CreateOrderRequest): String {
        val user = usersRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        val accessToken = payPalWebClient.getAccessToken().block()!!

        val response = payPalWebClient.createOrder(accessToken, request).block()!!

        val payments = Payments(
            id = null,
            orderId = response.id,
            captureId = null,
            captureHref = response.links.first { it.rel == "capture" }.href,
            user = user,
            amount = request.purchaseUnits[0].amount.value.toBigDecimal(),
            currency = request.purchaseUnits[0].amount.currencyCode,
            status = PaymentStatus.CREATED
        )
        paymentsRepository.save(payments)

        return response.links.first { it.rel == "approve" }.href
    }

    @Transactional
    fun capturePayment(userId: Long, orderId: String): String {
        val accessToken = payPalWebClient.getAccessToken().block()!!

        val payment = paymentsRepository.findByOrderId(orderId)
            ?: throw IllegalArgumentException("Payment not found: $orderId")

        if (payment.user.id != userId) {
            throw IllegalArgumentException("User mismatch for order: $orderId")
        }

        val response = payPalWebClient.captureOrder(accessToken, orderId).block()!!

        payment.status = PaymentStatus.COMPLETED
        payment.captureId = response.id
        paymentsRepository.save(payment)

        return response.status
    }

    fun getPaymentDetails(orderId: String): PaymentStatus {
        val accessToken = payPalWebClient.getAccessToken().block()!!

        val response = payPalWebClient.getOrderDetails(accessToken, orderId).block()!!

        return when (response.status) {
            "COMPLETED" -> PaymentStatus.COMPLETED
            "CREATED" -> PaymentStatus.CREATED
            else -> PaymentStatus.FAILED
        }
    }
}
