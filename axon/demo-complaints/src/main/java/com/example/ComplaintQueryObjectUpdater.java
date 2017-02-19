package com.example;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
class ComplaintQueryObjectUpdater {

 private final ComplaintQueryObjectRepository complaintsQueryObjectRepository;

 public ComplaintQueryObjectUpdater(ComplaintQueryObjectRepository cqor) {
  this.complaintsQueryObjectRepository = cqor;
 }

 @EventHandler
 public void on(ComplaintFiledEvent event) {
  complaintsQueryObjectRepository.save(new ComplaintQueryObject(event.getId(),
   event.getCompany(), event.getDescription()));
 }
}
