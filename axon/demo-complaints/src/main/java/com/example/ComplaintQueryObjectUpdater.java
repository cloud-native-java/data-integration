package com.example;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
class ComplaintQueryObjectUpdater {

 private final ComplaintQueryObjectRepository cqor;

 public ComplaintQueryObjectUpdater(ComplaintQueryObjectRepository cqor) {
  this.cqor = cqor;
 }

 @EventHandler
 public void on(ComplaintFiledEvent event) {
  cqor.save(new ComplaintQueryObject(event.getId(),
   event.getCompany(), event.getDescription()));
 }
}
