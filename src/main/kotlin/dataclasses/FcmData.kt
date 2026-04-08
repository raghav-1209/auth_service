package com.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class FcmData(
    val token: String,
    val uid: String,
)
