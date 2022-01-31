package com.zinkworks.atm.dtos;

import lombok.Data;

@Data
public class WithdrawalDTO {
	
	private Integer noteValue;	
	private Integer amount;

	public WithdrawalDTO(Integer noteValue, Integer amount) {
		this.noteValue = noteValue;
		this.amount = amount;
	}

}
