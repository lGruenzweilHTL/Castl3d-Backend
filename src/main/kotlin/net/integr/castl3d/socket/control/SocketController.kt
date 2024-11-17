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
        val (_, botId, botName) = gameManager.startGame(id, user) ?: return DebugS2CPacket("Bot not found for id $id")

        return DebugS2CPacket("Bot [${botName}:${botId}] started successfully!")
    }

    @MessageMapping("/move")
    @SendToUser("/private/receiver/debug")
    fun move(message: MoveC2SPacket, user: Principal): DebugS2CPacket {
        val game = gameManager.getGame(user) ?: return DebugS2CPacket("No game found for user ${user.name}")
        val accepted = game.handleUserMove(message)

        return DebugS2CPacket("Move [${message.fromX}:${message.fromY} -> ${message.toX}:${message.toY}] ${if (accepted) "accepted" else "rejected"}!")
    }
}