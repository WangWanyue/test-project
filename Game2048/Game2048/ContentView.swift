import SwiftUI

struct ContentView: View {
    @ObservedObject var game: GameLogic // Receive GameLogic as an ObservedObject

    var body: some View {
        VStack(spacing: 20) {
            HStack {
                VStack(alignment: .leading) {
                    Text("2048")
                        .font(.system(size: 60, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.47, green: 0.43, blue: 0.40)) // #776e65
                    Text("Join the numbers and get to the 2048 tile!")
                        .font(.subheadline)
                        .foregroundColor(Color(red: 0.47, green: 0.43, blue: 0.40))
                }
                Spacer()
                // Score display
                Text("Score: \(game.score)") // Use game.score
                    .font(.title)
                    .fontWeight(.bold)
                    .padding(.horizontal)
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(8)
            }

            // Game Board
            GridView(board: game.grid) // Use game.grid
                .gesture(
                    DragGesture(minimumDistance: 20, coordinateSpace: .local)
                        .onEnded { value in
                            handleSwipe(translation: value.translation)
                        }
                )

            Button("New Game") {
                game.resetGame()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 10)
            .background(Color(red: 0.57, green: 0.53, blue: 0.49)) // #90887C
            .foregroundColor(.white)
            .font(.headline)
            .cornerRadius(8)

            Spacer() // Pushes content to the top
        }
        .padding()
        .background(Color(red: 0.98, green: 0.97, blue: 0.95)) // Background for the whole view #faf8ef
        .edgesIgnoringSafeArea(.all)
        .alert("Game Over!", isPresented: $game.isGameOverAlertPresented) {
            Button("New Game") {
                game.resetGame()
                // game.isGameOverAlertPresented = false // SwiftUI handles this
            }
        } message: {
            Text("Final Score: \(game.score)")
        }
        .alert("You Win!", isPresented: $game.didWinAlertPresented) {
            Button("Keep Playing") {
                // game.didWinAlertPresented = false // SwiftUI handles this
            }
            Button("New Game") {
                game.resetGame()
                // game.didWinAlertPresented = false // SwiftUI handles this
            }
        } message: {
            Text("Congratulations! You reached 2048! Your score is \(game.score).")
        }
    }

    private func handleSwipe(translation: CGSize) {
        // let absDx = abs(translation.width) // No longer need moved here
        // let absDy = abs(translation.height)
        // var moved = false // This is now handled within GameLogic

        if abs(translation.width) > abs(translation.height) { // Horizontal swipe
            if translation.width > 0 {
                _ = game.moveRight() // Result not needed here anymore
            } else {
                _ = game.moveLeft()
            }
        } else { // Vertical swipe
            if translation.height > 0 {
                _ = game.moveDown()
            } else {
                _ = game.moveUp()
            }
        }

        // Movement and adding new tile logic is now handled within GameLogic's move functions.
        // Game status (win/over) is also checked there.
    }
}

#Preview {
    // For the preview, we need to provide a GameLogic instance.
    // This won't use saved data from UserDefaults in the preview,
    // but will allow the preview to build.
    ContentView(game: GameLogic())
}
