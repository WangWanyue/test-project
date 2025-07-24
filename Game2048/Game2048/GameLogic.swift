import Foundation
import SwiftUI // For ObservableObject and @Published

class GameLogic: ObservableObject {
    @Published var grid: [[Int]]
    @Published var score: Int
    @Published var isGameOverAlertPresented = false
    @Published var didWinAlertPresented = false

    private let gridSize = 4
    private let gridKey = "gameGrid"
    private let scoreKey = "gameScore"

    init() {
        self.grid = Array(repeating: Array(repeating: 0, count: gridSize), count: gridSize)
        self.score = 0
        // Attempt to load game first. If fails, start fresh.
        if !loadGame() {
            // Start the game with two tiles if no saved data
            addNewTile()
            addNewTile()
        }
    }

    // MARK: - Persistence
    func saveGame() {
        let encoder = JSONEncoder()
        if let encodedGrid = try? encoder.encode(grid) {
            UserDefaults.standard.set(encodedGrid, forKey: gridKey)
        }
        UserDefaults.standard.set(score, forKey: scoreKey)
        // print("Game saved!") // For debugging
    }

    @discardableResult // To suppress warning if called from init and result isn't used
    func loadGame() -> Bool {
        let decoder = JSONDecoder()
        if let savedGridData = UserDefaults.standard.data(forKey: gridKey),
           let decodedGrid = try? decoder.decode([[Int]].self, from: savedGridData) {
            self.grid = decodedGrid
            self.score = UserDefaults.standard.integer(forKey: scoreKey) // score is Int
            // print("Game loaded!") // For debugging
            return true
        }
        // print("No saved game found or failed to load.") // For debugging
        return false
    }

    // MARK: - Tile Management

    private func getRandomEmptyPosition() -> (row: Int, col: Int)? {
        var emptyPositions: [(Int, Int)] = []
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                if grid[r][c] == 0 {
                    emptyPositions.append((r, c))
                }
            }
        }
        return emptyPositions.randomElement()
    }

    func addNewTile() {
        guard let position = getRandomEmptyPosition() else {
            // No empty space, should not happen in a normal game unless it's full
            return
        }
        // 90% chance of 2, 10% chance of 4
        let randomNumber = Int.random(in: 1...10)
        grid[position.row][position.col] = (randomNumber == 1) ? 4 : 2
    }

    // MARK: - Movement Mechanics

    // Helper function to process a single line (row or column)
    // Returns the new line and the score achieved in this line
    private func processLine(line: [Int]) -> (newLine: [Int], lineScore: Int) {
        var newLine = line.filter { $0 != 0 } // Compact: remove zeros
        var lineScore = 0
        var mergedNewLine: [Int] = []

        var i = 0
        while i < newLine.count {
            if i + 1 < newLine.count && newLine[i] == newLine[i+1] {
                // Merge
                let mergedValue = newLine[i] * 2
                mergedNewLine.append(mergedValue)
                lineScore += mergedValue
                i += 2 // Skip the next tile as it's merged
            } else {
                mergedNewLine.append(newLine[i])
                i += 1
            }
        }

        // Fill remaining space with zeros
        while mergedNewLine.count < gridSize {
            mergedNewLine.append(0)
        }

        return (mergedNewLine, lineScore)
    }

    func moveLeft() -> Bool {
        var moved = false
        var currentScore = 0
        var newGrid = grid

        for r in 0..<gridSize {
            let (processedLine, lineScore) = processLine(line: newGrid[r])
            if newGrid[r] != processedLine {
                moved = true
            }
            newGrid[r] = processedLine
            currentScore += lineScore
        }

        if moved {
            grid = newGrid
            score += currentScore
            addNewTile()
            checkGameStatus()
        }
        return moved
    }

    func moveRight() -> Bool {
        var moved = false
        var currentScore = 0
        var newGrid = grid

        for r in 0..<gridSize {
            let reversedLine = newGrid[r].reversed()
            let (processedLineReversed, lineScore) = processLine(line: Array(reversedLine))
            let finalLine = processedLineReversed.reversed()
            if newGrid[r] != Array(finalLine) {
                moved = true
            }
            newGrid[r] = Array(finalLine)
            currentScore += lineScore
        }

        if moved {
            grid = newGrid
            score += currentScore
            addNewTile()
            checkGameStatus()
        }
        return moved
    }

    // Helper to rotate grid 90 degrees clockwise
    private func rotateGridClockwise(_ currentGrid: [[Int]]) -> [[Int]] {
        var rotatedGrid = Array(repeating: Array(repeating: 0, count: gridSize), count: gridSize)
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                rotatedGrid[c][gridSize - 1 - r] = currentGrid[r][c]
            }
        }
        return rotatedGrid
    }

    // Helper to rotate grid 90 degrees counter-clockwise
    private func rotateGridCounterClockwise(_ currentGrid: [[Int]]) -> [[Int]] {
        var rotatedGrid = Array(repeating: Array(repeating: 0, count: gridSize), count: gridSize)
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                rotatedGrid[gridSize - 1 - c][r] = currentGrid[r][c]
            }
        }
        return rotatedGrid
    }

    func moveUp() -> Bool {
        var moved = false
        var currentScore = 0

        // Rotate grid so 'up' becomes 'left'
        var rotatedGrid = rotateGridCounterClockwise(self.grid)

        for r in 0..<gridSize {
            let (processedLine, lineScore) = processLine(line: rotatedGrid[r])
            if rotatedGrid[r] != processedLine {
                moved = true
            }
            rotatedGrid[r] = processedLine
            currentScore += lineScore
        }

        if moved {
            // Rotate back
            self.grid = rotateGridClockwise(rotatedGrid)
            self.score += currentScore
            addNewTile()
            checkGameStatus()
        }
        return moved
    }

    func moveDown() -> Bool {
        var moved = false
        var currentScore = 0

        // Rotate grid so 'down' becomes 'left'
        var rotatedGrid = rotateGridClockwise(self.grid) // Rotate right (clockwise)
                                                         // so that moving left on this equals moving down on original

        for r in 0..<gridSize {
            let (processedLine, lineScore) = processLine(line: rotatedGrid[r])
            if rotatedGrid[r] != processedLine {
                moved = true
            }
            rotatedGrid[r] = processedLine
            currentScore += lineScore
        }

        if moved {
            // Rotate back
            self.grid = rotateGridCounterClockwise(rotatedGrid) // Rotate left (counter-clockwise) to restore
            self.score += currentScore
            addNewTile()
            checkGameStatus()
        }
        return moved
    }


    // MARK: - Game State

    func isGameOver() -> Bool {
        // Check for empty cells
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                if grid[r][c] == 0 {
                    return false // Found an empty cell
                }
            }
        }

        // No empty cells, check for possible merges
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                // Check horizontal merge (right)
                if c < gridSize - 1 && grid[r][c] == grid[r][c+1] {
                    return false // Found a possible horizontal merge
                }
                // Check vertical merge (down)
                if r < gridSize - 1 && grid[r][c] == grid[r+1][c] {
                    return false // Found a possible vertical merge
                }
            }
        }

        // No empty cells and no possible merges
        return true
    }

    func didWin() -> Bool {
        // Check if any tile is 2048
        for r in 0..<gridSize {
            for c in 0..<gridSize {
                if grid[r][c] == 2048 {
                    return true
                }
            }
        }
        return false
    }

    private func checkGameStatus() {
        if didWin() {
            // Only present win alert if game over is not already presented (e.g. winning move also fills board)
            if !isGameOverAlertPresented {
                didWinAlertPresented = true
            }
        } else if isGameOver() {
            // Check if any tile can be added. If not, it's truly game over.
            // This specific check is tricky here as addNewTile was already called.
            // The isGameOver() function itself should be robust.
            isGameOverAlertPresented = true
        }
    }

    // For debugging or resetting
    func resetGame() {
        self.grid = Array(repeating: Array(repeating: 0, count: gridSize), count: gridSize)
        self.score = 0
        self.isGameOverAlertPresented = false
        self.didWinAlertPresented = false
        addNewTile()
        addNewTile()
        // Optionally save this clean state if desired, or assume save happens on backgrounding
    }
}
