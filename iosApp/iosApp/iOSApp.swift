import SwiftUI

@main
struct iOSApp: App {
init() {
        iOSModuleKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}