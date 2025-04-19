package com.openapi.paypal_krpost.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "payments")
class Payments(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "order_id", nullable = false, unique = true)
    val orderId: String,

    @Column(name = "capture_id", nullable = true)
    var captureId: String?,

    @Column(name = "capture_href", nullable = true)
    var captureHref: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: Users,

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    val amount: BigDecimal,

    @Column(name = "currency", nullable = false, length = 10)
    val currency: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.CREATED

): BaseEntity()

enum class PaymentStatus {
    CREATED, APPROVED, COMPLETED, FAILED
}
