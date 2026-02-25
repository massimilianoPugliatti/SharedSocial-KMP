package com.example.sharedsocial_kmp

import android.app.Application
import com.example.sharedsocial_kmp.di.androidModule
import com.example.sharedsocial_kmp.di.commonModule
import com.example.sharedsocial_kmp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(commonModule , androidModule , networkModule)
        }
    }
}