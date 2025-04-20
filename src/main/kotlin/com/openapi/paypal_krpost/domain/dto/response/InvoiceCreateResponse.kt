package com.openapi.paypal_krpost.domain.dto.response

import com.fasterxml.jackson.annotation.JsonProperty


data class InvoiceCreateResponse(
    val id: String,
    val status: String,
    val detail: InvoiceDetail,
    val invoicer: InvoicerInfo,
    @JsonProperty("primary_recipients")
    val primaryRecipients: List<PrimaryRecipient>,
    val items: List<InvoiceItem>,
    val configuration: Configuration,
    val amount: Amount,
    @JsonProperty("due_amount")
    val dueAmount: DueAmount,
    val links: List<LinkInfo>,
    val unilateral: Boolean
)

data class InvoiceDetail(
    @JsonProperty("currency_code")
    val currencyCode: String,
    val note: String,
    @JsonProperty("category_code")
    val categoryCode: String,
    @JsonProperty("invoice_number")
    val invoiceNumber: String,
    @JsonProperty("invoice_date")
    val invoiceDate: String,
    @JsonProperty("payment_term")
    val paymentTerm: PaymentTerm,
    @JsonProperty("viewed_by_recipient")
    val viewedByRecipient: Boolean,
    @JsonProperty("group_draft")
    val groupDraft: Boolean,
    val metadata: Metadata,
    val archived: Boolean
)

data class PaymentTerm(
    @JsonProperty("term_type")
    val termType: String
)

data class Metadata(
    @JsonProperty("create_time")
    val createTime: String,
    @JsonProperty("last_update_time")
    val lastUpdateTime: String,
    @JsonProperty("created_by_flow")
    val createdByFlow: String,
    @JsonProperty("recipient_view_url")
    val recipientViewUrl: String,
    @JsonProperty("invoicer_view_url")
    val invoicerViewUrl: String,
    @JsonProperty("caller_type")
    val callerType: String,
    @JsonProperty("spam_info")
    val spamInfo: Map<String, Any> // 스팸정보는 지금 구조가 없어서 임시 Map으로 둠
)

data class InvoicerInfo(
    val name: Name,
    val address: Address,
    @JsonProperty("email_address")
    val emailAddress: String
)

data class PrimaryRecipient(
    @JsonProperty("billing_info")
    val billingInfo: BillingInfo
)

data class BillingInfo(
    val name: Name,
    val address: Address,
    @JsonProperty("email_address")
    val emailAddress: String
)

data class Name(
    @JsonProperty("given_name")
    val givenName: String,
    val surname: String,
    @JsonProperty("full_name")
    val fullName: String
)

data class Address(
    @JsonProperty("postal_code")
    val postalCode: String,
    @JsonProperty("country_code")
    val countryCode: String
)

data class InvoiceItem(
    val id: String?,
    val name: String,
    val description: String,
    val quantity: String,
    @JsonProperty("unit_amount")
    val unitAmount: Money,
    val tax: TaxInfo?,
    val discount: DiscountInfo?,
    @JsonProperty("unit_of_measure")
    val unitOfMeasure: String
)

data class Money(
    @JsonProperty("currency_code")
    val currencyCode: String? = null,
    val value: String? = null
)

data class TaxInfo(
    val id: String?,
    val name: String,
    val percent: String,
    val amount: Money?
)

data class DiscountInfo(
    val percent: String?,
    val amount: Money?
)

data class Configuration(
    @JsonProperty("tax_calculated_after_discount")
    val taxCalculatedAfterDiscount: Boolean,
    @JsonProperty("tax_inclusive")
    val taxInclusive: Boolean,
    @JsonProperty("allow_tip")
    val allowTip: Boolean,
    @JsonProperty("allow_only_pay_by_bank")
    val allowOnlyPayByBank: Boolean,
    @JsonProperty("template_id")
    val templateId: String?
)

data class Amount(
    @JsonProperty("currency_code")
    val currencyCode: String? = null,
    val value: String? = null,
    val breakdown: Breakdown? = null
)

data class Breakdown(
    @JsonProperty("item_total")
    val itemTotal: Money?,
    @JsonProperty("custom")
    val custom: Money?,
    @JsonProperty("shipping")
    val shipping: Shipping?,
    @JsonProperty("discount")
    val discount: DiscountBreakdown?,
    @JsonProperty("tax_total")
    val taxTotal: Money?
)

data class Shipping(
    val amount: Money?,
    val tax: TaxInfo?
)

data class DiscountBreakdown(
    @JsonProperty("item_discount")
    val itemDiscount: Money?,
    @JsonProperty("invoice_discount")
    val invoiceDiscount: Money?
)

data class DueAmount(
    @JsonProperty("currency_code")
    val currencyCode: String,
    val value: String
)

data class LinkInfo(
    val href: String,
    val rel: String,
    val method: String
)
