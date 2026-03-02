package com.example.sharedsocial_kmp.data.repository

import com.example.sharedsocial_kmp.data.remote.dto.FirebaseTokenRequest
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.domain.service.AnalyticsService
import com.example.sharedsocial_kmp.domain.service.PermissionService
import com.mmk.kmpnotifier.notification.PushNotifier
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Decoratore di [AuthRepository] che integra tracciamento analytics e registrazione token push.
 */
class ServiceAuthRepositoryDecorator(
    private val delegate: AuthRepository,
    private val analytics: AnalyticsService,
    private val pushNotifier: PushNotifier,
    private val httpClient: HttpClient,
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
            analytics.logEvent("login_failure", mapOf("type" to error::class.simpleName.orEmpty()))
        }
    }

    override suspend fun logout(): Result<Unit> {
        pushNotifier.deleteMyToken()
        return delegate.logout().onSuccess {
            analytics.logEvent("logout_success")
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
        }
    }

    private suspend fun registerFirebaseToken() {
        runCatching {
            val token = pushNotifier.getToken()
            if (!token.isNullOrBlank()) {
                httpClient.post("Utente/sendFirebaseToken") {
                    contentType(ContentType.Application.Json)
                    setBody(FirebaseTokenRequest(firebaseToken = token))
                }
            }
        }
    }
}