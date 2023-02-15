package com.kyawhtetzaw.currency.usecase

import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import java.time.LocalDateTime
import javax.inject.Inject

class GetLastUpdatedTime @Inject constructor(
    private val lastUpdateDataSource: LastUpdateDataSource
) {
    suspend operator fun invoke(): LocalDateTime? = lastUpdateDataSource.getLastUpdateTime()
}