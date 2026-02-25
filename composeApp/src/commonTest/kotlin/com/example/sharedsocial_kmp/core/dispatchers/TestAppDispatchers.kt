package com.example.sharedsocial_kmp.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Implementazione di [AppDispatchers] per gli Unit Test.
 * Utilizza [UnconfinedTestDispatcher] per far sì che le coroutine vengano eseguite
 * immediatamente nel thread del test, evitando ritardi artificiali.
 */
class TestAppDispatchers @OptIn(ExperimentalCoroutinesApi::class) constructor(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : AppDispatchers {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}