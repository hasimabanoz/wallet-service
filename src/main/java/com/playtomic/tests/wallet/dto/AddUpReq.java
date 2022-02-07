package com.playtomic.tests.wallet.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUpReq {

	private String creditCardNumber;

	private BigDecimal amount;
	
	private long fk_wallet;

}
