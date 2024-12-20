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

package net.integr.castl3d.service.bot.management

import net.integr.castl3d.rest.response.BotResponse
import net.integr.castl3d.rest.response.ListBotsResponse
import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.service.game.management.Castl3dBot
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.stereotype.Service
import java.lang.reflect.Constructor
import java.security.Principal

@Service
class BotManager {
    private val botCache = mutableMapOf<String, BotCacheEntry>()

    init {
        loadBotsToCache()
    }

    private final fun loadBotsToCache() {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(Bot::class.java))
        scanner.addIncludeFilter(AnnotationTypeFilter(Castl3dBot::class.java))

        val botClasses = scanner.findCandidateComponents("net.integr.castl3d.service.bot.impl")
            .map { Class.forName(it.beanClassName) }

        botClasses.forEach {
            val anno = it.getAnnotation(Castl3dBot::class.java)
            val entry = BotCacheEntry(anno.name, it.constructors.first())
            botCache[anno.id] = entry
        }
    }

    fun bootInstance(botId: String, user: Principal): Triple<Bot, String, String>? {
        val botCacheEntry = botCache[botId] ?: return null
        val botName = botCacheEntry.botName
        val bootedBot = botCacheEntry.constructor.newInstance() as Bot
        return Triple(bootedBot, botId, botName)
    }

    fun getBotsForList(): ListBotsResponse {
        return ListBotsResponse(botCache.map { BotResponse(it.key, it.value.botName) })
    }
}

data class BotCacheEntry(val botName: String, val constructor: Constructor<*>)