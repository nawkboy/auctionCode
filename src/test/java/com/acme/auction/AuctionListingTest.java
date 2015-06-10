package com.acme.auction;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for DefaultAuctionListing which is responsible for dealing
 * with operations on a particular auction listing.
 */
public class AuctionListingTest {
    private static final Date FIRST_DAY = new Date(0);
    private static final Date THIRD_DAY_AND_ONE_SEC = new Date(60 * 60 * 24 * 1000 * 3 + 1000);
    private static final int AUCTION_LENGTH_IN_DAYS = 3;
    private static final BigDecimal STARTING_PRICE = new BigDecimal("5.00");
    private static final BigDecimal BUY_IT_NOW_PRICE = new BigDecimal("50.00");
    private static final BigDecimal GEORGE_SECOND_HIGHER_BID = new BigDecimal("7.00");

    @Test
    public void testExpiredAuctionWithoutBids() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, null, AUCTION_LENGTH_IN_DAYS);
        assertNull(auctionListing.getBuyItNowPrice());

        //Move clock past auction close
        when(timeService.getTime()).thenReturn(THIRD_DAY_AND_ONE_SEC);

        assertNull(auctionListing.getWinningUser());
        assertNull(auctionListing.getWinningPrice());
        assertTrue(auctionListing.isAuctionClosed());
        assertFalse(auctionListing.isBoughtUsingBuyItNow());
    }

    @Test
    public void testExpiredAuctionWithBids() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, null, AUCTION_LENGTH_IN_DAYS);
        assertNull(auctionListing.getBuyItNowPrice());

        performInitialBidding(auctionListing);
        //Move clock past auction close and bid again.
        when(timeService.getTime()).thenReturn(THIRD_DAY_AND_ONE_SEC);

        //bid should be rejected.
        auctionListing.bid("sally", new BigDecimal("20.00"));

        assertEquals("george", auctionListing.getWinningUser());
        assertEquals(GEORGE_SECOND_HIGHER_BID, auctionListing.getWinningPrice());
        assertTrue(auctionListing.isAuctionClosed());
        assertFalse(auctionListing.isBoughtUsingBuyItNow());
    }

    @Test
    public void testEnsureGetWinningUserWillCloseAuction() {
        AuctionListing auctionListing = createReadToCloseAuction();
        assertEquals("george", auctionListing.getWinningUser());
        assertTrue(auctionListing.isAuctionClosed());
    }

    @Test
    public void testEnsureGetWinningPrinceWillCloseAuction() {
        AuctionListing auctionListing = createReadToCloseAuction();
        assertEquals(GEORGE_SECOND_HIGHER_BID, auctionListing.getWinningPrice());
        assertTrue(auctionListing.isAuctionClosed());
    }

    private AuctionListing createReadToCloseAuction() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, null, AUCTION_LENGTH_IN_DAYS);
        assertNull(auctionListing.getBuyItNowPrice());

        performInitialBidding(auctionListing);
        //Move clock past auction close and bid again.
        when(timeService.getTime()).thenReturn(THIRD_DAY_AND_ONE_SEC);

        return auctionListing;
    }

    @Test
    public void testBoughtUsingBuyItNowAfterSomeBidding() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, BUY_IT_NOW_PRICE, AUCTION_LENGTH_IN_DAYS);

        performInitialBidding(auctionListing);

        auctionListing.buyItNow("george");

        assertEquals("george", auctionListing.getWinningUser());
        assertEquals(BUY_IT_NOW_PRICE, auctionListing.getWinningPrice());
        assertTrue(auctionListing.isAuctionClosed());
        assertTrue(auctionListing.isBoughtUsingBuyItNow());
    }

    @Test
    public void testBoughtUsingBuyItWithNoBidding() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, BUY_IT_NOW_PRICE, AUCTION_LENGTH_IN_DAYS);

//        performInitialBidding(auctionListing);

        auctionListing.buyItNow("george");

        assertEquals("george", auctionListing.getWinningUser());
        assertEquals(BUY_IT_NOW_PRICE, auctionListing.getWinningPrice());
        assertTrue(auctionListing.isAuctionClosed());
        assertTrue(auctionListing.isBoughtUsingBuyItNow());
    }

    @Test
    public void testTypicalUsage() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);
        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, null, AUCTION_LENGTH_IN_DAYS);
        assertNull(auctionListing.getBuyItNowPrice());

        performInitialBidding(auctionListing);
    }

    @Test
    public void testTypicalUsageWithBuyItNow() {
        TimeService timeService = mock(TimeService.class);
        when(timeService.getTime()).thenReturn(FIRST_DAY);

        AuctionListingFactory listingFactory = new DefaultAuctionListingFactory(timeService);

        AuctionListing auctionListing = listingFactory.createAuctionListing("fred", STARTING_PRICE, BUY_IT_NOW_PRICE, AUCTION_LENGTH_IN_DAYS);

        assertEquals(BUY_IT_NOW_PRICE, auctionListing.getBuyItNowPrice());

        performInitialBidding(auctionListing);
    }

    private void performInitialBidding(AuctionListing auctionListing) {
        assertEquals(STARTING_PRICE, auctionListing.getStartingPrice());
        assertEquals(AUCTION_LENGTH_IN_DAYS, auctionListing.getAuctionLength());
        assertNull(auctionListing.getCurrentBid());

        BigDecimal underBidValue = STARTING_PRICE.subtract(new BigDecimal("0.01"));
        auctionListing.bid("zoe", underBidValue);
        assertNull(auctionListing.getCurrentBid());


        BigDecimal firstBid = STARTING_PRICE;

        auctionListing.bid("sally", firstBid);

        assertEquals("sally", auctionListing.getCurrentBid().getBidder());
        assertEquals(firstBid, auctionListing.getCurrentBid().getBidValue());

        auctionListing.bid("bob", firstBid);
        assertEquals("sally", auctionListing.getCurrentBid().getBidder());
        assertEquals(firstBid, auctionListing.getCurrentBid().getBidValue());

        auctionListing.bid("george", GEORGE_SECOND_HIGHER_BID);
        assertEquals("george", auctionListing.getCurrentBid().getBidder());
        assertEquals(GEORGE_SECOND_HIGHER_BID, auctionListing.getCurrentBid().getBidValue());
    }
}
