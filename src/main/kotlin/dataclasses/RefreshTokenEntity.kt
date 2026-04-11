package com.dataclasses

data class RefreshTokenEntity(
    val hashToken : String,
    val uid: String,
    val expiresAt: Long,
    val createdAt :  Long,
    val  id: Int
)
