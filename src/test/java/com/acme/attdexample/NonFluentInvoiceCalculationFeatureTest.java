package com.acme.attdexample;

import com.acme.atddharness.InvoiceExpectation;
import com.acme.atddharness.InvoiceLineExpectation;
import com.acme.auction.AuctionModule;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;
import com.acme.auctionclient.InvoiceLine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.acme.atddharness.User.*;
import static com.acme.auctionclient.FeeType.*;

/*
Feature: Invoice Calculation
As an auction platform user
I want to invoices to have charges appropriate to actual activity
so that I know how much I owe and so that I can trust the system.

Scenario: Auction listing purchased using BuyItNow functionality
Given an auction listing by Fred
And the auction listing starting price is 100.00
And the auction listing buy it now price is 250.00
When Sally bids 102.00
And George bids 105.50
And Sally bids 106.99
And George clicks buy it now
Then Fred's invoice should have a listing fee of 5.00 and a buy it now fee of 2.25
And George's invoice should have a purchase fee of 250.00
And Sally's invoice should have no fees.
*/

/**
 * As an auction platform user
 * I want to invoices to have charges appropriate to actual activity
 * so that I know how much I owe and so that I can trust the system.
 */
public class NonFluentInvoiceCalculationFeatureTest {

    private AuctionService auctionService;

    @Before
    public void setUp() {
        /*
        * Guice.createInjector() takes your Modules, and returns a new Injector
        * instance. Most applications will call this method exactly once, in their
        * main() method.
        */
        Injector injector = Guice.createInjector(new AuctionModule(false));

        /*
        * Now that we've got the injector, we can build objects.
        */
        auctionService = injector.getInstance(AuctionService.class);
    }

    /**
     * Auction listing purchased using BuyItNow functionality.
     *
     * @throws AuctionServiceException indicates a problem communicating with the auction service
     */
    @Test
    public void testTwoImagesWithBuyItNow_PurchasedUsingBuyItNow() throws AuctionServiceException {


        String fredAuthToken = auctionService.login(SELLER_FRED.getUsername(), SELLER_FRED.getPassword());
        String sallyAuthToken = auctionService.login(BUYER_SALLY.getUsername(), BUYER_SALLY.getPassword());
        String georgeAuthToken = auctionService.login(BUYER_GEORGE.getUsername(), BUYER_GEORGE.getPassword());

        final BigDecimal startingPrice = new BigDecimal("100.00");
        final BigDecimal buyItNowPrice = new BigDecimal("250.00");
        final int auctionLengthDays = 5;
        String listingId = auctionService.createListing(fredAuthToken, startingPrice,
                buyItNowPrice, auctionLengthDays);

        auctionService.bid(sallyAuthToken, listingId, new BigDecimal("102.00"));
        auctionService.bid(georgeAuthToken, listingId, new BigDecimal("250.00"));
        auctionService.bid(sallyAuthToken, listingId, new BigDecimal("106.99"));
        auctionService.buyItNow(georgeAuthToken, listingId);

        List<InvoiceLine> fredActualInvoiceLines = auctionService.fetchInvoices(fredAuthToken, listingId);
        InvoiceExpectation fredInvoiceExpectation = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, "5.00"),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, "2.25"));
        fredInvoiceExpectation.assertEqual(fredActualInvoiceLines);

        List<InvoiceLine> georgeActualInvoiceLines = auctionService.fetchInvoices(georgeAuthToken, listingId);
        InvoiceExpectation georgeInvoiceExpectation = new InvoiceExpectation(
                new InvoiceLineExpectation(PURCHASE_FEE, "250.00"));
        georgeInvoiceExpectation.assertEqual(georgeActualInvoiceLines);

        List<InvoiceLine> sallyActualInvoiceLines = auctionService.fetchInvoices(sallyAuthToken, listingId);
        InvoiceExpectation sallyInvoiceExpectation = new InvoiceExpectation();
        sallyInvoiceExpectation.assertEqual(sallyActualInvoiceLines);

        //alternative for sally invoice expectations.
        Assert.assertTrue(sallyActualInvoiceLines.isEmpty());

    }

}
