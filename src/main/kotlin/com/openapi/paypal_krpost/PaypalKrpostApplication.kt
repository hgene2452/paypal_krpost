package com.openapi.paypal_krpost

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class PaypalKrpostApplication

fun main(args: Array<String>) {
    runApplication<PaypalKrpostApplication>(*args)
}
