package com.kyawhtetzaw.currency.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class LastUpdateDataSourceImplTest {
    private lateinit var dataSource: LastUpdateDataSource
    private lateinit var preferences: SharedPreferences

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        dataSource = LastUpdateDataSourceImpl(preferences)
    }

    @After
    fun teardown() {
        preferences.edit().clear().apply()
    }

    @Test
    fun saveTimeStampStoresTimestampInSharedPreferences() {
        val timestamp = "2023-02-13 15:00:00"
        val localDateTime = LocalDateTime
            .parse(timestamp, DateTimeFormatter.ofPattern(LastUpdateDataSourceImpl.DatePattern))
        runBlocking { dataSource.saveTimestamp(localDateTime) }
        val storedTimeStamp = preferences.getString(LastUpdateDataSourceImpl.KEY_LAST_UPDATE, null)
        assertEquals(timestamp, storedTimeStamp)
    }

    @Test
    fun testSaveAndGetLastUpdateTime() = runTest {
        val dateTime = "2023-02-13 15:00:00"
        val timestamp = LocalDateTime
            .parse(dateTime, DateTimeFormatter.ofPattern(LastUpdateDataSourceImpl.DatePattern))
        dataSource.saveTimestamp(timestamp)

        // Get last update time and assert it matches the saved timestamp
        val lastUpdateTime = dataSource.getLastUpdateTime()
        assertEquals(timestamp, lastUpdateTime)
    }

    @Test
    fun getLastUpdateTimeShouldReturnNullsWhenNotSaved() = runTest {
        val lastUpdate = dataSource.getLastUpdateTime()
        assertNull(lastUpdate)
    }

    @Test
    fun testGetLastUpdateTimeWithInvalidFormat() = runTest {
        val invalidTimeStamp = "invalid_timestamp"
        preferences.edit().putString(LastUpdateDataSourceImpl.KEY_LAST_UPDATE, invalidTimeStamp).commit()

        // getLastUpdateTime should returns null
        val lastUpdateTime = dataSource.getLastUpdateTime()
        assertNull(lastUpdateTime)
    }
}