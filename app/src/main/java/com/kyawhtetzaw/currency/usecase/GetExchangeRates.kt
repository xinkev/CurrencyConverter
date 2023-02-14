package com.kyawhtetzaw.currency.usecase

import com.kyawhtetzaw.currency.data.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.database.model.ExchangeRateEntity
import com.kyawhtetzaw.currency.data.database.model.toDomainModel
import com.kyawhtetzaw.currency.model.ExchangeRate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetExchangeRates @Inject constructor(
    private val dao: ExchangeRateDao
) {
    suspend operator fun invoke(): Flow<List<ExchangeRate>> = dao.getAll()
        .map { it.map(ExchangeRateEntity::toDomainModel) }
}