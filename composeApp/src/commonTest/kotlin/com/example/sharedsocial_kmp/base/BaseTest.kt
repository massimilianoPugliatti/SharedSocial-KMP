package com.example.sharedsocial_kmp.base

import com.example.sharedsocial_kmp.core.dispatchers.TestAppDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Classe base per tutti i test unitari che coinvolgono coroutine e ViewModel.
 * Configura il dispatcher Main per l'ambiente di test, garantendo che le
 * operazioni asincrone vengano eseguite in modo deterministico.
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class BaseTest {

    protected val appDispatchers = TestAppDispatchers()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(appDispatchers.testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}