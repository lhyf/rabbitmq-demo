package org.lhyf.mq.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RoutingSendDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    //路由关键字
    private static final String[] routingKeys = new String[]{"info", "warning", "error"};

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = MQConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        for (String severity :routingKeys) {
            String message = "Send the message level:" + severity;
            channel.basicPublish(EXCHANGE_NAME,severity,null,message.getBytes());
            System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
        }

        channel.close();
        connection.close();
    }


}
