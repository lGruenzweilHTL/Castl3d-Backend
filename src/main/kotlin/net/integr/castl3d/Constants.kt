package net.integr.castl3d

class Constants {
    class Piece {
        companion object {
            const val PAWN = 0
            const val KNIGHT = 1
            const val BISHOP = 2
            const val ROOK = 3
            const val QUEEN = 4
            const val KING = 5
            const val NONE = -1
        }
    }

    class Color {
        companion object {
            const val WHITE = 10
            const val BLACK = 11
            const val NO_COLOR = 12
        }
    }
}