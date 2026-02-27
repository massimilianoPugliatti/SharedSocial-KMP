package com.example.sharedsocial_kmp.data.service

import cocoapods.FirebaseMessaging.FIRMessaging
import com.example.sharedsocial_kmp.domain.service.NotificationService
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * implementazione iOS di [NotificationService] utilizzando Firebase Messaging Pod.
 */
class IosNotificationService : NotificationService {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getPushToken(): String? = suspendCancellableCoroutine { continuation ->
        FIRMessaging.messaging().tokenWithCompletion { token, error ->
            if (error != null) {

                continuation.resume(null)
            } else {
                continuation.resume(token)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun subscribeToTopic(topic: String) {
        FIRMessaging.messaging().subscribeToTopic(topic)
    }
}