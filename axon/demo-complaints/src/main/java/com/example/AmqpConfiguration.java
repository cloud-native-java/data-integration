package com.example;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AmqpConfiguration {

 private static final String COMPLAINTS = "complaints";

 @Bean
 Exchange exchange() {
  return ExchangeBuilder.fanoutExchange(COMPLAINTS).build();
 }

 @Bean
 Queue queue() {
  return QueueBuilder.durable(COMPLAINTS).build();
 }

 @Bean
 Binding binding() {
  return BindingBuilder.bind(queue()) //
   .to(exchange()).with("*").noargs();
 }

 @Autowired
 public void configure(AmqpAdmin admin) {
  admin.declareExchange(exchange());
  admin.declareQueue(queue());
  admin.declareBinding(binding());
 }
}
