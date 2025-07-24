import SwiftUI

@main
struct Game2048App: App {
    @StateObject private var gameLogic = GameLogic()
    @Environment(\.scenePhase) var scenePhase

    var body: some Scene {
        WindowGroup {
            ContentView(game: gameLogic) // Pass the GameLogic instance
        }
        .onChange(of: scenePhase) { oldPhase, newPhase in
            if newPhase == .inactive || newPhase == .background {
                gameLogic.saveGame()
            }
        }
    }
}
