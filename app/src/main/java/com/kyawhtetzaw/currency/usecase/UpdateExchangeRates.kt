package com.kyawhtetzaw.currency.usecase

import com.kyawhtetzaw.currency.data.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRate
import com.kyawhtetzaw.currency.data.network.model.toEntity
import javax.inject.Inject

class UpdateExchangeRates @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val dao: ExchangeRateDao
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val networkRates = networkDataSource.latest()
        dao.insert(*networkRates.map(NetworkExchangeRate::toEntity).toTypedArray())
    }

}