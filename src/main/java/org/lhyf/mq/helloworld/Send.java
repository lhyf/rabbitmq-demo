package org.lhyf.mq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private static final String QUEUE_NAME = "queue_simple";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取一个连接
        Connection connection = MQConnectionUtil.getConnection();

        //从连接中创建通道
        Channel channel = connection.createChannel();

        //创建队列(声明)
        boolean durable = false;
        boolean exclusive = false;
        boolean autoDelete = false;

        //声明一个队列 -- 在RabbitMQ中，队列声明是幂等性的（一个幂等操作的特点是其任意多次执行所产生的影响均与一次执行的影响相同），也就是说，如果不存在，就创建，如果存在，不会对已经存在的队列产生任何影响。
        channel.queueDeclare(QUEUE_NAME, durable, exclusive, autoDelete, null);

        String msg = "Hello World!";

        channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());

        System.out.println("P [x] Sent '" + msg + "'");

        //关闭通道与连接
        channel.close();
        connection.close();
    }

}
