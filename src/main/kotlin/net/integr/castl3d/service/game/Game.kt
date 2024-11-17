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