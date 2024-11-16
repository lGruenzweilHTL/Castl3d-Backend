package net.integr.castl3d.service.game

import net.integr.castl3d.Constants
import net.integr.castl3d.socket.packet.s2c.DebugS2CPacket
import net.integr.castl3d.socket.packet.s2c.GameEndS2CPacket
import net.integr.castl3d.socket.packet.s2c.SetBoardS2CPacket
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.security.Principal

class ChessBoard {
    private var board = Array(8) { Array(8) { ChessFieldPieceData(0, 0, 0, false) } }

    private var moveValidator = MoveValidator(this)

    var messagingTemplate: SimpMessageSendingOperations? = null
    var user: Principal? = null

    private val userColor = Constants.Color.WHITE
    private val botColor = Constants.Color.BLACK

    init {
        // White
        set(0, 0, Constants.Piece.ROOK, botColor, 0, false)
        set(1, 0, Constants.Piece.KNIGHT, botColor, 0, false)
        set(2, 0, Constants.Piece.BISHOP, botColor, 0, false)
        set(3, 0, Constants.Piece.QUEEN, botColor, 0, false)
        set(4, 0, Constants.Piece.KING, botColor, 0, false)
        set(5, 0, Constants.Piece.BISHOP, botColor, 0, false)
        set(6, 0, Constants.Piece.KNIGHT, botColor, 0, false)
        set(7, 0, Constants.Piece.ROOK, botColor, 0, false)

        set(0, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(1, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(2, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(3, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(4, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(5, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(6, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)
        set(7, 1, Constants.Piece.PAWN, Constants.Color.WHITE, 0, false)

        // Black
        set(0, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(1, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(2, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(3, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(4, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(5, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(6, 6, Constants.Piece.PAWN, userColor, 0, false)
        set(7, 6, Constants.Piece.PAWN, userColor, 0, false)

        set(0, 7, Constants.Piece.ROOK, userColor, 0, false)
        set(1, 7, Constants.Piece.KNIGHT, userColor, 0, false)
        set(2, 7, Constants.Piece.BISHOP, userColor, 0, false)
        set(3, 7, Constants.Piece.QUEEN, userColor, 0, false)
        set(4, 7, Constants.Piece.KING, userColor, 0, false)
        set(5, 7, Constants.Piece.BISHOP, userColor, 0, false)
        set(6, 7, Constants.Piece.KNIGHT, userColor, 0, false)
        set(7, 7, Constants.Piece.ROOK, userColor, 0, false)
    }

    fun get(x: Int, y: Int): ChessFieldPieceData {
        return board[x][y]
    }

    private fun set(x: Int, y: Int, piece: Int, color: Int, moveCount: Int, hasJustMoved: Boolean) {
        board[x][y].piece = piece
        board[x][y].color = color
        board[x][y].moveCount = moveCount
        board[x][y].hasJustMoved = hasJustMoved

        updateBoard(x, y, piece, color, moveCount, hasJustMoved)
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

    fun move(x: Int, y: Int, newX: Int, newY: Int): Boolean {
        val valid = moveValidator.getValidMoves(x, y).any { it.first == newX && it.second == newY }
        if (!valid) return false

        val oldData = get(x, y)
        wipeJustMoved(oldData.color)
        set(newX, newY, oldData.piece, oldData.color, oldData.moveCount + 1, true)
        clear(x, y)

        return true
    }

    fun move(move: Move): Boolean {
        return move(move.from.x, move.from.y, move.to.x, move.to.y)
    }

    fun testFor(func: ChessBoard.() -> Boolean): Boolean {
        return copy().func()
    }

    fun testForUserCheckmate(move: Move): Boolean {
        return testFor {
            move(move)
            isUserCheckmate()
        }
    }

    fun testForBotCheckmate(move: Move): Boolean {
        return testFor {
            move(move)
            isBotCheckmate()
        }
    }

    private fun copy(): ChessBoard {
        val newBoard = ChessBoard()
        for (i in 0..7) {
            for (j in 0..7) {
                newBoard.set(i, j, get(i, j).piece, get(i, j).color, get(i, j).moveCount, get(i, j).hasJustMoved)
            }
        }

        return newBoard
    }

    private fun getAllValidMoves(color: Int): List<Move> {
        val moveList = mutableListOf<Move>()
        for (i in 0..7) {
            for (j in 0..7) {
                if (get(i, j).color != color) continue
                val moves = moveValidator.getValidMoves(i, j)
                for (move in moves) {
                    val isCapture = get(move.first, move.second).color != Constants.Color.NO_COLOR
                    moveList.add(Move(Coordinate(i, j), Coordinate(move.first, move.second), isCapture))
                }
            }
        }

        return moveList
    }

    fun getAllValidBotMoves(): List<Move> {
        return getAllValidMoves(userColor)
    }

    fun getAllValidUserMoves(): List<Move> {
        return getAllValidMoves(botColor)
    }

    private fun getKingPosition(color: Int): Coordinate {
        for (i in 0..7) {
            for (j in 0..7) {
                if (get(i, j).isKing() && get(i, j).color == color) {
                    return Coordinate(i, j)
                }
            }
        }

        return Coordinate(-1, -1)
    }

    fun getBotKingPosition(): Coordinate {
        return getKingPosition(userColor)
    }

    fun getUserKingPosition(): Coordinate {
        return getKingPosition(botColor)
    }

    private fun isInCheck(color: Int): Boolean {
        val kingPosition = getKingPosition(color)
        val opponentColor = if (color == Constants.Color.WHITE) Constants.Color.BLACK else Constants.Color.WHITE
        for (i in 0..7) {
            for (j in 0..7) {
                if (get(i, j).color == opponentColor) {
                    val validMoves = moveValidator.getValidMoves(i, j)
                    if (validMoves.any { it.first == kingPosition.x && it.second == kingPosition.y }) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun isBotInCheck(): Boolean {
        return isInCheck(userColor)
    }

    fun isUserInCheck(): Boolean {
        return isInCheck(botColor)
    }

    private fun isCheckmate(color: Int): Boolean {
        if (!isInCheck(color)) return false

        val kingPosition = getKingPosition(color)
        val validMoves = moveValidator.getValidMoves(kingPosition.x, kingPosition.y)
        return validMoves.isEmpty()
    }

    fun isBotCheckmate(): Boolean {
        return isCheckmate(userColor)
    }

    fun isUserCheckmate(): Boolean {
        return isCheckmate(botColor)
    }

    private fun isStalemate(color: Int): Boolean {
        if (isInCheck(color)) return false

        val validMoves = getAllValidMoves(color)
        return validMoves.isEmpty()
    }

    fun isBotStalemate(): Boolean {
        return isStalemate(userColor)
    }

    fun isUserStalemate(): Boolean {
        return isStalemate(botColor)
    }

    fun getWinner(): Int {
        if (isCheckmate(Constants.Color.WHITE)) return Constants.Color.BLACK
        if (isCheckmate(Constants.Color.BLACK)) return Constants.Color.WHITE
        if (isStalemate(Constants.Color.WHITE) || isStalemate(Constants.Color.BLACK)) return Constants.Color.NO_COLOR

        return Constants.Color.NO_COLOR
    }

    private fun send(message: Any) {
        messagingTemplate!!.convertAndSendToUser(user!!.name, "/private/bot_receiver", message)
    }

    private fun updateBoard(x: Int, y: Int, piece: Int, color: Int, moveCount: Int, hasJustMoved: Boolean) {
        send(SetBoardS2CPacket(x, y, piece, color, moveCount, hasJustMoved))
    }

    fun debug(message: String) {
        send(DebugS2CPacket(message))
    }

    fun announceWinner(winner: Int) {
        send(DebugS2CPacket("Game over! Winner: $winner"))
        send(GameEndS2CPacket(winner))
    }
}

