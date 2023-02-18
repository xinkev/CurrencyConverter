package com.kyawhtetzaw.currency

import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Success
import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Timer
import com.kyawhtetzaw.currency.ExchangeRateUpdateState.Updating
import com.kyawhtetzaw.currency.data.local.database.dao.ExchangeRateDao
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.data.network.NetworkDataSource
import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRate
import com.kyawhtetzaw.currency.data.network.model.toEntity
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExchangeRateUpdater @Inject constructor(
    private val lastUpdateDataSource: LastUpdateDataSource,
    private val networkDataSource: NetworkDataSource,
    private val dao: ExchangeRateDao
) {
    /**
     * @param duration amount of time to hold of the update
     */
    fun start(
        duration: Duration
    ): Flow<ExchangeRateUpdateState> = flow {
        val durationInSeconds = duration.inWholeSeconds

        var timePassed = lastUpdateDataSource.getLastUpdateTime()?.let {
            ChronoUnit.SECONDS.between(it, LocalDateTime.now())
        }

        while (true) {
            val allowToUpdate = timePassed == null || timePassed > durationInSeconds
            if (allowToUpdate) {
                emit(Updating)
                val result = updateExchangeRates()

                if (result.isSuccess) {
                    emit(Success)

                    // reset the time passed
                    timePassed = 0
                    continue
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Something went wrong."
                    emit(ExchangeRateUpdateState.Error(errorMessage))

                    currentCoroutineContext().cancel()
                }
            } else {
                delay(1.seconds)

                val time = formatTimer(durationInSeconds - timePassed!!)
                emit(Timer(time))

                timePassed += 1L
            }
        }
    }

    private fun formatTimer(time: Long): String {
        val min = time / 60
        val sec = time % 60
        return "%02d:%02d".format(min, sec)
    }

    private suspend fun updateExchangeRates(): Result<Unit> = runCatching {
        val now = LocalDateTime.now()
        val shouldUpdate = lastUpdateDataSource.getLastUpdateTime()?.let {
            ChronoUnit.MINUTES.between(it, now) >= 30
        } ?: true

        if (shouldUpdate) {
            val networkRates = networkDataSource.latest()
            dao.insert(*networkRates.map(NetworkExchangeRate::toEntity).toTypedArray())
            lastUpdateDataSource.saveTimestamp(now)
        }
    }
}

sealed interface ExchangeRateUpdateState {
    object Updating : ExchangeRateUpdateState
    object Success : ExchangeRateUpdateState
    data class Error(val msg: String) : ExchangeRateUpdateState
    data class Timer(val time: String) : ExchangeRateUpdateState
}