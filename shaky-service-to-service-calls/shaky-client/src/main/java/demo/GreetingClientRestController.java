package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingClientRestController {

 private final RetryableGreetingClient retry;

 private final CircuitBreakerGreetingClient cb;

 @Autowired
 GreetingClientRestController(
   RetryableGreetingClient retry,
   CircuitBreakerGreetingClient cb) {
  this.retry = retry;
  this.cb = cb;
 }

 @GetMapping("/hystrix/hi/{name}")
 String hystrix(
   @PathVariable String name) {
  return this.cb
    .greet(name);
 }

 @GetMapping("/retry/hi/{name}")
 String retry(
   @PathVariable String name) {
  return this.retry
    .greet(name);
 }
}
