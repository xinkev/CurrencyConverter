package com.kyawhtetzaw.currency.usecase

import com.kyawhtetzaw.currency.data.local.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRate
import com.kyawhtetzaw.currency.data.network.model.toEntity
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class UpdateExchangeRates @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val lastUpdateDataSource: LastUpdateDataSource,
    private val dao: ExchangeRateDao
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val now = LocalDateTime.now()
        val shouldUpdate = lastUpdateDataSource.getLastUpdateTime()?.let {
            ChronoUnit.MINUTES.between(it, now) >= 30
        } ?: true

        if (shouldUpdate) {
            val networkRates = networkDataSource.latest()
            dao.insert(*networkRates.map(NetworkExchangeRate::toEntity).toTypedArray())
            lastUpdateDataSource.saveTimestamp(now)
        }
    }
}