package org.lhyf.mq.publish;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();

        Channel channel = connection.createChannel();


        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        for(int i =0;i<50;i++){
            String msg = "["+i+"] +Hello World!";
            channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes());
            System.out.println("P [x] Sent '" + msg + "'");
        }

        channel.close();
        connection.close();
    }


}
