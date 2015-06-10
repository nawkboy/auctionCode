package com.acme.auction;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Implementation of this interface is responsible for dealing with
 * the details of a particular auction listing.
 */
public interface AuctionListing {
    void bid(String user, BigDecimal bidAmount);

    void buyItNow(String user);

    boolean isAuctionClosed();

    Date getStartingTime();

    Date getEndingTime();

    String getListingOwner();

    String getListingId();

    Bid getCurrentBid();

    String getWinningUser();

    BigDecimal getWinningPrice();

    BigDecimal getStartingPrice();

    BigDecimal getBuyItNowPrice();

    int getAuctionLength();

    boolean isBoughtUsingBuyItNow();

    public interface Bid {
        String getBidder();

        BigDecimal getBidValue();
    }
}
