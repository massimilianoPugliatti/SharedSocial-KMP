package com.example.sharedsocial_kmp.core.platform

/**
 * Interfaccia per il tracking del comportamento dell' utente ed eventuali crash.
 */
interface AnalyticsService {
    fun logEvent(name: String, params: Map<String, String> = emptyMap())
    fun setUserId(userId: Long)
    fun recordNonFatalException(throwable: Throwable)
}

