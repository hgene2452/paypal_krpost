package com.openapi.paypal_krpost.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "invoice")
class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(name = "invoice_id", nullable = false, unique = true)
    val invoiceId: String,

    @Column(name = "recipient_view_url", nullable = false)
    val recipientViewUrl: String?,

    @Column(name = "status", nullable = true)
    var status: InvoiceStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payments_id", nullable = false)
    var payments: Payments
): BaseEntity()

enum class InvoiceStatus {
    DRAFT, SENT, PAID, CANCELLED, PARTIALLY_PAID
}
