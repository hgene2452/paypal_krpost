package com.openapi.paypal_krpost.repository

import com.openapi.paypal_krpost.domain.entity.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : JpaRepository<Users, Long> {
}
