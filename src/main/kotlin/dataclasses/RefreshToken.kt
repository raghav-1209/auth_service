package com.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    val refreshToken: String,
    val uid: String
)
