package com.example;

import lombok.Data;

@Data
public class ComplaintFiledEvent {

 private final String id;

 private final String company;

 private final String description;

 public ComplaintFiledEvent(String id, String company, String description) {
  this.id = id;
  this.company = company;
  this.description = description;
 }
}
