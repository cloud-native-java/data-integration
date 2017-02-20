package com.example;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
@Deprecated
@RestController
@RequestMapping("/complaints")
class ComplaintRestController {

 private final CommandGateway cg;

 private final ComplaintQueryObjectRepository cqor;

 @Autowired
 ComplaintRestController(CommandGateway cg, ComplaintQueryObjectRepository cqor) {
  this.cg = cg;
  this.cqor = cqor;
 }

 // <1>
 @PostMapping
 CompletableFuture<String> fileComplaint(
  @RequestBody Map<String, String> request) {

  String id = UUID.randomUUID().toString();

  FileComplaintCommand command = new FileComplaintCommand(id,
   request.get("company"), request.get("description"));

  return cg.send(command);
 }

 @PutMapping
 CompletableFuture<String> updateComplaint(
  @RequestBody Map<String, String> request) {

  String id = UUID.randomUUID().toString();

  ChangeComplaintCommand command = new ChangeComplaintCommand(id,
      request.get("company"), request.get("description"));

  return cg.send(command);

 }

 // <2>
 @GetMapping
 List<ComplaintQueryObject> findAll() {
  return cqor.findAll();
 }

 @GetMapping("/{id}")
 ComplaintQueryObject find(@PathVariable String id) {
  return cqor.findOne(id);
 }
}
