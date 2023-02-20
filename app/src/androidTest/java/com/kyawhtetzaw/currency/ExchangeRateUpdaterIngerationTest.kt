package com.kyawhtetzaw.currency

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.kyawhtetzaw.currency.data.local.database.CurrencyDatabase
import com.kyawhtetzaw.currency.data.local.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSourceImpl
import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRates
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeRateUpdaterIntegrationTest {

    private lateinit var database: CurrencyDatabase
    private lateinit var dao: ExchangeRateDao
    private lateinit var updater: ExchangeRateUpdater
    private lateinit var lastUpdateDataSource: LastUpdateDataSource
    private lateinit var networkDataSource: NetworkDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CurrencyDatabase::class.java).build()
        dao = database.exchangeRateDao()
        lastUpdateDataSource = LastUpdateDataSourceImpl(
            context.getSharedPreferences(
                "test_prefs",
                Context.MODE_PRIVATE
            )
        )
        networkDataSource = object : NetworkDataSource {
            override suspend fun latest(): NetworkExchangeRates =
                mapOf("EUR" to 0.85, "GBP" to 0.73)

        }
        updater = ExchangeRateUpdater(lastUpdateDataSource, networkDataSource, dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdatesAndSavesExchangeRatesToDatabase() = runTest {
        // Ensure the database is empty before starting the test
        dao.getAll()
            .test {
                assertTrue(awaitItem().isEmpty())
            }

        // Wait for initial update
        delay(1.seconds)

        val duration = 1.minutes
        updater.start(duration)
            .test {
                TestCase.assertEquals(ExchangeRateUpdateState.Updating, awaitItem())
                TestCase.assertEquals(ExchangeRateUpdateState.Success, awaitItem())
                TestCase.assertEquals(ExchangeRateUpdateState.Timer("01:00"), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

        // Assert that the exchange rates were saved to the database
        dao.getAll().test {
            val cache = awaitItem()
            assertTrue("Exchange rates were not saved.", cache.isNotEmpty())
        }
    }
}