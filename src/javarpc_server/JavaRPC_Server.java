/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpc_server;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;

/**
 *
 * @author AdminMobility
 */
public class JavaRPC_Server {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        
        ConnectionFactory factory = new ConnectionFactory();
        System.out.println(factory.getUsername()+" "+factory.getPassword());
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

        System.out.println(" [x] Awaiting RPC requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties
                                             .Builder()
                                             .correlationId(props.getCorrelationId())
                                             .build();

            String message = new String(delivery.getBody());
            
            System.out.println(" [.] convert(" + message + ")");
            String response = "" + convert(message);

            channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes());

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    private static String convert(String input) {
        return input;
    }
    
}

