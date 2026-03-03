package com.example.sharedsocial_kmp.features.auth.data.repository

import com.example.sharedsocial_kmp.features.auth.domain.model.User
import com.example.sharedsocial_kmp.features.auth.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.core.service.AnalyticsService
import com.example.sharedsocial_kmp.core.service.PermissionService
import com.mmk.kmpnotifier.notification.PushNotifier

/**
 * Decoratore di [AuthRepository] che orchestra tracciamento analytics,
 * permessi di sistema e registrazione del token push.
 */
class AuthRepositoryDecorator(
    private val delegate: AuthRepository,
    private val analytics: AnalyticsService,
    private val pushNotifier: PushNotifier,
    private val permissionService: PermissionService
) : AuthRepository by delegate {

    override suspend fun login(email: String, password: String): Result<User> {
        return delegate.login(email, password).onSuccess { user ->
            analytics.setUserId(user.id)
            analytics.logEvent("login_success")

            registerFirebaseToken()
            permissionService.askNotificationPermission()
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
            analytics.logEvent("login_failure", mapOf("type" to (error::class.simpleName ?: "Unknown")))
        }
    }

    override suspend fun logout(): Result<Unit> {
        runCatching { pushNotifier.deleteMyToken() }

        return delegate.logout().onSuccess {
            analytics.logEvent("logout_success")
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
        }
    }

    /**
     * Recupera il token FCM e delega l'invio al repository.
     */
    private suspend fun registerFirebaseToken() {
        runCatching {
            val token = pushNotifier.getToken()
            if (!token.isNullOrBlank()) {
                delegate.registerPushToken(token).onFailure { error ->
                    analytics.recordNonFatalException(error)
                    analytics.logEvent("push_token_registration_failed")
                }
            }
        }
    }
}