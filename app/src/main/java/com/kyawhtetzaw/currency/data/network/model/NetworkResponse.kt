package com.kyawhtetzaw.currency.data.network.model

@kotlinx.serialization.Serializable
data class NetworkResponse<T>(
    val rates: T
)
