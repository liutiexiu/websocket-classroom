package me.tiezhu;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import me.tiezhu.model.MsgGreeting;
import me.tiezhu.model.HelloMessage;
import me.tiezhu.model.MsgInClassroom;
import me.tiezhu.model.MsgTimeSpend;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClassroomIntegrationTests {

    @LocalServerPort
    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testOneStudentClassroom() throws Exception {
        Thread.currentThread().setName("main-test");

        final long startTime = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(2);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionHandler(failure) {

            {
                Thread.currentThread().setName("client-handler-thread");
            }

            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                System.out.println("client after connected, headers:" + connectedHeaders);
                System.out.println(" sessionId:" + session.getSessionId());

                session.subscribe("/user/personal/msg", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        System.out.println("client got headers:" + headers);
                        if ("MsgTimeSpend".equals(headers.getFirst("type"))) {
                            latch.countDown();
                            return MsgTimeSpend.class;
                        } else {
                            return MsgGreeting.class;
                        }
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        try {
                            System.out.println("client /personal/msg got " + payload);
                        } catch (Throwable t) {
                            failure.set(t);
                        }
                    }
                });

                session.subscribe("/user/classroom/sync", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        System.out.println("client got headers:" + headers);
                        return MsgInClassroom.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MsgInClassroom msgInClassroom = (MsgInClassroom) payload;

                        try {
                            System.out.println("client /classroom/sync got " + msgInClassroom);
                        } catch (Throwable t) {
                            failure.set(t);
                        }
                    }
                });

                try {
                    session.send("/app/online", new HelloMessage("Spring"));
                } catch (Throwable t) {
                    failure.set(t);
                }
            }
        };

        // headers.add("X-HTTP-USER", "student4");
        this.stompClient.connect("ws://localhost:{port}/msg/websocket?user=student4", this.headers, handler, this.port);

        if (latch.await(40, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("failed", failure.get());
            } else {
                System.out.println("done in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
            }
        } else {
            fail("fail");
        }

    }

    private class TestSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;


        public TestSessionHandler(AtomicReference<Throwable> failure) {
            this.failure = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
        }
    }
}
