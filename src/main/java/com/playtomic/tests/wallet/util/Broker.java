package com.playtomic.tests.wallet.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playtomic.tests.wallet.api.WalletController;
import com.playtomic.tests.wallet.dto.AddUpReq;
import com.playtomic.tests.wallet.entity.Transaction;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.TransactionRepository;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.StripeService;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class Broker {

	private Logger log = LoggerFactory.getLogger(WalletController.class);

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private StripeService stripeService;

	private BlockingQueue<AddUpReq> addUpQueue = new ArrayBlockingQueue<AddUpReq>(1024);
	private BlockingQueue<Transaction> purchaseQueue = new ArrayBlockingQueue<Transaction>(1024);
	private BlockingQueue<Long> refundQueue = new ArrayBlockingQueue<Long>(1024);

	private String desc;

	public Broker() {
		super();
		this.desc = "Broker Service";

		AddUpConsumerThread t1 = new AddUpConsumerThread();
		t1.start();
		PurchaseConsumerThread t2 = new PurchaseConsumerThread();
		t2.start();
		RefundConsumerThread t3 = new RefundConsumerThread();
		t3.start();

		log.info("Broker started.");
	}

	class AddUpConsumerThread extends Thread {

		public void run() {
			while (true) {
				try {
					AddUpReq req = addUpQueue.poll();
					if (req != null) {
						stripeService.charge(req.getCreditCardNumber(), req.getAmount());

						Wallet wallet = walletRepository.findById(req.getFk_wallet()).orElseThrow(
								() -> new IllegalArgumentException("Invalid wallet Id:" + req.getFk_wallet()));
						wallet.setBalance(wallet.getBalance() + req.getAmount().longValue());
						walletRepository.save(wallet);
						log.info("add-up successful.");
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	class PurchaseConsumerThread extends Thread {

		public void run() {
			while (true) {
				try {
					Transaction req = purchaseQueue.poll();
					if (req != null) {
						Wallet wallet = walletRepository.findById(req.getFk_wallet()).orElseThrow(
								() -> new IllegalArgumentException("Invalid wallet Id:" + req.getFk_wallet()));

						if (wallet.getBalance() < req.getAmount()) {
							log.info("Balance is exeeded.");
							req.setDescription("Balance is exeeded.");
							req.setStatus(-1);
							transactionRepository.save(req);
							continue;
						}

						req.setStatus(0);
						transactionRepository.save(req);

						wallet.setBalance(wallet.getBalance() - req.getAmount());
						walletRepository.save(wallet);
						log.info("purchase successful.");
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	class RefundConsumerThread extends Thread {

		public void run() {
			while (true) {
				try {
					Long id = refundQueue.poll();
					if (id != null) {
						Transaction transaction = transactionRepository.findById(id)
								.orElseThrow(() -> new IllegalArgumentException("Invalid transaction Id:" + id));
						if (transaction.getStatus() != 0) {
							log.info("Refund is not allowed");
							throw new Exception("Refund is not allowed");
						}
						transaction.setStatus(1);
						transaction.setDescription("Trasaction refunded.");
						transactionRepository.save(transaction);
						// update wallet balance
						Wallet wallet = walletRepository.findById(transaction.getFk_wallet()).orElseThrow(
								() -> new IllegalArgumentException("Invalid wallet Id:" + transaction.getFk_wallet()));
						wallet.setBalance(wallet.getBalance() + transaction.getAmount());
						walletRepository.save(wallet);
						log.info("refund successful.");
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}

		}
	}

}
