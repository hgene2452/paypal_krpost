package com.openapi.paypal_krpost.domain.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class InvoiceCreateRequest(
    val detail: InvoiceDetail,
    val invoicer: InvoicerInfo,
    @JsonProperty("primary_recipients")
    val primaryRecipients: List<PrimaryRecipient>,
    val items: List<InvoiceItem>
)

data class InvoiceDetail(
    @JsonProperty("invoice_number")
    val invoiceNumber: String,
    @JsonProperty("currency_code")
    val currencyCode: String,
    val note: String? = null,
    val term: String? = null,
)

data class InvoicerInfo(
    val name: Name,
    @JsonProperty("email_address")
    val emailAddress: String,
    val address: Address? = null
)

data class Name(
    @JsonProperty("given_name")
    val givenName: String,
    val surname: String
)

data class Address(
    @JsonProperty("address_line1")
    val addressLine1: String,
    @JsonProperty("address_line2")
    val addressLine2: String? = null,
    @JsonProperty("admin_area2")
    val adminArea2: String? = null,
    @JsonProperty("admin_area1")
    val adminArea1: String? = null,
    @JsonProperty("postal_code")
    val postalCode: String? = null,
    @JsonProperty("country_code")
    val countryCode: String
)

data class PrimaryRecipient(
    @JsonProperty("billing_info")
    val billingInfo: BillingInfo
)

data class BillingInfo(
    val name: Name? = null,
    @JsonProperty("email_address")
    val emailAddress: String,
    val address: Address? = null
)

data class InvoiceItem(
    val name: String,
    val quantity: String,
    @JsonProperty("unit_amount")
    val unitAmount: Money,
    val description: String? = null,
    val tax: TaxInfo? = null,
    val discount: DiscountInfo? = null,
    @JsonProperty("unit_of_measure")
    val unitOfMeasure: String? = "QUANTITY"
)

data class Money(
    @JsonProperty("currency_code")
    val currencyCode: String,
    val value: String
)

data class TaxInfo(
    val name: String,
    val percent: String
)

data class DiscountInfo(
    val percent: String? = null,
    val amount: Money? = null
)
