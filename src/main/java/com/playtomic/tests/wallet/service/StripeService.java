package com.playtomic.tests.wallet.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Handles the communication with Stripe.
 *
 * A real implementation would call to String using their API/SDK. This dummy
 * implementation throws an error when trying to charge less than 10€.
 */
@Service
public class StripeService {

	@NonNull
	private RestTemplate restTemplate;

	@Value("${stripe.simulator.charges-uri}")
	private String chargesUri;

	@Value("${stripe.simulator.refunds-uri}")
	private String refundsUri;

	public StripeService() {
		this.restTemplate = (new RestTemplateBuilder()).errorHandler(new StripeRestTemplateResponseErrorHandler()).build();
	}
	
	public StripeService(@NotNull RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.errorHandler(new StripeRestTemplateResponseErrorHandler()).build();
	}

	public StripeService(String chargesUri, String refundsUri, @NotNull RestTemplateBuilder restTemplateBuilder) {
		this.chargesUri = chargesUri;
		this.refundsUri = refundsUri;
		this.restTemplate = restTemplateBuilder.errorHandler(new StripeRestTemplateResponseErrorHandler()).build();
	}

	/**
	 * Charges money in the credit card.
	 *
	 * Ignore the fact that no CVC or expiration date are provided.
	 *
	 * @param creditCardNumber
	 *            The number of the credit card
	 * @param amount
	 *            The amount that will be charged.
	 *
	 * @throws StripeServiceException
	 */
	public void charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount) throws StripeServiceException {
		ChargeRequest body = new ChargeRequest(creditCardNumber, amount);
		// Object.class because we don't read the body here.
		URI uri = URI.create(chargesUri);
		restTemplate.postForObject(uri, body, Object.class);
	}

	/**
	 * Refunds the specified payment.
	 */
	public void refund(@NonNull String paymentId) throws StripeServiceException {
		// Object.class because we don't read the body here.
		restTemplate.postForEntity(refundsUri, null, Object.class, paymentId);
	}

	@AllArgsConstructor
	private static class ChargeRequest {

		@NonNull
		@JsonProperty("credit_card")
		String creditCardNumber;

		@NonNull
		@JsonProperty("amount")
		BigDecimal amount;
	}
}
