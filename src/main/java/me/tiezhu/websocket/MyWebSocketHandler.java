package me.tiezhu.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 这个handler似乎没有生效
 */
public class MyWebSocketHandler extends WebSocketHandlerDecorator {

    public MyWebSocketHandler(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("here," + session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("or here," + status);
        super.afterConnectionClosed(session, status);
    }
}
