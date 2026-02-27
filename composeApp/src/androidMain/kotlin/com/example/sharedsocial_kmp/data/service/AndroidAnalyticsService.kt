package com.example.sharedsocial_kmp.data.service

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.sharedsocial_kmp.domain.service.AnalyticsService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidAnalyticsService(
    context: Context,
    private val isDebug: Boolean
) : AnalyticsService {

    private val analytics: FirebaseAnalytics? = try {
        FirebaseAnalytics.getInstance(context)
    } catch (e: Exception) {
        Log.w("AnalyticsService", "Firebase Analytics non disponibile: ${e.message}")
        null
    }

    private val crashlytics: FirebaseCrashlytics? = try {
        FirebaseCrashlytics.getInstance()
    } catch (e: Exception) {
        Log.w("AnalyticsService", "Firebase Crashlytics non disponibile")
        null
    }

    override fun logEvent(name: String, params: Map<String, String>) {
        if (isDebug) {
            // Un recruiter vedrà questo nel Logcat anche senza Firebase funzionante
            Log.d("SocialKMP_Debug", "📊 Evento: $name | Parametri: $params")
        }

        analytics?.let { fb ->
            val bundle = Bundle()
            params.forEach { (key, value) -> bundle.putString(key, value) }
            fb.logEvent(name, bundle) // Chiamata all'SDK reale
        }
    }

    override fun setUserId(userId: String) {
        if (isDebug) Log.d("SocialKMP_Debug", "👤 UserID impostato: $userId")
        analytics?.setUserId(userId)
        crashlytics?.setUserId(userId)
    }

    override fun recordNonFatalException(throwable: Throwable) {
        if (isDebug) {
            Log.e("SocialKMP_Error", "❌ Errore intercettato: ${throwable.message}", throwable)
        } else {
            crashlytics?.recordException(throwable)
        }
    }
}