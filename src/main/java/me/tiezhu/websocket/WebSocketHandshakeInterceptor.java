package me.tiezhu.websocket;

import com.sun.net.httpserver.HttpPrincipal;
import me.tiezhu.websocket.Constants.Attribute;
import me.tiezhu.websocket.Constants.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 收集握手前后的信息
 * 也可以用来改变request
 */
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
        LOGGER.debug("before handshake URI:{}, headers:{}, params:{}", req.getURI(), req.getHeaders(), req.getServletRequest().getParameterNames());
        System.out.println("oh, " + ((WebSocketHandlerDecorator)wsHandler).getDelegate().getClass());

        // String user = request.getHeaders().getFirst(Header.INPUT_USER);
        String user = req.getServletRequest().getParameter(Param.USER);
        if (StringUtils.isNotEmpty(user)) {
            LOGGER.info("user {} handshaking {}", user, request.getURI());
            attributes.put(Attribute.PRINCIPAL, new HttpPrincipal(user, "default"));
        }

        if (req.getURI().toString().contains("/bad/")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        LOGGER.debug("after handshake", exception);
        response.getHeaders().add("X-HANDSHAKE", "after");
    }
}
