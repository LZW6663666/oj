package com.lzw.ojbackendjudgeservice.rabbitmq;

import com.lzw.ojbackendjudgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MyMessageConsumer 类用于消费 RabbitMQ 中的消息。
 * 该类使用 RabbitMQ 的 @RabbitListener 注解来定义一个消息监听器，专门监听名为 "code_queue" 的队列。
 */
@Component
@Slf4j
public class MyMessageConsumer {
    @Resource
    private JudgeService judgeService;

    /**
     * 接收来自 "code_queue" 队列的消息。
     * 使用手动确认模式（ACK），在消息处理完成后，需要显式地确认消息已被处理。
     *
     * @param message 消息内容，以字符串形式传递。
     * @param channel RabbitMQ 的 Channel，用于与消息队列进行通信，如确认消息处理等。
     * @param deliveryTag 消息的唯一标识，用于确认消息是否已被处理。
     */
    //指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 记录接收到的消息信息
        log.info("receiveMessage message ={}", message);
        long questionSubmitId= Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            // 确认消息处理完成，将 deliveryTag 与 multiple 参数设为 false，表示只确认当前消息。
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            // 确认消息处理失败，将 deliveryTag 与 multiple 参数设为 false，表示只确认当前消息。
            // 否定确认（Nack）消息，通知消息代理消息处理失败
            channel.basicNack(deliveryTag, false, false);

        }


    }
}

