package com.hhl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq二---均匀分配消息给每个接收者，prefetchCount设置均匀分配的个数
 * Created by hanlin.huang on 2017/5/24.
 */
public class DemoSend02 {

    private static final String QUEUE_NAME = "HHL_TEST_QUEUE";


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();   //创建连接工厂
        factory.setHost("118.190.44.59");   //设置服务器IP
        factory.setUsername("bbyshp");
        factory.setPassword("bqjr@2017");
        Connection connection = factory.newConnection(); //ConnectionFactory建立连接
        Channel channel = connection.createChannel();   //创建通道
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  //定义队列(队列名称、队列是否持久化、是否是此连接的唯一队列、是否自动删除


        String message = "Hello World!";
        /*channel.basicPublish("", QUEUE_NAME, null, message.getBytes());//发送（使用默认交换机，routing-key，其他属性的消息，消息体）
        channel.close();
        connection.close();*/


        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        //发送（使用默认交换机，routing-key,其他属性的消息——路由标题等（这里持久化），消息体）
        channel.close();
        connection.close();


    }

}
