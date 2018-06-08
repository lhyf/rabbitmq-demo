package org.lhyf.mq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.lhyf.mq.utils.MQConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 交换器在匹配模式下：
 * 如果消费者端的路由关键字只使用【#】来匹配消息，在匹配【topic】模式下，它会变成一个分发【fanout】模式，接收所有消息。
 * 如果消费者端的路由关键字中没有【#】或者【*】，它就变成直连【direct】模式来工作。
 */
public class TopicSend {
    private static final String EXCHANG_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null;

        try {

            connection = MQConnectionUtil.getConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANG_NAME, "topic");
            // routingKeys
            String[] routingKeys = new String[]{"quick.orange.rabbit",
                    "lazy.orange.elephant",
                    "quick.orange.fox",
                    "lazy.brown.fox",
                    "quick.brown.fox",
                    "quick.orange.male.rabbit",
                    "lazy.orange.male.rabbit",
                    "", // * 无法匹配 ""
                    ".."};  // #. 可以匹配 ..

            for (String severity : routingKeys) {
                String message = "From " + severity + " routingKey' s message!";
                channel.basicPublish(EXCHANG_NAME, severity, null, message.getBytes());
                System.out.println("TopicSend [x] Sent '" + severity + "':'" + message + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

}
