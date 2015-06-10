package com.acme.atddharness;

import com.acme.auctionclient.AuctionServiceException;

/**
 * Quasi-fluent interface for use in creating automated acceptance tests
 * for the auction system.
 */
public interface Scenario {
    Scenario bid(User biddingUser, String bidAmountAsString) throws AuctionServiceException;

    Scenario buyItNow(User user) throws AuctionServiceException;

    Scenario expireAuctionNaturally();

    Scenario assertInvoices(User user, InvoiceExpectation invoiceExpectation) throws AuctionServiceException;

    Scenario assertInvoices(User user, InvoiceLineExpectation... lineExpectations) throws AuctionServiceException;
}
