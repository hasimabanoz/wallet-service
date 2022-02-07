package com.playtomic.tests.wallet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.playtomic.tests.wallet.api.WalletController;
import com.playtomic.tests.wallet.dto.AddUpReq;
import com.playtomic.tests.wallet.entity.Transaction;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.TransactionRepository;
import com.playtomic.tests.wallet.repository.UserRepository;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.util.Broker;

@SpringBootTest
@ActiveProfiles(profiles = "dev")
@TestInstance(Lifecycle.PER_CLASS)
public class WalletApplicationTest {

	@InjectMocks
	WalletController controller;

	@Mock
	private UserRepository userRepository;

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private Broker broker;

	@BeforeAll
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void balanceTest() throws InterruptedException {
		Wallet wallet = new Wallet();
		wallet.setBalance(100);
		wallet.setFk_user(1);
		wallet.setId(1);
		Optional<Wallet> w = Optional.of(wallet);
		Mockito.doReturn(w).when(walletRepository).findById(1L);
		// run test
		Wallet response = controller.balance(1);
		assertEquals(100L, response.getBalance());
	}

	@Test
	public void addUpTest() throws InterruptedException {
		BlockingQueue<AddUpReq> addUpQueue = Mockito.spy(new ArrayBlockingQueue<AddUpReq>(1024));
		Mockito.doReturn(addUpQueue).when(broker).getAddUpQueue();

		AddUpReq req = new AddUpReq();
		req.setAmount(new BigDecimal(13));
		req.setCreditCardNumber("4465392455995853");
		req.setFk_wallet(1);

		assertDoesNotThrow(() -> addUpQueue.put(req));
		
		String result = controller.addUp(req);
		assertEquals("Add-up request received.", result);
	}
	
	@Test
	public void purchaseTest() throws InterruptedException {
		BlockingQueue<Transaction> purchaseQueue = Mockito.spy(new ArrayBlockingQueue<Transaction>(1024));
		Mockito.doReturn(purchaseQueue).when(broker).getPurchaseQueue();

		Transaction req = new Transaction();
		req.setAmount(13);
		req.setDate_and_time("2022/01/15 13:55");
		req.setDescription("reservation for tennis");
		req.setFk_user(1);
		req.setFk_wallet(1);

		assertDoesNotThrow(() -> purchaseQueue.put(req));
		
		String result = controller.purchase(req);
		assertEquals("Purchase request received.", result);
	}
	
	@Test
	public void refundTest() throws Exception {
		BlockingQueue<Long> refundQueue = Mockito.spy(new ArrayBlockingQueue<Long>(1024));
		Mockito.doReturn(refundQueue).when(broker).getRefundQueue();

		assertDoesNotThrow(() -> refundQueue.put(1L));
		
		String result = controller.refund(1L);
		assertEquals("Refund request received.", result);
	}
	
	// TODO refund test throws transaction not found
	
	@Test
	public void historyTest() throws Exception {
		List<Transaction> result = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setAmount(13L);
		t1.setStatus(0);
		result.add(t1);
		Mockito.doReturn(result).when(transactionRepository).findAll();
		// run test
		List<Transaction> response = controller.history();
		assertEquals(1, response.size());
		assertEquals(13L, response.get(0).getAmount());
		assertEquals(0, response.get(0).getStatus());
	}
}
