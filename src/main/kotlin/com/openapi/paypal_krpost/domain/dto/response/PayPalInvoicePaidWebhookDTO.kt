package com.openapi.paypal_krpost.domain.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PayPalInvoicePaidWebhookDTO(
    val id: String,
    @JsonProperty("event_version")
    val eventVersion: String,
    @JsonProperty("create_time")
    val createTime: String,
    @JsonProperty("resource_type")
    val resourceType: String,
    @JsonProperty("event_type")
    val eventType: String,
    val summary: String,
    val resource: InvoiceResource,
    val links: List<Link>
)

data class InvoiceResource(
    val invoice: Invoice
)

data class Invoice(
    val id: String,
    val status: String,
    val detail: InvoiceDetail,
    val invoicer: Invoicer,
    @JsonProperty("primary_recipients")
    val primaryRecipients: List<PrimaryRecipient>,
    val items: List<Item>,
    val configuration: Configuration,
    val amount: Amount,
    @JsonProperty("due_amount")
    val dueAmount: AmountValue,
    val payments: Payments,
    val links: List<Link>,
    val unilateral: Boolean
)

data class Invoicer(
    val name: Name,
    val address: Address,
    @JsonProperty("email_address")
    val emailAddress: String
)

data class Item(
    val id: String,
    val name: String,
    val description: String,
    val quantity: String,
    @JsonProperty("unit_amount")
    val unitAmount: AmountValue,
    val tax: Tax,
    val discount: Discount,
    @JsonProperty("unit_of_measure")
    val unitOfMeasure: String
)

data class AmountValue(
    @JsonProperty("currency_code")
    val currencyCode: String,
    val value: String
)

data class Tax(
    val id: String,
    val name: String,
    val percent: String,
    val amount: AmountValue
)

data class Discount(
    val percent: String,
    val amount: AmountValue
)

data class Payments(
    @JsonProperty("paid_amount")
    val paidAmount: AmountValue,
    val transactions: List<Transaction>
)

data class Transaction(
    val type: String,
    @JsonProperty("payment_id")
    val paymentId: String,
    @JsonProperty("transaction_type")
    val transactionType: String,
    @JsonProperty("payment_date")
    val paymentDate: String,
    val method: String,
    @JsonProperty("amount_value")
    val amount: AmountValue?,
    @JsonProperty("transaction_status")
    val transactionStatus: String
)

data class Link(
    val href: String,
    val rel: String,
    val method: String
)
