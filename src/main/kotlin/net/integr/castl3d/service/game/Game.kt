package net.integr.castl3d.service.game

import net.integr.castl3d.Constants
import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.socket.packet.c2s.MoveC2SPacket

class Game(val bot: Bot) {
    var botIsMoving = false

    var board = ChessBoard {
        // White
        set(0, 0, Constants.Piece.ROOK, Constants.Color.WHITE, 0, false)
        set(1, 0, Constants.Piece.KNIGHT, Constants.Color.WHITE, 0, false)
        set(2, 0, Constants.Piece.BISHOP, Constants.Color.WHITE, 0, false)
        set(3, 0, Constants.Piece.QUEEN, Constants.Color.WHITE, 0, false)
        set(4, 0, Constants.Piece.KING, Constants.Color.WHITE, 0, false)
        set(5, 0, Constants.Piece.BISHOP, Constants.Color.WHITE, 0, false)
        set(6, 0, Constants.Piece.KNIGHT, Constants.Color.WHITE, 0, false)
        set(7, 0, Constants.Piece.ROOK, Constants.Color.WHITE, 0, false)

        set(0, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(1, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(2, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(3, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(4, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(5, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(6, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(7, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)

        // Black
        set(0, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(1, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(2, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(3, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(4, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(5, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(6, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)
        set(7, 6, Constants.Piece.PAWN, Constants.Color.BLACK, 0, false)

        set(0, 7, Constants.Piece.ROOK, Constants.Color.BLACK, 0, false)
        set(1, 7, Constants.Piece.KNIGHT, Constants.Color.BLACK, 0, false)
        set(2, 7, Constants.Piece.BISHOP, Constants.Color.BLACK, 0, false)
        set(3, 7, Constants.Piece.QUEEN, Constants.Color.BLACK, 0, false)
        set(4, 7, Constants.Piece.KING, Constants.Color.BLACK, 0, false)
        set(5, 7, Constants.Piece.BISHOP, Constants.Color.BLACK, 0, false)
        set(6, 7, Constants.Piece.KNIGHT, Constants.Color.BLACK, 0, false)
        set(7, 7, Constants.Piece.ROOK, Constants.Color.BLACK, 0, false)
    }

    fun start() {
        bot.init()
    }

    fun move() {
        bot.move(board)
    }

    fun handleUserMove(pack: MoveC2SPacket) {
        if (botIsMoving) return

        botIsMoving = true
    }
}