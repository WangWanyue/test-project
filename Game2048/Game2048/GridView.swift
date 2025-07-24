import SwiftUI

struct GridView: View {
    let board: [[Int]]
    let cellSize: CGFloat = 70 // Should match TileView's size
    let spacing: CGFloat = 10

    var body: some View {
        VStack(spacing: spacing) {
            ForEach(0..<board.count, id: \.self) { rowIndex in
                HStack(spacing: spacing) {
                    ForEach(0..<self.board[rowIndex].count, id: \.self) { columnIndex in
                        TileView(number: self.board[rowIndex][columnIndex], size: self.cellSize)
                    }
                }
            }
        }
        .padding(spacing)
        .background(Color.gray.opacity(0.8)) // Background for the grid container
        .cornerRadius(8)
    }
}

#Preview {
    GridView(board: [
        [0, 2, 4, 8],
        [16, 32, 64, 128],
        [256, 512, 1024, 2048],
        [0, 0, 0, 0]
    ])
}
