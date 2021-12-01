package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.FundTransferException;
import com.db.awmd.challenge.service.NotificationService;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	@Autowired
	NotificationService notificationService;

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	private final Map<String, MoneyTransfer> fundTransferDetails = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public void clearFundTransferDetails() {
		fundTransferDetails.clear();
	}

	@Override
	public void fundTransfer(MoneyTransfer moneyTransfer) throws FundTransferException {
		String fromAccountId = moneyTransfer.getAccountFromId();
		String toAccountId = moneyTransfer.getAccountToId();
		BigDecimal amount = moneyTransfer.getTransferAmount();
		String requestID = "FT" + System.currentTimeMillis();
		if (!accounts.containsValue(fromAccountId)) {
			throw new FundTransferException("Invalid Account Id " + fromAccountId);
		}

		if (fundTransferDetails.containsKey(requestID)) {
			throw new FundTransferException("Duplicate Request " + requestID);
		}

		fundTransferDetails.put(requestID, moneyTransfer);

		Account account = getAccount(fromAccountId);
		String message = "Amount " + amount + "Transfered to Account " + toAccountId.substring(toAccountId.length() - 4)
				+ "From your Account" + toAccountId.substring(toAccountId.length() - 4);
		notificationService.notifyAboutTransfer(account, message);
	}

}
