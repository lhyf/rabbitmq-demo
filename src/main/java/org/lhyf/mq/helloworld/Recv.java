package org.lhyf.mq.helloworld;

import com.rabbitmq.client.*;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    private static final String QUEUE_NAME = "queue_simple";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        //声明要关注的队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        System.out.println("C [*] Waiting for messages. To exit press CTRL+C");

        //DefaultConsumer类实现了Consumer接口，通过传入一个通道，告诉服务器我们需要那个通道的消息，
        // 如果通道中有消息，就会执行回调函数handleDelivery
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);

                System.out.println("C [x] Received '" + msg + "'");
            }
        };
        //自动回复队列应答 -- RabbitMQ中的消息确认机制
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}
