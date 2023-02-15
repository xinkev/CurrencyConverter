package com.kyawhtetzaw.currency.data.network.model

import com.kyawhtetzaw.currency.data.local.database.model.ExchangeRateEntity

typealias NetworkExchangeRate = Map.Entry<String, Double>
typealias NetworkExchangeRates = Map<String, Double>

fun NetworkExchangeRate.toEntity(): ExchangeRateEntity = ExchangeRateEntity(
    symbol = key,
    rate = value
)