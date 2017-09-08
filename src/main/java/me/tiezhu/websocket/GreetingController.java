package me.tiezhu.websocket;

import me.tiezhu.model.Greeting;
import me.tiezhu.model.HelloMessage;
import me.tiezhu.queue.QueueReceiver;
import me.tiezhu.queue.QueueSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private QueueSender queueSender;

    @Autowired
    private QueueReceiver queueReceiver;

    @GetMapping("/test")
    @ResponseBody
    public String testGet() {
        return "@json:{\"code\":0}";
    }

    // to /app/hello
    @MessageMapping("/hello")
    @SendToUser(value = "/topic/greetings", broadcast = false)
    // @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message, @Header("simpSessionId") String sessionId, final Principal principal) throws Exception {
        System.out.println("simpSessionId=" + sessionId);
        System.out.println("principal=" + principal);
        queueSender.sentToHelloQueue("lalala");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setName("push thread");
                    Thread.sleep(3500);
                    template.convertAndSendToUser(principal.getName(), "/topic/greetings", new Greeting("Pre Hello"));
                    // template.convertAndSend("/topic/greetings", new Greeting("Pre Hello"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }

}
