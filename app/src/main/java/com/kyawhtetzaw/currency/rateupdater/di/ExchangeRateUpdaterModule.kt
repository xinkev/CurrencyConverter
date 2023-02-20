package com.kyawhtetzaw.currency.rateupdater.di

import com.kyawhtetzaw.currency.ExchangeRateUpdater
import com.kyawhtetzaw.currency.ExchangeRateUpdaterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface ExchangeRateUpdaterModule {
    @Binds
    fun updater(impl: ExchangeRateUpdaterImpl): ExchangeRateUpdater
}