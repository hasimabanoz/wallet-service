package com.playtomic.tests.wallet.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long fk_user;
	
	private long fk_wallet;
	
	private int status;

	private String date_and_time;

	private long amount;
	
	private String description;
	
}
