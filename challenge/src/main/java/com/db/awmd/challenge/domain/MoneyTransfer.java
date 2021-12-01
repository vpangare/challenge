package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MoneyTransfer {

	@NotNull
	@NotEmpty
	private String accountFromId;

	@NotNull
	@NotEmpty
	private String accountToId;

	@NotNull
	@Min(value = 0, message = "Transfer Amount must be positive.")
	private BigDecimal transferAmount;

	public MoneyTransfer(String accountFromId, String accountToId) {
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.transferAmount = BigDecimal.ZERO;
	}

	@JsonCreator
	public MoneyTransfer(@JsonProperty("accountFromId") String accountFromId,
			@JsonProperty("accountToId") String accountToId,
			@JsonProperty("transferAmount") BigDecimal transferAmount) {
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.transferAmount = transferAmount;
	}
}
