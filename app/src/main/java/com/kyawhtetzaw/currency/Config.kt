package com.kyawhtetzaw.currency

import kotlin.time.Duration.Companion.minutes

object Config {
    /**
     * The currency which exchange rates will be based on. This is currently stored here to reduce
     * development time. Should be stored in something like SharedPreferences or DataStore.
     */
    const val BaseCurrency = "USD"

    /**
     * Amount of time to hold of the update
     */
    val WaitInterval = 30.minutes
}