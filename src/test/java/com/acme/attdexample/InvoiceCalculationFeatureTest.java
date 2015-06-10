package com.acme.attdexample;

import com.acme.atddharness.ATDDAuctionModule;
import com.acme.atddharness.InvoiceLineExpectation;
import com.acme.atddharness.Scenario;
import com.acme.atddharness.ScenarioBuilderFactory;
import com.acme.auctionclient.AuctionServiceException;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import org.junit.Rule;
import org.junit.Test;

import static com.acme.atddharness.User.*;
import static com.acme.auctionclient.FeeType.*;

/*
Feature: Invoice Calculation
As an auction platform user
I want to invoices to have charges appropriate to actual activity
so that I know how much I owe and so that I can trust the system.

Scenario: Auction listing purchased using BuyItNow functionality
Given an auction listing by Fred
And the auction listing has 2 images
And the auction listing starting price is 100.00
And the auction listing buy it now price is 250.00
When Sally bids 102.00
And George bids 105.50
And Sally bids 106.99
And George clicks buy it now
Then Fred's invoice should have a listing fee of 5.00 and a buy it now fee of 2.25
And George's invoice should have a purchase fee of 250.00
And Sally's invoice should have no fees.

Scenario: Auction listing with multiple bids and natural expiration
Given an auction listing by Fred
And the auction listing has 2 images
And the auction listing starting price is 100.00
And the auction listing buy it now price is 250.00
When Sally bids 102.00
And George bids 105.50
And Sally bids 106.99
And auction naturally expires
Then Fred's invoice should have a listing fee of 5.00 and a buy it now fee of 2.25
And Sally's invoice should have a purchase fee of 106.99
And George's invoice should have no fees.
*/

/**
 * As an auction platform user
 * I want to invoices to have charges appropriate to actual activity
 * so that I know how much I owe and so that I can trust the system.
 */
public class InvoiceCalculationFeatureTest {

    @Rule
    public GuiceBerryRule rule = new GuiceBerryRule(ATDDAuctionModule.class);

    @Inject
    private ScenarioBuilderFactory scenarioBuilderFactory;

    /**
     * Auction listing purchased using BuyItNow functionality.
     *
     * @throws AuctionServiceException indicates a problem accessing the auction service
     */
    @Test
    public void testTwoImagesWithBuyItNow_PurchasedUsingBuyItNow() throws AuctionServiceException {

        Scenario scenario = scenarioBuilderFactory.createScenarioBuilder()
                .withSeller(SELLER_FRED)
                .withStartingPrice("100.00")
                .withBuyItNowPrice("250.00")
                .withAuctionLengthInDays(5)
                .build();

        scenario.bid(BUYER_SALLY, "102.00")
                .bid(BUYER_GEORGE, "105.50")
                .bid(BUYER_SALLY, "106.99")
                .buyItNow(BUYER_GEORGE)

                .assertInvoices(SELLER_FRED,
                        new InvoiceLineExpectation(LISTING_FEE, "5.00"),
                        new InvoiceLineExpectation(BUY_IT_NOW_FEE, "2.25"))

                .assertInvoices(BUYER_GEORGE,
                        new InvoiceLineExpectation(PURCHASE_FEE, "250.00"))

                .assertInvoices(BUYER_SALLY);
    }

    /**
     * Auction listing with multiple bids and natural expiration.
     *
     * @throws AuctionServiceException indicates a problem accessing the auction service
     */
    @Test
    public void testTwoImagesWithBuyItNow_HighestBidWinsWhenAuctionExpires() throws AuctionServiceException {

        Scenario scenario = scenarioBuilderFactory.createScenarioBuilder()
                .withSeller(SELLER_FRED)
                .withStartingPrice("100.00")
                .withBuyItNowPrice("250.00")
                .withAuctionLengthInDays(5)
                .build();

        scenario.bid(BUYER_SALLY, "102.00")
                .bid(BUYER_GEORGE, "105.50")
                .bid(BUYER_SALLY, "106.99")
                .expireAuctionNaturally()

                .assertInvoices(SELLER_FRED,
                        new InvoiceLineExpectation(LISTING_FEE, "5.00"),
                        new InvoiceLineExpectation(BUY_IT_NOW_FEE, "2.25"))

                .assertInvoices(BUYER_SALLY,
                        new InvoiceLineExpectation(PURCHASE_FEE, "106.99"))

                .assertInvoices(BUYER_GEORGE);
    }
}
