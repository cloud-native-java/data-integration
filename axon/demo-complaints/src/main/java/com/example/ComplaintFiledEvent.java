package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
@Deprecated
@Data
@AllArgsConstructor
public class ComplaintFiledEvent {

 private final String id;

 private final String company;

 private final String description;
}
