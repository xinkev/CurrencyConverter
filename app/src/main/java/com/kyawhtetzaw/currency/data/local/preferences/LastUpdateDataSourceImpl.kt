package com.kyawhtetzaw.currency.data.local.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class LastUpdateDataSourceImpl @Inject constructor(
    private val preferences: SharedPreferences
) : LastUpdateDataSource {
    private val dateFormatter = DateTimeFormatter.ofPattern(DatePattern)
    override suspend fun saveTimestamp(timeStamp: LocalDateTime) {
        preferences.edit(commit = true) {
            putString(KEY_LAST_UPDATE, timeStamp.format(dateFormatter))
        }
    }

    override suspend fun getLastUpdateTime(): LocalDateTime? =
        preferences.getString(KEY_LAST_UPDATE, null)?.let {
            try {
                LocalDateTime.parse(it, dateFormatter)
            } catch (e: DateTimeParseException){
                null
            }
        }

    companion object {
        const val DatePattern = "yyyy-MM-dd HH:mm:ss"
        const val KEY_LAST_UPDATE = "last_update"
    }
}