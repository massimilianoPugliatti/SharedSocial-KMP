import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
init() {
        iOSModuleKt.doInitKoin()
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}