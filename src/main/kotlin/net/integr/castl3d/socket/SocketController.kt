package net.integr.castl3d.socket

import net.integr.castl3d.Constants
import net.integr.castl3d.socket.packet.c2s.MoveC2SPacket
import net.integr.castl3d.socket.packet.c2s.StartGameC2SPacket
import net.integr.castl3d.socket.packet.s2c.SetBoardS2CPacket
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller

@Controller
class SocketController {
    @MessageMapping("/start_game")
    @SendToUser("/private/move_receiver")
    fun startGame(message: StartGameC2SPacket): SetBoardS2CPacket {
        return SetBoardS2CPacket(0, 0, Constants.Piece.QUEEN, Constants.Color.WHITE, 1, false)
    }

    @MessageMapping("/move")
    @SendToUser("/private/move_receiver")
    fun move(message: MoveC2SPacket): SetBoardS2CPacket {
        return SetBoardS2CPacket(0, 0, Constants.Piece.QUEEN, Constants.Color.WHITE, 1, false)
    }
}