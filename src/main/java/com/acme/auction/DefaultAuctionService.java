package com.acme.auction;

import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;
import com.acme.auctionclient.FeeType;
import com.acme.auctionclient.InvoiceLine;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Toy example implementation of an auction service.
 */
public class DefaultAuctionService implements AuctionService {
    public static final String AUCTION_LISTING_FEE_AMOUNT = "5.00";
    public static final String BUY_IT_NOW_LISTING_FEE_AMOUNT = "2.25";
    private final AuctionListingFactory auctionListingFactory;
    private final Map<String, String> userPassMap;
    private final BiMap<String, String> userToAuthTokenBiMap;
    private final Map<String, AuctionListing> listingIdToListingMap = new ConcurrentHashMap<String, AuctionListing>();

    @Inject
    public DefaultAuctionService(AuctionListingFactory auctionListingFactory) {
        super();
        this.auctionListingFactory = auctionListingFactory;


        userPassMap = ImmutableMap.of(
                "default.seller@acme.com", "letsSell",
                "fred.seller@acme.com", "sellingIsFun",
                "sally.buyer@acme.com", "gotToBuy",
                "george.buyer@acme.com", "sallyIsAnnoying");

        ImmutableBiMap.Builder<String, String> userToAuthTokenMapBuilder = ImmutableBiMap.builder();
        for (String key : userPassMap.keySet()) {
            userToAuthTokenMapBuilder.put(key, UUID.randomUUID().toString());
        }
        userToAuthTokenBiMap = userToAuthTokenMapBuilder.build();
    }

    public String login(String username, String password) throws AuctionServiceException {
        if (username == null) {
            throw new AuctionServiceException("null username");
        }
        if (password == null) {
            throw new AuctionServiceException("null password");
        }
        String expectedPassword = this.userPassMap.get(username);
        if (expectedPassword == null || !password.equals(expectedPassword)) {
            throw new AuctionServiceException("Invalid user/pass combination");
        }

        return userToAuthTokenBiMap.get(username);
    }

    public String createListing(String authToken, BigDecimal startingPrice, BigDecimal buyItNowPrice, int auctionLength) throws AuctionServiceException {
        String listingOwner = validateTokenAndAcquireUser(authToken);
        AuctionListing auctionListing = auctionListingFactory.createAuctionListing(listingOwner, startingPrice, buyItNowPrice, auctionLength);
        listingIdToListingMap.put(auctionListing.getListingId(), auctionListing);
        return auctionListing.getListingId();
    }

    public void bid(String authToken, String listingId, BigDecimal bidAmount) throws AuctionServiceException {
        String biddingUser = validateTokenAndAcquireUser(authToken);
        AuctionListing auctionListing = findAuctionListing(listingId);
        ensureOwnerNotBidding(biddingUser, auctionListing);
        auctionListing.bid(biddingUser, bidAmount);
    }

    public void buyItNow(String authToken, String listingId) throws AuctionServiceException {
        //TODO: Not well tested. See comments in related test for more detail.
        String biddingUser = validateTokenAndAcquireUser(authToken);
        AuctionListing auctionListing = findAuctionListing(listingId);
        ensureOwnerNotBidding(biddingUser, auctionListing);
        auctionListing.buyItNow(biddingUser);
    }

    private AuctionListing findAuctionListing(String authToken, String listingId) throws AuctionServiceException {
        String biddingUser = validateTokenAndAcquireUser(authToken);
        AuctionListing auctionListing = findAuctionListing(listingId);
        ensureOwnerNotBidding(biddingUser, auctionListing);

        return auctionListing;
    }

    //TODO: Not well tested. See comments in related test for more detail.
    public List<InvoiceLine> fetchInvoices(String authToken, String listingId) throws AuctionServiceException {
        String requestingUser = validateTokenAndAcquireUser(authToken);
        AuctionListing auctionListing = findAuctionListing(listingId);

        ImmutableList.Builder<InvoiceLine> invoiceListBuilder = ImmutableList.builder();
        if (requestingUser.equals(auctionListing.getListingOwner())) {
            invoiceListBuilder.add(new InvoiceLine(listingId, FeeType.LISTING_FEE, AUCTION_LISTING_FEE_AMOUNT));
            if (auctionListing.getBuyItNowPrice() != null) {
                invoiceListBuilder.add(new InvoiceLine(listingId, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_LISTING_FEE_AMOUNT));
            }
        } else if (requestingUser.equals(auctionListing.getWinningUser())) {
            invoiceListBuilder.add(new InvoiceLine(listingId, FeeType.PURCHASE_FEE, auctionListing.getWinningPrice()));
        }

        return invoiceListBuilder.build();
    }

    private void ensureOwnerNotBidding(String biddingUser, AuctionListing auctionListing) throws AuctionServiceException {
        if (biddingUser.equals(auctionListing.getListingOwner())) {
            throw new AuctionServiceException("Purchasing on your own listings is not allowed");
        }
    }

    private void ensureOwnerMatchesListing(String requestingUser, AuctionListing auctionListing) throws AuctionServiceException {
        if (!requestingUser.equals(auctionListing.getListingOwner())) {
            throw new AuctionServiceException("Only listing owner can perform this operation");
        }
    }

    private AuctionListing findAuctionListing(String listingId) throws AuctionServiceException {
        AuctionListing auctionListing = listingIdToListingMap.get(listingId);
        if (auctionListing == null) {
            throw new AuctionServiceException("invalid listing id");
        }
        return auctionListing;
    }

    private String validateTokenAndAcquireUser(String authToken) throws AuctionServiceException {
        String user = userToAuthTokenBiMap.inverse().get(authToken);
        if (user == null) {
            throw new AuctionServiceException("Unrecognized auth token");
        }
        return user;
    }

}
