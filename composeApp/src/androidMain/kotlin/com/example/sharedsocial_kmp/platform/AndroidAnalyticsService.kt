package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.sharedsocial_kmp.core.platform.AnalyticsService
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
    } catch (_: Exception) {
        Log.w("AnalyticsService", "Firebase Crashlytics non disponibile")
        null
    }

    override fun logEvent(name: String, params: Map<String, String>) {
        if (isDebug) {
            Log.d("SocialKMP_Debug", "📊 Evento: $name | Parametri: $params")
        }

        analytics?.let { fb ->
            val bundle = Bundle()
            params.forEach { (key, value) -> bundle.putString(key, value) }
            fb.logEvent(name, bundle)
        }
    }

    override fun setUserId(userId: Long) {
        if (isDebug) Log.d("SocialKMP_Debug", "👤 UserID impostato: $userId")
        analytics?.setUserId(userId.toString())
        crashlytics?.setUserId(userId.toString())
    }

    override fun recordNonFatalException(throwable: Throwable) {
        if (isDebug) {
            Log.e("SocialKMP_Error", "❌ Errore intercettato: ${throwable.message}", throwable)
        } else {
            crashlytics?.recordException(throwable)
        }
    }
}