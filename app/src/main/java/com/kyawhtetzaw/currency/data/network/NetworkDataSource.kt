package com.kyawhtetzaw.currency.data.network

import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRates

interface NetworkDataSource {
    suspend fun latest(): NetworkExchangeRates
}