package com.kyawhtetzaw.currency.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kyawhtetzaw.currency.data.database.model.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    suspend fun getAll(): Flow<List<ExchangeRateEntity>>

    @Insert
    suspend fun insert(vararg entities: ExchangeRateEntity)
}