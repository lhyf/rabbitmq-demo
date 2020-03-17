package org.lhyf.mq.test;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/****
 * @author YF
 * @date 2020-02-28 11:42
 * @desc RabbitMQTest
 *
 **/
public class RabbitMQTest {

    public static final String EXCHANGE_NAME = "TEST_EXC";
    public static final String ROUTING_KEY = "routing_key";


    public Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.31.201");
        factory.setPort(5672);
        factory.setVirtualHost("my_vhost");
        factory.setUsername("tom");
        factory.setPassword("123456");
        Connection connection = factory.newConnection();
        return connection;
    }


    @Test
    public void testDeclareChannel() throws IOException, TimeoutException {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, true, false, null);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);

        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
                new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(1)
                        .userId("hidden")
                        .build(),
                "Hello Lita".getBytes());

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("location", "here");
        headers.put("time", "today");
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
                new AMQP.BasicProperties.Builder()
                        .headers(headers)
                        .build(), "Hi Lita".getBytes());

        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
                new AMQP.BasicProperties.Builder()
                        .expiration("6000")
                        .build(), "Hello World".getBytes());


        // mandatory 参数测试
        channel.basicPublish(EXCHANGE_NAME, "", true, MessageProperties.PERSISTENT_TEXT_PLAIN,
                "mandatory test".getBytes());
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("投递失败的消息:" + msg);
            }
        });

        channel.close();
        connection.close();
    }
}
