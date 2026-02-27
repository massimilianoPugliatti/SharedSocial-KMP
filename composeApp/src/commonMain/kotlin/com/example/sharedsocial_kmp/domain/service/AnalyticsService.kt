package com.example.sharedsocial_kmp.domain.service

/**
 * Interfaccia per il tracking del comportamento dell' utente ed eventuali crash.
 */
interface AnalyticsService {
    fun logEvent(name: String, params: Map<String, String> = emptyMap())
    fun setUserId(userId: String)
    fun recordNonFatalException(throwable: Throwable)
}

