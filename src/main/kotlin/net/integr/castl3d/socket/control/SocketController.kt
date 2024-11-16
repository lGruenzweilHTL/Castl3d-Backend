package net.integr.castl3d.socket.control

import net.integr.castl3d.service.game.management.GameManager
import net.integr.castl3d.socket.packet.c2s.MoveC2SPacket
import net.integr.castl3d.socket.packet.c2s.StartGameC2SPacket
import net.integr.castl3d.socket.packet.s2c.DebugS2CPacket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class SocketController @Autowired constructor(val gameManager: GameManager) {
    @MessageMapping("/start_game")
    @SendToUser("/private/receiver/debug")
    fun startGame(message: StartGameC2SPacket, user: Principal): DebugS2CPacket {
        val id = message.botId
        val game = gameManager.startGame(id, user) ?: throw IllegalArgumentException("Bot not found for id $id")

        return DebugS2CPacket("Bot [${game.bot.name}:${game.bot.id}] started! Initializing game...")
    }

    @MessageMapping("/move")
    @SendToUser("/private/receiver/debug")
    fun move(message: MoveC2SPacket, user: Principal): DebugS2CPacket {
        val game = gameManager.getGame(user) ?: throw IllegalArgumentException("Game not found for user ${user.name}")
        game.handleUserMove(message)
        return DebugS2CPacket("Move registered! Please wait for the bot to respond!")
    }
}