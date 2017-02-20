package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity @Deprecated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintQueryObject {

 @Id
 private String complaintId;

 private String company;

 private String description;


}
