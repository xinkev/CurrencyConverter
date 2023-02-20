package com.kyawhtetzaw.currency

import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Success
import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Timer
import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Updating
import com.kyawhtetzaw.currency.data.local.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import java.time.LocalDateTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeRateUpdaterTest {
    private val lastUpdateDataSource: LastUpdateDataSource = mockk()
    private val networkDataSource: NetworkDataSource = mockk()
    private val dao: ExchangeRateDao = mockk(relaxed = true)

    private lateinit var updater: ExchangeRateUpdater

    @Before
    fun setup() {
        updater = ExchangeRateUpdater(lastUpdateDataSource, networkDataSource, dao)
    }

    @Test
    fun `start emits Updating state when the last update time is null`() = runTest {
        coEvery { lastUpdateDataSource.getLastUpdateTime() } returns null

        val result = updater.start(10.seconds).take(1).toList()
        val expected = listOf(Updating)

        assertEquals(expected, result)
    }

    @Test
    fun `start emits Updating state when time since last update is greater than duration`() =
        runTest {
            coEvery { lastUpdateDataSource.getLastUpdateTime() } returns LocalDateTime.now()
                .minusSeconds(20)

            val result = updater.start(10.seconds).take(1).toList()
            val expected = listOf(Updating)

            assertEquals(expected, result)
        }

    @Test
    fun `start emits Timer state when time since last update is less than duration`() = runTest {
        coEvery { lastUpdateDataSource.getLastUpdateTime() } returns LocalDateTime.now()
            .minusSeconds(5)

        val result = updater.start(10.seconds).take(2).toList()
        val expected = listOf(Timer("00:05"), Timer("00:04"))

        assertEquals(expected, result)
    }

    @Test
    fun `start emits Success state when network request is successful`() = runTest {
        coEvery { lastUpdateDataSource.getLastUpdateTime() } returns LocalDateTime.now()
            .minusSeconds(20)
        coEvery { networkDataSource.latest() } returns mapOf("EUR" to 0.85, "GBP" to 0.73)
        coEvery { dao.insert(any()) } just Runs

        val result = updater.start(10.seconds).take(2).toList()
        val expected = listOf(Updating, Success)

        assertEquals(expected, result)
    }

    @Test
    fun `start emits Error state when network request fails`() = runTest {
        coEvery { lastUpdateDataSource.getLastUpdateTime() } returns LocalDateTime.now()
            .minusSeconds(20)
        val errorMessage = "Something went wrong."
        coEvery { networkDataSource.latest() } throws Exception(errorMessage)

        val result = updater.start(10.seconds).take(2).toList()
        val expected = listOf(Updating, ExchangeRateUpdateState.Error(errorMessage))

        assertEquals(expected, result)
    }
}
