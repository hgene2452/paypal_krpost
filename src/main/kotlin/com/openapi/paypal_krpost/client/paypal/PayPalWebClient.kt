package com.openapi.paypal_krpost.client.paypal

import com.openapi.paypal_krpost.configuration.PayPalProperties
import com.openapi.paypal_krpost.domain.dto.request.CreateOrderRequest
import com.openapi.paypal_krpost.domain.dto.request.InvoiceCreateRequest
import com.openapi.paypal_krpost.domain.dto.response.*
import org.springframework.core.ParameterizedTypeReference
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

    /**
     * PayPal 인보이스 생성
     */
    fun createInvoice(accessToken: String, request: InvoiceCreateRequest): Mono<InvoiceCreateResponse> {
        val bodyToMono = webClient.post()
            .uri("${payPalProperties.baseUrl}/v2/invoicing/invoices")
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(InvoiceCreateResponse::class.java)

        return bodyToMono
    }

    /**
     * PayPal 인보이스 전송 (이메일)
     */
    fun sendInvoice(accessToken: String, invoiceId: String): Mono<Void> {
        val bodyToMono = webClient.post()
            .uri("${payPalProperties.baseUrl}/v2/invoicing/invoices/$invoiceId/send")
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .bodyValue(
                mapOf(
                    "send_to_invoicer" to true
                )
            )
            .retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) { response ->
                response.bodyToMono(String::class.java)
                    .flatMap { errorBody ->
                        println("🔥 PayPal Error Body: $errorBody")
                        Mono.error(RuntimeException("PayPal API Error: $errorBody"))
                    }
            }
            .bodyToMono(Void::class.java)

        return bodyToMono
    }
}
