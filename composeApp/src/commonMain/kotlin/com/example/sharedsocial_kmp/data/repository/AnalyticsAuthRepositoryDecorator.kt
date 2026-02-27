package com.example.sharedsocial_kmp.data.repository

import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.domain.service.AnalyticsService

class AnalyticsAuthRepositoryDecorator(
    private val delegate: AuthRepository,
    private val analytics: AnalyticsService
) : AuthRepository by delegate {

    override suspend fun login(email: String, password: String): Result<User> {
        return delegate.login(email, password).onSuccess { user ->
            analytics.setUserId(user.id)
            analytics.logEvent("login_success")
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
            analytics.logEvent("login_failure", mapOf("type" to error::class.simpleName.orEmpty()))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return delegate.logout().onSuccess {
            analytics.logEvent("logout_success")
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
        }
    }
}