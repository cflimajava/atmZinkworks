package com.zinkworks.atm.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name = "account")
public class Account implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, name = "account_number", unique = true)
	private String accountNumber;
	
	@Column(nullable = false)
	private Integer pin;
	
	@Column(nullable = false)
	private BigDecimal balance;
	
	@Column(nullable = false)
	private BigDecimal overdraft;

}
