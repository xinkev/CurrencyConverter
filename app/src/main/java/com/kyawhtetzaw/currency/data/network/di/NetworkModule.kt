package com.kyawhtetzaw.currency.data.network.di

import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import com.kyawhtetzaw.currency.data.network.OpenExchangeNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    fun networkSource(impl: OpenExchangeNetworkDataSource): NetworkDataSource
}