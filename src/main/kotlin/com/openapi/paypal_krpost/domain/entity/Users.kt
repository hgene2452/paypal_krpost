package com.openapi.paypal_krpost.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class Users(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "email", nullable = false)
    val email: String

): BaseEntity()
