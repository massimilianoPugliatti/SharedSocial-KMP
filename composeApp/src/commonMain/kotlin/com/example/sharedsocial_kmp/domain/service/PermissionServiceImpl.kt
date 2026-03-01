package com.example.sharedsocial_kmp.domain.service

import com.mmk.kmpnotifier.notification.NotifierManager

class PermissionServiceImpl : PermissionService {
    override fun askNotificationPermission() {
        NotifierManager.getPermissionUtil().askNotificationPermission()
    }
}