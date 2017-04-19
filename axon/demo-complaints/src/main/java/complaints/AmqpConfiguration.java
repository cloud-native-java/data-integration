package complaints;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AmqpConfiguration {

 private static final String COMPLAINTS = "complaints";

 // <1>
 @Bean
 Exchange exchange() {
  return ExchangeBuilder.fanoutExchange(COMPLAINTS).build();
 }

 // <2>
 @Bean
 Queue queue() {
  return QueueBuilder.durable(COMPLAINTS).build();
 }

 // <3>
 @Bean
 Binding binding() {
  return BindingBuilder.bind(queue()) //
   .to(exchange()).with("*").noargs();
 }

 // <4>
 @Autowired
 public void configure(AmqpAdmin admin) {
  admin.declareExchange(exchange());
  admin.declareQueue(queue());
  admin.declareBinding(binding());
 }
}
