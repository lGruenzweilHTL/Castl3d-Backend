package net.integr.castl3d.service.bot

import net.integr.castl3d.service.game.ChessBoard
import net.integr.castl3d.socket.packet.s2c.DebugS2CPacket
import net.integr.castl3d.socket.packet.s2c.SetBoardS2CPacket
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.security.Principal

open class Bot(val id: String, val name: String) {
    var messagingTemplate: SimpMessageSendingOperations? = null
    var user: Principal? = null

    open fun init() {

    }

    open fun move(board: ChessBoard) {

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
}
