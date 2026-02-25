package com.example.sharedsocial_kmp.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Implementazione di produzione di [AppDispatchers].
 * * Fornisce l'accesso ai dispatcher reali del framework Kotlin Coroutines per l'esecuzione
 * dei task nei thread appropriati (Main, IO, Default) durante il normale runtime dell'applicazione.
 */
class RealAppDispatchers : AppDispatchers {

    /**
     * Mappa al thread principale (Main Thread) per le operazioni UI e l'interazione con il framework.
     */
    override val main: CoroutineDispatcher = Dispatchers.Main

    /**
     * Mappa al pool di thread ottimizzato per operazioni di I/O (Network e Disk).
     */
    override val io: CoroutineDispatcher = Dispatchers.IO

    /**
     * Mappa al pool di thread ottimizzato per task computazionali intensivi.
     */
    override val default: CoroutineDispatcher = Dispatchers.Default
}