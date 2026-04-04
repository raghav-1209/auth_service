package com.dataclasses

data class JwtConfig(
    val realm: String,
    val secret: String,
    val issuer: String,
    val audience: String,
)
