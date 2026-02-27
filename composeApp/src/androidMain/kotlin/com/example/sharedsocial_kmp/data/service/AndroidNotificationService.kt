package com.example.sharedsocial_kmp.data.service

import com.example.sharedsocial_kmp.domain.service.NotificationService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class AndroidNotificationService : NotificationService {
    override suspend fun getPushToken(): String? = 
        FirebaseMessaging.getInstance().token.await()

    override fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }
}