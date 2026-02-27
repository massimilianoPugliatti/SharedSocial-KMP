package com.example.sharedsocial_kmp.domain.service

/**
 * Interfccia per gestire le notifiche push
 */
interface NotificationService {
    suspend fun getPushToken(): String?
    fun subscribeToTopic(topic: String)
}