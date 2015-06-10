package com.acme.auctionclient;

import com.acme.atddharness.User;
import com.acme.auction.AuctionListing;
import com.acme.auction.AuctionListingFactory;
import com.acme.auction.DefaultAuctionService;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test of the AuctionService.
 */
public class AuctionServiceTest {
    /*
    NOTE: There are lots of tests I would usually write before building out the implementation.
    Since this is a demo I really only need the happy path to work well enough to wire up
    the fluent integration test example. For real production code this would be bad, bad, bad
    as bugs are inevitable without better tests. Oddly, for a demo on testing a few bugs may be useful.

    Tests I have skipped include:
    1) Ensure a seller can not bid on their own auction listing.
    2) Ensure a seller can not perform "buyItNow" on their own auction listing.
    3) Various buyItNow tests that ensure buyItNow is routed to the correct auction listing.
       (The details of the AuctionListing behavior belong in the AuctionListingTest not here)
    4) Proper testing of invoices.
     */

    @Test
    public void testBadAuthTokenOnCreateListing() throws AuctionServiceException {
        AuctionListingFactory auctionListingFactory = mock(AuctionListingFactory.class);

        AuctionService auctionService = new DefaultAuctionService(auctionListingFactory);
        AuctionListing fredFirstListing = mock(AuctionListing.class, "fredFirstListing");
        String fredAuthToken = auctionService.login(User.SELLER_FRED.getUsername(), User.SELLER_FRED.getPassword());
        BigDecimal startingPriceA = new BigDecimal("5.00");
        when(auctionListingFactory.createAuctionListing(User.SELLER_FRED.getUsername(), startingPriceA, null, 3))
                .thenReturn(fredFirstListing);


        boolean exceptionCaught = false;
        try {
            auctionService.createListing("badAuthId", startingPriceA, null, 3);
        } catch (AuctionServiceException ex) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void testBadAuthTokenOnBid() throws AuctionServiceException {
        AuctionListingFactory auctionListingFactory = mock(AuctionListingFactory.class);

        AuctionService auctionService = new DefaultAuctionService(auctionListingFactory);
        AuctionListing fredFirstListing = mock(AuctionListing.class, "fredFirstListing");
        when(fredFirstListing.getListingId()).thenReturn("fredFirstListing123");

        String fredAuthToken = auctionService.login(User.SELLER_FRED.getUsername(), User.SELLER_FRED.getPassword());
        BigDecimal startingPriceA = new BigDecimal("5.00");
        when(auctionListingFactory.createAuctionListing(User.SELLER_FRED.getUsername(), startingPriceA, null, 3))
                .thenReturn(fredFirstListing);
        String fredFirstListingId = auctionService.createListing(fredAuthToken, startingPriceA, null, 3);

        boolean exceptionCaught = false;
        try {
            auctionService.bid("badAuthId", fredFirstListingId, new BigDecimal("20.00"));
        } catch (AuctionServiceException ex) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void testBidRoutingToCorrectListing() throws AuctionServiceException {
        AuctionListingFactory auctionListingFactory = mock(AuctionListingFactory.class);

        //Mock listings, the mock AuctionListingFactory will eventually be set to return.
        //Behavior for which to return is defined later closer to the call that causes it.
        AuctionListing fredFirstListing = mock(AuctionListing.class, "fredFirstListing");
        when(fredFirstListing.getListingId()).thenReturn("fred123");
        AuctionListing fredSecondListing = mock(AuctionListing.class, "fredSecondListing");
        when(fredSecondListing.getListingId()).thenReturn("fred456");
        AuctionListing sallyOnlyListing = mock(AuctionListing.class, "sallyOnlyListing");
        when(sallyOnlyListing.getListingId()).thenReturn("sally123");

        AuctionService auctionService = new DefaultAuctionService(auctionListingFactory);

        String fredAuthToken = auctionService.login(User.SELLER_FRED.getUsername(), User.SELLER_FRED.getPassword());
        //Sally can list as well.
        String sallyAuthToken = auctionService.login(User.BUYER_SALLY.getUsername(), User.BUYER_SALLY.getPassword());
        //George will only participate in bidding.
        String georgeAuthToken = auctionService.login(User.BUYER_GEORGE.getUsername(), User.BUYER_GEORGE.getPassword());

        BigDecimal startingPriceA = new BigDecimal("5.00");
        when(auctionListingFactory.createAuctionListing(User.SELLER_FRED.getUsername(), startingPriceA, null, 3))
                .thenReturn(fredFirstListing);
        String fredFirstListingId = auctionService.createListing(fredAuthToken, startingPriceA, null, 3);

        BigDecimal startingPriceB = new BigDecimal("5.50");
        when(auctionListingFactory.createAuctionListing(User.SELLER_FRED.getUsername(), startingPriceB, null, 3))
                .thenReturn(fredSecondListing);
        String fredSecondListingId = auctionService.createListing(fredAuthToken, startingPriceB, null, 3);

        BigDecimal startingPriceC = new BigDecimal("5.60");
        when(auctionListingFactory.createAuctionListing(User.BUYER_SALLY.getUsername(), startingPriceC, null, 3))
                .thenReturn(sallyOnlyListing);
        String sallyOnlyListingId = auctionService.createListing(sallyAuthToken, startingPriceC, null, 3);

        /****
         * We can now start to work on testing the auction service correctly routes the bid requests
         * to the associated auction listing.
         */
        BigDecimal georgeFavoriteFirstBid = new BigDecimal("10.00");
        auctionService.bid(georgeAuthToken, fredFirstListingId, georgeFavoriteFirstBid);
        verify(fredFirstListing).bid(User.BUYER_GEORGE.getUsername(), georgeFavoriteFirstBid);

        auctionService.bid(georgeAuthToken, fredSecondListingId, georgeFavoriteFirstBid);
        verify(fredSecondListing).bid(User.BUYER_GEORGE.getUsername(), georgeFavoriteFirstBid);

        auctionService.bid(georgeAuthToken, sallyOnlyListingId, georgeFavoriteFirstBid);
        verify(sallyOnlyListing).bid(User.BUYER_GEORGE.getUsername(), georgeFavoriteFirstBid);

    }
}
