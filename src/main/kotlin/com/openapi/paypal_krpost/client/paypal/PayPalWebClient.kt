package com.openapi.paypal_krpost.client.paypal

import com.openapi.paypal_krpost.configuration.PayPalProperties
import com.openapi.paypal_krpost.domain.dto.request.CreateOrderRequest
import com.openapi.paypal_krpost.domain.dto.response.CaptureOrderResponse
import com.openapi.paypal_krpost.domain.dto.response.CreateOrderResponse
import com.openapi.paypal_krpost.domain.dto.response.GetOrderDetailsResponse
import com.openapi.paypal_krpost.domain.dto.response.PayPalAccessTokenResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.Base64

@Component
class PayPalWebClient(
    private val webClient: WebClient,
    private val payPalProperties: PayPalProperties
) {
    /**
     * PayPal Access Token 발급
     */
    fun getAccessToken(): Mono<String> {
        val basicAuth = Base64.getEncoder().encodeToString("${payPalProperties.clientId}:${payPalProperties.clientSecret}".toByteArray())

        return webClient.post()
            .uri("${payPalProperties.baseUrl}/v1/oauth2/token")
            .header("Authorization", "Basic $basicAuth")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue("grant_type=client_credentials")
            .retrieve()
            .bodyToMono(PayPalAccessTokenResponse::class.java)
            .map { it.accessToken }
    }

    /**
     * PayPal 주문 생성
     */
    fun createOrder(accessToken: String, createOrderRequest: CreateOrderRequest): Mono<CreateOrderResponse> {
        return webClient.post()
            .uri("${payPalProperties.baseUrl}/v2/checkout/orders")
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .bodyValue(createOrderRequest)
            .retrieve()
            .bodyToMono(CreateOrderResponse::class.java)
    }

    /**
     * PayPal 결제 캡처
     */
    fun captureOrder(accessToken: String, orderId: String): Mono<CaptureOrderResponse> {
        return webClient.post()
            .uri("${payPalProperties.baseUrl}/v2/checkout/orders/$orderId/capture")
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .retrieve()
            .bodyToMono(CaptureOrderResponse::class.java)
    }

    /**
     * Paypal 주문 확인
     */
    fun getOrderDetails(accessToken: String, orderId: String): Mono<GetOrderDetailsResponse> {
        return webClient.get()
            .uri("${payPalProperties.baseUrl}/v2/checkout/orders/$orderId")
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .retrieve()
            .bodyToMono(GetOrderDetailsResponse::class.java)
    }
}
