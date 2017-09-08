package me.tiezhu.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 注册自己的WebSocketHandler会返回400
 * TODO 如果能自己注册，就可以处理连接问题了
 */
@Deprecated
// @Component
public class MyWebSocketHandler {
}
