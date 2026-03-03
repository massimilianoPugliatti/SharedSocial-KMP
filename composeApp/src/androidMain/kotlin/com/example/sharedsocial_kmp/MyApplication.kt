package com.example.sharedsocial_kmp

import android.app.Application
import com.example.sharedsocial_kmp.di.androidModule
import com.example.sharedsocial_kmp.core.di.commonModule
import com.example.sharedsocial_kmp.core.network.networkModule
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true
            )
        )
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(commonModule , androidModule , networkModule)
        }
    }
}