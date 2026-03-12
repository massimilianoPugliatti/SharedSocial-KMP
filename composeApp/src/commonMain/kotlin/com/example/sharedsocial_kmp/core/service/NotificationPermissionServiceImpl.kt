package com.example.sharedsocial_kmp.core.service

import com.mmk.kmpnotifier.notification.NotifierManager

class NotificationPermissionServiceImpl : NotificationPermissionService {
    override fun askNotificationPermission() {
        NotifierManager.getPermissionUtil().askNotificationPermission()
    }
}