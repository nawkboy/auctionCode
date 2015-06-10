package com.acme.auction;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Used to create AuctionListing instances.
 */
public interface AuctionListingFactory {
    AuctionListing createAuctionListing(String listingOwner, BigDecimal startingPrice,
                                        @Nullable BigDecimal buyItNowPrice, int auctionLength);
}
