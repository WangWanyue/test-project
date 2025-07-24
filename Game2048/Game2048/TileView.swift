import SwiftUI

struct TileView: View {
    let number: Int
    let size: CGFloat = 70 // Define a fixed size for the tile for now

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 6)
                .fill(backgroundColor(for: number))

            if number != 0 {
                Text("\(number)")
                    .font(.system(size: 32, weight: .bold, design: .rounded))
                    .foregroundColor(foregroundColor(for: number))
            }
        }
        .frame(width: size, height: size)
    }

    private func backgroundColor(for number: Int) -> Color {
        switch number {
        case 0: return Color.gray.opacity(0.3) // Empty cell
        case 2: return Color(red: 0.93, green: 0.89, blue: 0.85) // #eee4da
        case 4: return Color(red: 0.93, green: 0.88, blue: 0.78) // #ede0c8
        case 8: return Color(red: 0.95, green: 0.69, blue: 0.47) // #f2b179
        case 16: return Color(red: 0.96, green: 0.59, blue: 0.40) // #f59563
        case 32: return Color(red: 0.96, green: 0.49, blue: 0.37) // #f67c5f
        case 64: return Color(red: 0.97, green: 0.37, blue: 0.23) // #f65e3b
        case 128: return Color(red: 0.93, green: 0.82, blue: 0.45) // #edcf72
        case 256: return Color(red: 0.93, green: 0.80, blue: 0.38) // #edcc61
        case 512: return Color(red: 0.93, green: 0.78, blue: 0.31) // #edc850
        case 1024: return Color(red: 0.93, green: 0.77, blue: 0.25) // #edc53f
        case 2048: return Color(red: 0.93, green: 0.76, blue: 0.18) // #edc22e
        default: return Color.black // Should not happen for standard 2048
        }
    }

    private func foregroundColor(for number: Int) -> Color {
        return number < 8 ? Color(red: 0.47, green: 0.43, blue: 0.40) : Color.white // #776e65 or white
    }
}

#Preview {
    VStack {
        TileView(number: 0)
        TileView(number: 2)
        TileView(number: 4)
        TileView(number: 8)
        TileView(number: 64)
        TileView(number: 1024)
    }
}
