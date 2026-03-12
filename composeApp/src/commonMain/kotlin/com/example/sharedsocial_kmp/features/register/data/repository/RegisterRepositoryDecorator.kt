package com.example.sharedsocial_kmp.features.register.data.repository

import com.example.sharedsocial_kmp.core.platform.AnalyticsService
import com.example.sharedsocial_kmp.features.register.domain.repository.RegisterRepository

/**
 * Decoratore di [RegisterRepository] che orchestra tracciamento analytics.
 */
class RegisterRepositoryDecorator(
    private val delegate: RegisterRepository,
    private val analytics: AnalyticsService,
) : RegisterRepository by delegate {

    override suspend fun register(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ): Result<String> {
        return delegate.register(name, surname, username, email, password).onSuccess {
            analytics.logEvent("register_success")

        }.onFailure { error ->
            analytics.recordNonFatalException(error)
            analytics.logEvent(
                "register_failure",
                mapOf("type" to (error::class.simpleName ?: "Unknown"))
            )
        }
    }
}