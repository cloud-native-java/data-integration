package com.example;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/complaints")
class ComplaintRestController {

 private final CommandGateway cg;

 private final ComplaintQueryObjectRepository cqor;

 ComplaintRestController(CommandGateway cg, ComplaintQueryObjectRepository cqor) {
  this.cg = cg;
  this.cqor = cqor;
 }

 @PostMapping
 CompletableFuture<String> fileComplaint(
  @RequestBody Map<String, String> request) {
  String id = UUID.randomUUID().toString();
  return cg.send(new FileComplaintCommand(id, request.get("company"), request
   .get("description")));
 }

 @GetMapping
 List<ComplaintQueryObject> findAll() {
  return cqor.findAll();
 }

 @GetMapping("/{id}")
 ComplaintQueryObject find(@PathVariable String id) {
  return cqor.findOne(id);
 }
}
