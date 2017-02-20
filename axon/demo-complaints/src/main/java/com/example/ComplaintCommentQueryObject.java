package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Deprecated
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintCommentQueryObject {

	@Id
	private String id;

	private String username;

	@OneToMany(mappedBy = "technician")
	private List<ComplaintQueryObject> complaints = new ArrayList<>();

}
