package com.hhl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq三---将所有消息发给每个消费者
 * Created by hanlin.huang on 2017/5/24.
 */
public class DemoRec03_1 {

    private static final String QUEUE_NAME = "HHL_TEST_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();   //创建连接工厂
        factory.setHost("118.190.44.59");   //设置服务器IP
        factory.setUsername("bbyshp");
        factory.setPassword("bqjr@2017");
        Connection connection = factory.newConnection(); //ConnectionFactory建立连接
        Channel channel = connection.createChannel();   //创建通道
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  //定义队列(队列名称、队列是否持久化、是否是此连接的唯一队列、是否自动删除


/*

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);  //接收（队列名称，自动回复，回调）
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();  //得到交付
            String message = new String(delivery.getBody()); //得到交付的消息
            System.out.println(" [x] Received '" + message + "'");
        }
*/



        channel.exchangeDeclare(QUEUE_NAME, "fanout");
        String queueName = "log-fb1";  //队列1名称
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, QUEUE_NAME, "");//把Queue、Exchange绑定
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
        }


    }
}
