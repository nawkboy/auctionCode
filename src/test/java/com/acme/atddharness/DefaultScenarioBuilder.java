package com.acme.atddharness;

import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;

import java.math.BigDecimal;

/**
 * Default implementation of ScenarioBuilder.
 */
public class DefaultScenarioBuilder implements ScenarioBuilder {
    private final AuctionService auctionService;
    private final AdjustableTimeService adjustableTimeService;
    private final ScenarioFactory scenarioFactory;
    private User seller = User.SELLER_DEFAULT;
    private BigDecimal startingPrice;
    private BigDecimal buyItNowPrice;
    /*default to 3 day auctions */
    private int auctionLengthInDays = 3;

    public DefaultScenarioBuilder(AuctionService auctionService,
                                  AdjustableTimeService adjustableTimeService,
                                  ScenarioFactory scenarioFactory) {
        super();
        this.auctionService = auctionService;
        this.adjustableTimeService = adjustableTimeService;
        this.scenarioFactory = scenarioFactory;
    }

    public ScenarioBuilder withSeller(User seller) {
        this.seller = seller;
        return this;
    }

    public ScenarioBuilder withStartingPrice(String priceAsString) {
        this.startingPrice = new BigDecimal(priceAsString);
        return this;
    }

    public ScenarioBuilder withBuyItNowPrice(String priceAsString) {
        this.buyItNowPrice = new BigDecimal(priceAsString);
        return this;
    }

    public ScenarioBuilder withAuctionLengthInDays(int auctionLengthInDays) {
        this.auctionLengthInDays = auctionLengthInDays;
        return this;
    }

    public Scenario build() throws AuctionServiceException {
        if (startingPrice == null || startingPrice.signum() < 0) {
            throw new IllegalStateException("startingPrice must be defined and non-negative");
        }
        if (buyItNowPrice != null && buyItNowPrice.signum() < 0) {
            throw new IllegalStateException("buyItNowPrice can be null, but never negative");
        }

        return scenarioFactory.createScenario(auctionService, adjustableTimeService, seller, startingPrice,
                buyItNowPrice, auctionLengthInDays);
    }
}
