package com.example.sharedsocial_kmp.data.service

import cocoapods.FirebaseAnalytics.FIRAnalytics
import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import com.example.sharedsocial_kmp.domain.service.AnalyticsService
import platform.Foundation.NSLog
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

class IosAnalyticsService(private val isDebug: Boolean) : AnalyticsService {

    override fun logEvent(name: String, params: Map<String, String>) {
        if (isDebug) {
            // NSLog è lo standard iOS per i log di sistema
            NSLog("🍎 [Analytics iOS]: %s | Params: %s", name, params.toString())
        }

        // L'SDK iOS di Firebase è "null-safe" per design se non inizializzato
        FIRAnalytics.logEventWithName(name, parameters = params as Map<Any?, *>)
    }

    override fun setUserId(userId: String) {
        FIRAnalytics.setUserID(userId)
        FIRCrashlytics.crashlytics().setUserID(userId)
    }

    override fun recordNonFatalException(throwable: Throwable) {
        if (isDebug) {
            NSLog("❌ [iOS Error]: %s", throwable.message ?: "Unknown Error")
            return
        }

        // Mapping dello stacktrace Kotlin in un NSError per Crashlytics
        val userInfo = mutableMapOf<Any?, Any?>().apply {
            put("KotlinStackTrace", throwable.stackTraceToString())
            put(NSLocalizedDescriptionKey, throwable.message ?: "Unknown Kotlin Error")
        }

        val error = NSError.errorWithDomain("com.social.app.KotlinError", 0, userInfo)
        FIRCrashlytics.crashlytics().recordError(error)
    }
}