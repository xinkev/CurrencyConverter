package com.kyawhtetzaw.currency.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyawhtetzaw.currency.model.ExchangeRate

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey
    val symbol: String,
    val rate: Double
)


fun ExchangeRateEntity.toDomainModel(): ExchangeRate = ExchangeRate(
    symbol = symbol,
    rate = rate
)