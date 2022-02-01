package com.zinkworks.atm.interfaces.components;

import com.zinkworks.atm.representations.BalanceReceipt;

public interface IBalanceComponent {

	public BalanceReceipt getBalance(String accountNumber);
}
