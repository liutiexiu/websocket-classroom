package me.tiezhu.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by liushuai on 2017/9/6.
 */
@EnableRabbit
@Configuration
public class QueueConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConfig.class);

    public static final String Q_NAME_CLASSROOM = "classroom";
    public static final String Q_NAME_USER = "user";

    @Bean
    public Queue classroomQueue() {
        return new Queue(Q_NAME_CLASSROOM);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(Q_NAME_USER);
    }

    @Bean
    public MessageConverter messageConverter() {
        MyMessageConverter converter = new MyMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }

    public static class MyMessageConverter extends SimpleMessageConverter {
        private static ObjectMapper mapper = new ObjectMapper();

        @Override
        protected Message createMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
            if (object.getClass().getName().startsWith("me.tiezhu")) {
                LOGGER.debug("queue payload {}, use myMessageConverter", object);

                try {
                    messageProperties = messageProperties == null ? new MessageProperties() : messageProperties;
                    messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    messageProperties.setHeader("_CLASS", object.getClass().getName());

                    return new Message(mapper.writeValueAsBytes(object), messageProperties);
                } catch (JsonProcessingException e) {
                    LOGGER.error("fail to convert to json, {}", object, e);
                }
            }

            return super.createMessage(object, messageProperties);
        }

        public Object fromMessage(Message message) throws MessageConversionException {
            MessageProperties messageProperties = message.getMessageProperties();
            Object className = messageProperties == null ? null : messageProperties.getHeaders().get("_CLASS");
            if (className != null) {
                LOGGER.debug("queue message {}, use myMessageConverter", message);

                try {
                    return mapper.readValue(new String(message.getBody()), Class.forName(className.toString()));
                } catch (ClassNotFoundException | IOException e) {
                    LOGGER.error("fail to convert from queue, {}", message, e);
                }
            }
            return super.fromMessage(message);
        }
    }
}
