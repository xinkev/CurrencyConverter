package com.kyawhtetzaw.currency.di

import com.kyawhtetzaw.currency.di.MyDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Dispatcher(IO)
    @Provides
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}