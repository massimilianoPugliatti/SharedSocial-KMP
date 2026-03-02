//
//  IosAnalyticsService.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 01/03/26.
//


import Foundation
import FirebaseAnalytics
import FirebaseCrashlytics
import ComposeApp

class IosAnalyticsService: NSObject, AnalyticsService {
    
    private let isDebug: Bool
    
    init(isDebug: Bool) {
        self.isDebug = isDebug
        super.init()
    }
    
    func logEvent(name: String, params: [String : String]) {
        if isDebug {
            print("🍎 [Analytics iOS]: \(name) | Params: \(params)")
        }
        
        Analytics.logEvent(name, parameters: params)
    }
    
    func setUserId(userId: String) {
        Analytics.setUserID(userId)
        Crashlytics.crashlytics().setUserID(userId)
    }
    
    func recordNonFatalException(throwable: KotlinThrowable) {
        if isDebug {
            print("❌ [iOS Error]: \(throwable.message ?? "Unknown Error")")
            return
        }
        
        let userInfo: [String: Any] = [
            "KotlinStackTrace": throwable.getStackTrace(),
            NSLocalizedDescriptionKey: throwable.message ?? "Unknown Kotlin Error"
        ]
        
        let error = NSError(
            domain: "com.social.app.KotlinError",
            code: 0,
            userInfo: userInfo
        )
        
        Crashlytics.crashlytics().record(error: error)
    }
}
