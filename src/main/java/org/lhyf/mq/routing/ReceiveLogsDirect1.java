package org.lhyf.mq.routing;

import com.rabbitmq.client.*;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect1 {

    private static final String EXCHANGE_NAME = "direct_logs";

    // 路由关键字
    private static final String[] routingKeys = new String[]{"info", "warning"};

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //获取匿名队列名称
        String queueName = channel.queueDeclare().getQueue();

        for (String severity : routingKeys) {
            channel.queueBind(queueName,EXCHANGE_NAME,severity);
            System.out.println("ReceiveLogsDirect1 exchange:"+EXCHANGE_NAME+", queue:"+queueName+", BindRoutingKey:" + severity);

        }

        System.out.println("ReceiveLogsDirect1 [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }



}
