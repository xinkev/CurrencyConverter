package com.kyawhtetzaw.currency.data.local.preferences

import java.time.LocalDateTime

interface LastUpdateDataSource {
    /**
     * Save the date time when the exchange rates data are updated.
     */
    suspend fun saveTimestamp(timeStamp: LocalDateTime)

    /**
     * Get the last updated time.
     */
    suspend fun getLastUpdateTime(): LocalDateTime?
}