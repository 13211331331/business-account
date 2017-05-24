package com.hhl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq五---接收符合特定路由规则的消息
 * Created by hanlin.huang on 2017/5/24.
 */
public class DemoSend05 {

    private static final String QUEUE_NAME = "HHL_TEST_QUEUE_5";


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();   //创建连接工厂
        factory.setHost("118.190.44.59");   //设置服务器IP
        factory.setUsername("bbyshp");
        factory.setPassword("bqjr@2017");
        Connection connection = factory.newConnection(); //ConnectionFactory建立连接
        Channel channel = connection.createChannel();   //创建通道
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  //定义队列(队列名称、队列是否持久化、是否是此连接的唯一队列、是否自动删除

/*
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());//发送（使用默认交换机，routing-key，其他属性的消息，消息体）
        channel.close();
        connection.close();*/

        channel.exchangeDeclare(QUEUE_NAME, "topic");//声明topic类型的Exchange
        String routingKeyOne = "logs.error.one";// 定义一个路由名为“error”
        for (int i = 0; i <= 1; i++) {
            String messageOne = "this is one error logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyOne, null, messageOne.getBytes());
            System.out.println(" [x] Sent '" + routingKeyOne + "':'"+ messageOne + "'");
        }
        System.out.println("################################");
        String routingKeyTwo = "logs.error.two";
        for (int i = 0; i <= 2; i++) {
            String messageTwo = "this is two error logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyTwo, null, messageTwo.getBytes());
            System.out.println(" [x] Sent '" + routingKeyTwo + "':'" + messageTwo + "'");
        }
        System.out.println("################################");
        String routingKeyThree = "logs.info.one";
        for (int i = 0; i <= 3; i++) {
            String messageThree = "this is one info logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyThree, null,messageThree.getBytes());
            System.out.println(" [x] Sent '" + routingKeyThree + "':'"+ messageThree + "'");
        }
        channel.close();
        connection.close();




    }

}
