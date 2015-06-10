package com.acme.atddharness;

import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;
import com.acme.auctionclient.InvoiceLine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a quasi-fluent API for writing integration tests.
 * The specific focus is on a simple auction example.
 */
public class ScenarioImpl implements Scenario {
    public static final long ADDITIONAL_EXPIRATION_MINUTES_PAD = 60 * 60 * 1000 * 3;
    private final AuctionService auctionService;
    private final AdjustableTimeService adjustableTimeService;
    private final String listingId;
    private final int auctionLengthInDays;
    private final Map<User, String> userToAuthTokenMap = new HashMap<User, String>();
    private static final long SECONDS_PER_DAY = 60 * 60 * 24 * 1000;


    ScenarioImpl(AuctionService auctionService,
                 AdjustableTimeService adjustableTimeService,
                 User seller,
                 BigDecimal startingPrice,
                 BigDecimal buyItNowPrice, int auctionLengthInDays) throws AuctionServiceException {
        this.auctionService = auctionService;
        this.adjustableTimeService = adjustableTimeService;
        this.auctionLengthInDays = auctionLengthInDays;
        this.listingId = createListing(seller, startingPrice, buyItNowPrice, auctionLengthInDays);
    }

    private String getAuthToken(User user) throws AuctionServiceException {
        String authToken = userToAuthTokenMap.get(user);
        if (authToken == null) {
            authToken = auctionService.login(user.getUsername(), user.getPassword());
            userToAuthTokenMap.put(user, authToken);
        }
        return authToken;
    }

    private String createListing(User seller, BigDecimal startingPrice,
                                 BigDecimal buyItNowPrice, int auctionLengthInDays) throws AuctionServiceException {
        String authToken = getAuthToken(seller);
        return auctionService.createListing(authToken, startingPrice, buyItNowPrice, auctionLengthInDays);
    }

    public Scenario bid(User biddingUser, String bidAmountAsString) throws AuctionServiceException {
        BigDecimal bidAmount = new BigDecimal(bidAmountAsString);
        String authToken = getAuthToken(biddingUser);
        auctionService.bid(authToken, listingId, bidAmount);

        return this;
    }

    public Scenario buyItNow(User user) throws AuctionServiceException {
        String authToken = getAuthToken(user);
        auctionService.buyItNow(authToken, listingId);

        return this;
    }

    public Scenario expireAuctionNaturally() {

        long millisToAdd = SECONDS_PER_DAY * this.auctionLengthInDays + 1000 + ADDITIONAL_EXPIRATION_MINUTES_PAD;

        adjustableTimeService.incrementTimeOffset(millisToAdd);

        return this;
    }


    public Scenario assertInvoices(User user, InvoiceExpectation invoiceExpectation) throws AuctionServiceException {
        //invoke relevant service or db query
        String authToken = getAuthToken(user);
        List<InvoiceLine> invoiceLines = auctionService.fetchInvoices(authToken, listingId);

        //compare results by sending to InvoiceExpectation. Will throw AssertionError if match fails.
        invoiceExpectation.assertEqual(invoiceLines);

        return this;
    }

    public Scenario assertInvoices(User user, InvoiceLineExpectation... lineExpectations) throws AuctionServiceException {
        assertInvoices(user, new InvoiceExpectation(lineExpectations));

        return this;
    }

}
