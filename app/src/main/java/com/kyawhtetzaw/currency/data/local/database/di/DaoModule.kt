package com.kyawhtetzaw.currency.data.local.database.di

import com.kyawhtetzaw.currency.data.local.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.local.database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun exchangeRateDao(db: CurrencyDatabase): ExchangeRateDao = db.exchangeRateDao()
}