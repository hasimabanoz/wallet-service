package com.playtomic.tests.wallet.service.impl;


import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.StripeService;
import com.playtomic.tests.wallet.service.StripeServiceException;

/**
 * This test is failing with the current implementation.
 *
 * How would you test this?
 */
public class StripeServiceTest {

    String testUri = "https://sandbox.playtomic.io/v1/stripe-simulator/charges";
    StripeService s = new StripeService(testUri, testUri, new RestTemplateBuilder());

    @Test
    public void test_exception() {
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            s.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
    }

    @Test
    public void test_ok() throws StripeServiceException {
        s.charge("4242 4242 4242 4242", new BigDecimal(15));
    }
}
