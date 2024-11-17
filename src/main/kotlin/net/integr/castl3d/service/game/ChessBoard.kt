/*
 * Copyright © 2024 Integr
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package net.integr.castl3d.service.game

import net.integr.castl3d.Constants
import net.integr.castl3d.service.game.management.HasAlreadyMovedException
import net.integr.castl3d.socket.packet.s2c.DebugS2CPacket
import net.integr.castl3d.socket.packet.s2c.GameEndS2CPacket
import net.integr.castl3d.socket.packet.s2c.SetBoardS2CPacket
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.security.Principal
import kotlin.jvm.Throws

/**
 * A class representing a chess board.
 * @constructor Initializes the board with the starting positions of the pieces.
 */
class ChessBoard(val messagingTemplate: SimpMessageSendingOperations?, val user: Principal?) {
    var board = Array(8) { Array(8) { ChessFieldPieceData(Constants.Piece.NONE, Constants.Color.NO_COLOR, 0, false) } }

    private var moveValidator = MoveValidator(this)

    val userColor = Constants.Color.WHITE
    val botColor = Constants.Color.BLACK

    var currentMover = userColor
    var hasMovedOnce = false

    init {
        // Bot
        set(0, 0, Constants.Piece.ROOK, botColor, 0, false)
        set(1, 0, Constants.Piece.KNIGHT, botColor, 0, false)
        set(2, 0, Constants.Piece.BISHOP, botColor, 0, false)
        set(3, 0, Constants.Piece.QUEEN, botColor, 0, false)
        set(4, 0, Constants.Piece.KING, botColor, 0, false)
        set(5, 0, Constants.Piece.BISHOP, botColor, 0, false)
        set(6, 0, Constants.Piece.KNIGHT, botColor, 0, false)
        set(7, 0, Constants.Piece.ROOK, botColor, 0, false)

        set(0, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(1, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(2, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(3, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(4, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(5, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(6, 1, Constants.Piece.PAWN, botColor, 0, false)
        set(7, 1, Constants.Piece.PAWN, botColor, 0, false)

        // User
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

    /**
     * Get the piece at the specified position.
     * @param x The x coordinate of the piece.
     * @param y The y coordinate of the piece.
     * @return The piece at the specified position.
     * @see ChessFieldPieceData
     */
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

    /**
     * Move a piece from one position to another.
     * @param x The x coordinate of the piece to move.
     * @param y The y coordinate of the piece to move.
     * @param newX The x coordinate to move the piece to.
     * @param newY The y coordinate to move the piece to.
     * @return True if the move was successful, false otherwise.
     * @see Move
     * @throws HasAlreadyMovedException If the bot has already moved.
     */
    @Throws(HasAlreadyMovedException::class)
    fun move(x: Int, y: Int, newX: Int, newY: Int): Boolean {
        if (currentMover != get(x, y).color) return false
        if (hasMovedOnce) throw HasAlreadyMovedException()
        val moves = moveValidator.getValidMoves(x, y)
        val valid = moves.any { it.first == newX && it.second == newY }
        if (!valid) return false

        val oldData = get(x, y)
        wipeJustMoved(oldData.color)
        set(newX, newY, oldData.piece, oldData.color, oldData.moveCount + 1, true)
        clear(x, y)
        currentMover = if (currentMover == botColor) userColor else botColor
        hasMovedOnce = true
        return true
    }

    /**
     * Move a piece from one position to another.
     * @param move The move to make.
     * @return True if the move was successful, false otherwise.
     * @see Move
     * @throws HasAlreadyMovedException If the bot has already moved.
     */
    @Throws(HasAlreadyMovedException::class)
    fun move(move: Move): Boolean {
        return move(move.from.x, move.from.y, move.to.x, move.to.y)
    }

    /**
     * Test for scenarios in a copy of the board without modifying the original.
     * @param func The function to test for.
     * @return True if the function returns true, false otherwise.
     * @see ChessBoard
     */
    fun testFor(func: ChessBoard.() -> Boolean): Boolean {
        return copy().func()
    }

    /**
     * Test for a user checkmate in a copy of the board without modifying the original.
     * @param move The move to make.
     * @return True if the user is in checkmate, false otherwise.
     * @see Move
     */
    fun testForUserCheckmate(move: Move): Boolean {
        return testFor {
            move(move)
            isUserCheckmate()
        }
    }

    /**
     * Test for a bot checkmate in a copy of the board without modifying the original.
     * @param move The move to make.
     * @return True if the bot is in checkmate, false otherwise.
     * @see Move
     */
    fun testForBotCheckmate(move: Move): Boolean {
        return testFor {
            move(move)
            isBotCheckmate()
        }
    }

    private fun copy(): ChessBoard {
        val newBoard = ChessBoard(null, null)
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

    /**
     * Get all valid moves for the bot.
     * @return A list of all valid moves.
     * @see Move
     */
    fun getAllValidBotMoves(): List<Move> {
        return getAllValidMoves(botColor)
    }

    /**
     * Get all valid moves for the user.
     * @return A list of all valid moves.
     * @see Move
     */
    fun getAllValidUserMoves(): List<Move> {
        return getAllValidMoves(userColor)
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

    /**
     * Get the position of the bot's king.
     * @return The position of the bot's king.
     * @see Coordinate
     */
    fun getBotKingPosition(): Coordinate {
        return getKingPosition(botColor)
    }

    /**
     * Get the position of the user's king.
     * @return The position of the user's king.
     * @see Coordinate
     */
    fun getUserKingPosition(): Coordinate {
        return getKingPosition(userColor)
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

    /**
     * Check if the bot is in check.
     * @return True if the bot is in check, false otherwise.
     */
    fun isBotInCheck(): Boolean {
        return isInCheck(botColor)
    }

    /**
     * Check if the user is in check.
     * @return True if the user is in check, false otherwise.
     */
    fun isUserInCheck(): Boolean {
        return isInCheck(userColor)
    }

    private fun isCheckmate(color: Int): Boolean {
        if (!isInCheck(color)) return false

        val kingPosition = getKingPosition(color)
        val validMoves = moveValidator.getValidMoves(kingPosition.x, kingPosition.y)
        return validMoves.isEmpty()
    }

    /**
     * Check if the bot is in checkmate.
     * @return True if the bot is in checkmate, false otherwise.
     */
    fun isBotCheckmate(): Boolean {
        return isCheckmate(botColor)
    }

    /**
     * Check if the user is in checkmate.
     * @return True if the user is in checkmate, false otherwise.
     */
    fun isUserCheckmate(): Boolean {
        return isCheckmate(userColor)
    }

    private fun isStalemate(color: Int): Boolean {
        if (isInCheck(color)) return false

        val validMoves = getAllValidMoves(color)
        return validMoves.isEmpty()
    }

    /**
     * Check if the bot is in stalemate.
     * @return True if the bot is in stalemate, false otherwise.
     */
    fun isBotStalemate(): Boolean {
        return isStalemate(botColor)
    }

    /**
     * Check if the user is in stalemate.
     * @return True if the user is in stalemate, false otherwise.
     */
    fun isUserStalemate(): Boolean {
        return isStalemate(userColor)
    }

    /**
     * Get the winner of the game.
     * @return The color of the winner.
     * @see Constants.Color
     */
    fun getWinner(): Int {
        if (!kingExists(Constants.Color.WHITE)) return Constants.Color.BLACK
        if (!kingExists(Constants.Color.BLACK)) return Constants.Color.WHITE
        if (isCheckmate(Constants.Color.WHITE)) return Constants.Color.BLACK
        if (isCheckmate(Constants.Color.BLACK)) return Constants.Color.WHITE
        if (isStalemate(Constants.Color.WHITE) || isStalemate(Constants.Color.BLACK)) return Constants.Color.ANY_COLOR

        return Constants.Color.NO_COLOR
    }

    private fun kingExists(color: Int): Boolean {
        val kp = getKingPosition(color)
        return !(kp.x == -1 && kp.y == -1)
    }

    /**
     * Check if the bot's king exists.
     * @return True if the bot's king exists, false otherwise.
     */
    fun botKingExists(): Boolean {
        return kingExists(botColor)
    }

    /**
     * Check if the user's king exists.
     * @return True if the user's king exists, false otherwise.
     */
    fun userKingExists(): Boolean {
        return kingExists(userColor)
    }

    private fun send(message: Any, dest: String) {
        if (messagingTemplate == null) throw IllegalStateException("Messaging template not set!")
        messagingTemplate.convertAndSendToUser(user!!.name, "/private/receiver/$dest", message)
    }

    private fun updateBoard(x: Int, y: Int, piece: Int, color: Int, moveCount: Int, hasJustMoved: Boolean) {
        send(SetBoardS2CPacket(x, y, piece, color, moveCount, hasJustMoved), "set_board")
    }

    /**
     * Send a debug message to the user.
     * @param message The message to send.
     */
    fun debug(message: String) {
        send(DebugS2CPacket(message), "debug")
    }

    /**
     * Send a message to the user announcing the winner.
     * @param winner The color of the winner.
     * @see Constants.Color
     */
    fun announceWinner(winner: Int) {
        debug("Game over! Winner: ${if (winner == Constants.Color.WHITE) "White" else "Black"}")
        send(GameEndS2CPacket(winner), "game_end")
    }
}

