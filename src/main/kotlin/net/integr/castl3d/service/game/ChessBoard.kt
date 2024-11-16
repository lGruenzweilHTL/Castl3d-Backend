package net.integr.castl3d.service.game

import net.integr.castl3d.Constants

class ChessBoard(init: ChessBoard.() -> Unit) {
    private var board = Array(8) { Array(8) { ChessFieldPieceData(0, 0, 0, false) } }

    private var moveValidator = MoveValidator(this)

    init {
        this.init()
    }

    fun get(x: Int, y: Int): ChessFieldPieceData {
        return board[x][y]
    }

    fun set(x: Int, y: Int, piece: Int, color: Int, moveCount: Int, hasJustMoved: Boolean) {
        board[x][y].piece = piece
        board[x][y].color = color
        board[x][y].moveCount = moveCount
        board[x][y].hasJustMoved = hasJustMoved
    }

    private fun wipeJustMoved(color: Int) {
        for (x in 0..7) {
            for (y in 0..7) {
                if (board[x][y].color == color) board[x][y].hasJustMoved = false
            }
        }
    }

    private fun clear(x: Int, y: Int) {
        set(x, y, Constants.Piece.NONE, Constants.Color.NO_COLOR, 0, false)
    }

    fun move(x: Int, y: Int, newX: Int, newY: Int) {
        val valid = moveValidator.getValidMoves(x, y).any { it.first == newX && it.second == newY }
        if (!valid) return

        val oldData = get(x, y)
        wipeJustMoved(oldData.color)
        set(newX, newY, oldData.piece, oldData.color, oldData.moveCount + 1, true)
        clear(x, y)
    }
}

