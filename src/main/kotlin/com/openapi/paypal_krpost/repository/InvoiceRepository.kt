package com.openapi.paypal_krpost.repository

import com.openapi.paypal_krpost.domain.entity.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : JpaRepository<Invoice, Long> {
    fun findByInvoiceId(invoiceId: String): Invoice?
}
