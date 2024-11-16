package net.integr.castl3d.service.game

import net.integr.castl3d.Constants
import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.socket.packet.c2s.MoveC2SPacket
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.security.Principal

class Game(val bot: Bot, messagingTemplate: SimpMessageSendingOperations, user: Principal) {
    private var board = ChessBoard(messagingTemplate, user)

    fun handleUserMove(pack: MoveC2SPacket): Boolean {
        if (board.currentMover == board.botColor) return false

        board.hasMovedOnce = false
        val retVal = board.move(pack.fromX, pack.fromY, pack.toX, pack.toY)

        val winner = board.getWinner()

        if (winner != Constants.Color.NO_COLOR) {
            board.announceWinner(winner)
            return retVal
        }

        handleBotMove()
        return retVal
    }

    private fun handleBotMove() {
        if (board.currentMover != board.botColor) return
        board.hasMovedOnce = false
        bot.move(board)

        val winner = board.getWinner()

        if (winner != Constants.Color.NO_COLOR) {
            board.announceWinner(winner)
            return
        }
    }
}