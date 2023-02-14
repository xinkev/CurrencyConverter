package com.kyawhtetzaw.currency.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatchers: MyDispatchers)

enum class MyDispatchers {
    IO
}