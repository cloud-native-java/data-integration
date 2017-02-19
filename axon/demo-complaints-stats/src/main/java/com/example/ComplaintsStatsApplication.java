package com.example;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ComplaintsStatsApplication {

 public static void main(String[] args) {
  SpringApplication.run(ComplaintsStatsApplication.class, args);
 }

 @Bean
 SpringAMQPMessageSource statisticsQueue(Serializer serializer) {
  return new SpringAMQPMessageSource(
   new DefaultAMQPMessageConverter(serializer)) {

   @RabbitListener(queues = "complaints")
   @Override
   public void onMessage(Message message, Channel channel) throws Exception {
    super.onMessage(message, channel);
   }
  };
 }
}
