package com.kyawhtetzaw.currency.data.database.di

import android.content.Context
import androidx.room.Room
import com.kyawhtetzaw.currency.data.database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun database(@ApplicationContext context: Context): CurrencyDatabase = Room
        .databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            "currency_database"
        )
        .build()

}