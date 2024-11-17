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

package net.integr.castl3d.service.game.management

import net.integr.castl3d.service.bot.management.BotManager
import net.integr.castl3d.service.game.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class GameManager @Autowired constructor(private val botManager: BotManager, private val messagingTemplate: SimpMessageSendingOperations) {
    val games = mutableMapOf<String, Game>()

    fun startGame(id: String, user: Principal): Game? {
        val bot = botManager.bootInstance(id, user) ?: return null
        val game = Game(bot, messagingTemplate, user)

        games[user.name] = game
        return game
    }

    fun getGame(user: Principal): Game? {
        return games[user.name]
    }
}