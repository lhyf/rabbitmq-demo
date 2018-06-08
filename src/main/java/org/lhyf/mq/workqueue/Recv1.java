package org.lhyf.mq.workqueue;

import com.rabbitmq.client.*;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv1 {
    private static final String QUEUE_NAME = "workqueue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //声明要关注的队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        System.out.println("Recv1 [*] Waiting for messages. To exit press CTRL+C");
        // 每次从队列中获取数量 , 可以起到负载均衡的作用
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                try {
                    doWork(msg);
                } finally {
                    System.out.println("Recv1 Done");

                    //消息处理完成确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //手动答复
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }

    private static void doWork(String msg) {
        try {
            System.out.println("Recv1 Received '" + msg + "'");
            Thread.sleep(1000); // 暂停1秒钟
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }

}
