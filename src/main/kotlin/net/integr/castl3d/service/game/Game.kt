package net.integr.castl3d.service.game

import net.integr.castl3d.Constants
import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.socket.packet.c2s.MoveC2SPacket
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.security.Principal

class Game(val bot: Bot) {
    var messagingTemplate: SimpMessageSendingOperations? = null
    var user: Principal? = null

    private var botIsMoving = false

    private var board = ChessBoard()

    init {
        board.messagingTemplate = messagingTemplate
        board.user = user
    }

    fun handleUserMove(pack: MoveC2SPacket) {
        if (botIsMoving) return
        board.move(pack.fromX, pack.fromY, pack.toX, pack.toY)
        botIsMoving = true

        val winner = board.getWinner()

        if (winner != Constants.Color.NO_COLOR) {
            board.announceWinner(winner)
            return
        }

        handleBotMove()
    }

    private fun handleBotMove() {
        if (!botIsMoving) return
        bot.move(board)
        botIsMoving = false

        val winner = board.getWinner()

        if (winner != Constants.Color.NO_COLOR) {
            board.announceWinner(winner)
            return
        }
    }
}