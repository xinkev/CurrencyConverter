package com.kyawhtetzaw.currency.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kyawhtetzaw.currency.data.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.database.model.ExchangeRateEntity

@Database(
    entities = [ExchangeRateEntity::class],
    version = 1
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao
}