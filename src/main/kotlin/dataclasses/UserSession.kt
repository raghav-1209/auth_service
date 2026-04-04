package com.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val refreshToken: String,
    val token: String,
)
