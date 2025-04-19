package com.openapi.paypal_krpost.service

import com.openapi.paypal_krpost.repository.PaymentsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PaymentsService(
    private val paymentsRepository: PaymentsRepository
) {
}
