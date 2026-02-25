package com.example.sharedsocial_kmp.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Astrazione dei Dispatcher delle Coroutine per l'intera applicazione.
 * * Questa interfaccia permette il disaccoppiamento dalle implementazioni standard di [kotlinx.coroutines.Dispatchers],
 * facilitando l'iniezione di [kotlinx.coroutines.test.TestDispatcher] durante gli unit test
 * e garantendo un comportamento deterministico della concorrenza.
 */
interface AppDispatchers {
    /**
     * Dispatcher per le operazioni sulla UI (Main Thread).
     */
    val main: CoroutineDispatcher

    /**
     * Dispatcher ottimizzato per le operazioni di I/O (Network, Database, File System).
     */
    val io: CoroutineDispatcher

    /**
     * Dispatcher per task intensivi a livello di CPU.
     */
    val default: CoroutineDispatcher
}