package com.playtomic.tests.wallet.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.playtomic.tests.wallet.dto.AddUpReq;
import com.playtomic.tests.wallet.entity.Transaction;
import com.playtomic.tests.wallet.entity.User;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.TransactionRepository;
import com.playtomic.tests.wallet.repository.UserRepository;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.util.Broker;

@RestController
public class WalletController {
	private Logger log = LoggerFactory.getLogger(WalletController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private Broker broker;

	@RequestMapping("/")
	public void log() {
		log.info("Logging from /");
	}

	@GetMapping("/user/{id}")
	@ResponseBody
	public User get(@PathVariable("id") long id) {
		log.info("Logging from /user/" + id);
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		return user;
	}

	@GetMapping("/balance/{id}")
	@ResponseBody
	public Wallet balance(@PathVariable("id") long id) throws InterruptedException {
		log.info("Logging from /balance");
		Wallet wallet = walletRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid wallet Id:" + id));
		return wallet;
	}

	@PostMapping("/add-up")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String addUp(@RequestBody AddUpReq req) throws InterruptedException {
		log.info("Logging from /add-up");
		broker.getAddUpQueue().put(req);
		return "Add-up request received.";
	}

	@PostMapping("/purchase")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String purchase(@RequestBody Transaction req) throws InterruptedException {
		log.info("Logging from /purchase");
		broker.getPurchaseQueue().put(req);
		return "Purchase request received.";
	}

	@GetMapping("/refund/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String refund(@PathVariable("id") long id) throws Exception {
		log.info("Logging from /refund");
		broker.getRefundQueue().put(id);
		return "Refund request received.";
	}

	@GetMapping("/history")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Transaction> history() {
		log.info("Logging from /history");
		// TODO filter users transactions
		List<Transaction> result = new ArrayList<Transaction>();
		transactionRepository.findAll().forEach(result::add);
		return result;
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public String save(@RequestBody User user) {
		log.info("Logging from /save");
		userRepository.save(user);
		return user.toString();
	}
}
