package net.integr.castl3d.socket.config

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal

class AnonymousHandshakeHandler : DefaultHandshakeHandler() {
    override fun determineUser(request: ServerHttpRequest, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Principal? {
        if (request is ServletServerHttpRequest) {
            val session = request.servletRequest.session
            return Principal { session.id }
        } else {
            return null
        }
    }
}