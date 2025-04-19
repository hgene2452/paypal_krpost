package com.openapi.paypal_krpost.api

import com.openapi.paypal_krpost.domain.dto.request.CreateOrderRequest
import com.openapi.paypal_krpost.domain.entity.PaymentStatus
import com.openapi.paypal_krpost.service.PaymentsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentsApi(
    private val paymentsService: PaymentsService
) {
    /**
     * 주문 생성 API
     * userId는 임시로 쿼리파라미터나 헤더로 받는다고 가정 (나중에 인증 붙이면 수정)
     */
    @PostMapping
    fun createOrder(
        @RequestBody request: CreateOrderRequest
    ): String {
        val userId: Long = 1L
        return paymentsService.createOrder(userId, request)
    }

    /**
     * 결제 캡처(승인) API
     */
    @PostMapping("/{orderId}/capture")
    fun capturePayment(
        @PathVariable orderId: String
    ): String {
        val userId: Long = 1L
        return paymentsService.capturePayment(userId, orderId)
    }

    /**
     * 결제 상태 조회 API
     */
    @GetMapping("/{orderId}")
    fun getPaymentStatus(
        @PathVariable orderId: String
    ): PaymentStatus {
        return paymentsService.getPaymentDetails(orderId)
    }
}
