package com.hhl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq四---通过路由规则，接收端接收发送端发送的消息
 * Created by hanlin.huang on 2017/5/24.
 */
public class DemoSend04 {

    private static final String QUEUE_NAME = "HHL_TEST_QUEUE_4";


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

        channel.exchangeDeclare(QUEUE_NAME, "direct");//rounting模式

        String routingKeyOne = "error";//定义一个路由名为“error”
        for (int i = 0; i <= 1; i++) {
            String messageOne = "this is a error logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyOne, null, messageOne.getBytes());
            System.out.println(" [x] Sent '" + routingKeyOne + "':'" + messageOne + "'");
        }
        System.out.println("################################");
        String routingKeyTwo = "info"; //定义一个路由名为“info”
        for (int i = 0; i <= 2; i++) {
            String messageTwo = "this is a info logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyTwo, null, messageTwo.getBytes());
            System.out.println(" [x] Sent '" + routingKeyTwo + "':'" + messageTwo+ "'");
        }
        System.out.println("################################");
        String routingKeyThree = "all";//定义一个路由名为“all”
        for (int i = 0; i <= 3; i++) {
            String messageThree = "this is a all logs:" + i;
            channel.basicPublish(QUEUE_NAME, routingKeyThree, null, messageThree.getBytes());
            System.out.println(" [x] Sent '" + routingKeyThree + "':'" + messageThree + "'");
        }
        channel.close();
        connection.close();




    }

}
