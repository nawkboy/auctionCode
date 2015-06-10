package com.acme.auction;

import com.google.common.base.Objects;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Factory for creating auction listing instances.
 */
public class DefaultAuctionListingFactory implements AuctionListingFactory {
    private final TimeService timeService;

    @Inject
    public DefaultAuctionListingFactory(TimeService timeService) {
        super();
        this.timeService = timeService;
    }

    public AuctionListing createAuctionListing(String listingOwner, BigDecimal startingPrice,
                                               BigDecimal buyItNowPrice, int auctionLength) {
        return new DefaultAuctionListing(this.timeService, listingOwner, startingPrice,
                buyItNowPrice, auctionLength);
    }

    /**
     * Concrete implementation of a single auction instance.
     */
    public static class DefaultAuctionListing implements AuctionListing {
        private final TimeService timeService;
        private final String listingOwner;
        private final BigDecimal startingPrice;
        private final BigDecimal buyItNowPrice;
        private final int auctionLength;
        private final String listingId = "listing" + UUID.randomUUID().toString();
        private final Date startingTime;
        private final Date endingTime;
        private Bid currentBid = null;
        private boolean boughtUsingBuyItNow = false;
        private boolean listingClosed = false;
        private String winningUser;
        private BigDecimal winningPrice;

        private DefaultAuctionListing(TimeService timeService, String listingOwner,
                                      BigDecimal startingPrice, BigDecimal buyItNowPrice, int auctionLength) {
            super();
            this.timeService = timeService;
            this.listingOwner = listingOwner;
            this.startingPrice = startingPrice;
            this.buyItNowPrice = buyItNowPrice;
            this.auctionLength = auctionLength;
            this.startingTime = timeService.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.startingTime);
            calendar.add(Calendar.DAY_OF_WEEK, this.auctionLength);
            this.endingTime = calendar.getTime();
        }

        public synchronized void bid(String user, BigDecimal bidAmount) {
            if (isAuctionClosed()) return;

            if (currentBid == null) {
                if (startingPrice.compareTo(bidAmount) <= 0) {
                    this.currentBid = new DefaultBid(user, bidAmount);
                }
            } else {
                if (currentBid.getBidValue().compareTo(bidAmount) < 0) {
                    this.currentBid = new DefaultBid(user, bidAmount);
                }
            }
        }

        public synchronized void buyItNow(String user) {
            if (isAuctionClosed()) return;

            this.listingClosed = true;
            this.boughtUsingBuyItNow = true;
            this.winningPrice = this.buyItNowPrice;
            this.winningUser = user;
        }

        public synchronized boolean isAuctionClosed() {
            if (listingClosed) return true;

            if (isPastNaturalClosingTime()) {
                this.listingClosed = true;

                if (getCurrentBid() != null) {
                    this.winningPrice = this.getCurrentBid().getBidValue();
                    this.winningUser = this.getCurrentBid().getBidder();
                }
                return true;
            }
            return false;
        }

        public synchronized boolean isBoughtUsingBuyItNow() {
            return this.boughtUsingBuyItNow;
        }

        public Date getStartingTime() {
            return this.startingTime;
        }

        public Date getEndingTime() {
            return this.endingTime;
        }

        public String getWinningUser() {
            //Close auction if needed.
            isAuctionClosed();
            return this.winningUser;
        }

        public BigDecimal getWinningPrice() {
            //Close auction if needed.
            isAuctionClosed();
            return this.winningPrice;
        }

        public String getListingOwner() {
            return listingOwner;
        }

        public String getListingId() {
            return this.listingId;
        }

        public Bid getCurrentBid() {
            return currentBid;
        }

        public BigDecimal getStartingPrice() {
            return startingPrice;
        }

        public BigDecimal getBuyItNowPrice() {
            return buyItNowPrice;
        }

        public int getAuctionLength() {
            return auctionLength;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }

        private boolean isPastNaturalClosingTime() {
            Date currentTime = this.timeService.getTime();
            return currentTime.after(this.endingTime);
        }

        private static class DefaultBid implements Bid {
            private final String bidder;
            private final BigDecimal bidValue;

            private DefaultBid(String bidder, BigDecimal bidValue) {
                super();
                this.bidder = bidder;
                this.bidValue = bidValue;
            }

            public String getBidder() {
                return this.bidder;
            }

            public BigDecimal getBidValue() {
                return this.bidValue;
            }
        }
    }
}
