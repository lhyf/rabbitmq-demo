package org.lhyf.mq.topic;

import com.rabbitmq.client.*;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic1 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        /*
        匹配交换器的匹配符
        *（星号）表示一个单词
        #（井号）表示零个或者多个单词
         */
        //路由关键字
        String[] routingkeys = new String[]{"*.orange.*","*"};
        //绑定路由关键字
        for (String bindingKey : routingkeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
            System.out.println("ReceiveLogsTopic1 exchange:" + EXCHANGE_NAME + ", queue:" + queueName + ", BindRoutingKey:" + bindingKey);
        }
        System.out.println("ReceiveLogsTopic1 [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("ReceiveLogsTopic1 [x] Received '" + envelope.getRoutingKey() + "':'" + msg + "'");
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}
