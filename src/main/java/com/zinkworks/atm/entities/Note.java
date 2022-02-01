package com.zinkworks.atm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name = "note")
public class Note implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private Integer value;
	
	@Column(nullable = false)
	private Integer amount;
	
	public Note() {}

	public Note(Integer value, Integer amount) {
		this.value = value;
		this.amount = amount;
	}
	
	public Note(Integer id, Integer value, Integer amount) {
		this.id = id;
		this.value = value;
		this.amount = amount;
	}
	
	

}
