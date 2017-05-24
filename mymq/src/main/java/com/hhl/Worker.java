package com.hhl;

/**
 * Created by hanlin.huang on 2017/5/17.
 */
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class Worker {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws java.io.IOException, java.lang.InterruptedException, TimeoutException {

/*
        #fk
        #服务器IP
        mq.fk.host=10.83.22.94
        #TCP端口号，默认是5672
        mq.fk.port=5672
        #生产者服务器用户名
        mq.fk.user=fls
        #生产者服务器密码
        mq.fk.password=!QAZxsw2
                */

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.83.22.94");
        factory.setUsername("fls");
        factory.setPassword("!QAZxsw2");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 指定队列持久化
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 指定该消费者同时只接收一条消息
        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);

        // 打开消息应答机制
        channel.basicConsume(TASK_QUEUE_NAME, true, consumer);

       // while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            System.out.println(" [x] Received '" + message + "'");
            doWork(message);
            System.out.println(" [x] Done");

            // 返回接收到消息的确认信息
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
       // }
    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()) {
            if (ch == '.')
                Thread.sleep(1000);
        }
    }
}