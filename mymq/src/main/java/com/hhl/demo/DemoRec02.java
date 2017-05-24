package com.hhl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq二---均匀分配消息给每个接收者，prefetchCount设置均匀分配的个数
 * Created by hanlin.huang on 2017/5/24.
 */
public class DemoRec02 {

    private static final String QUEUE_NAME = "HHL_TEST_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();   //创建连接工厂
        factory.setHost("118.190.44.59");   //设置服务器IP
        factory.setUsername("bbyshp");
        factory.setPassword("bqjr@2017");
        Connection connection = factory.newConnection(); //ConnectionFactory建立连接
        Channel channel = connection.createChannel();   //创建通道
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  //定义队列(队列名称、队列是否持久化、是否是此连接的唯一队列、是否自动删除



     /*   QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);  //接收（队列名称，自动回复，回调）
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();  //得到交付
            String message = new String(delivery.getBody()); //得到交付的消息
            System.out.println(" [x] Received '" + message + "'");
        }*/

        channel.basicQos(1);//告诉RabbitMQ同一时间给一个消息给消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();//得到交付
            String message = new String(delivery.getBody()); //得到交付的消息
            System.out.println(" [x] Received '" + message + "'");
            doWork(message);
            System.out.println(" [x] Done");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//下一个消息
        }







    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()) {
            if (ch == '.')
                Thread.sleep(1000);//这里是假装我们很忙
        }
    }

}
