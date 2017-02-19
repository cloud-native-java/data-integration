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

 public ComplaintRestController(CommandGateway cg,
  ComplaintQueryObjectRepository cqor) {
  this.cg = cg;
  this.cqor = cqor;
 }

 @PostMapping
 public CompletableFuture<String> fileComplaint(
  @RequestBody Map<String, String> request) {
  String id = UUID.randomUUID().toString();
  return cg.send(new FileComplaintCommand(id, request.get("company"), request
   .get("description")));
 }

 @GetMapping
 public List<ComplaintQueryObject> findAll() {
  return cqor.findAll();
 }

 @GetMapping("/{id}")
 public ComplaintQueryObject find(@PathVariable String id) {
  return cqor.findOne(id);
 }
}
