package com.kyawhtetzaw.currency.data.local.preferences.di

import android.content.Context
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun lastUpdateDataSource(
        @ApplicationContext context: Context
    ): LastUpdateDataSource {
        val prefs = context.getSharedPreferences("last_update_prefs", Context.MODE_PRIVATE)
        return LastUpdateDataSourceImpl(prefs)
    }
}