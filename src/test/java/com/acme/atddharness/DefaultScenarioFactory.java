package com.acme.atddharness;

import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;

import java.math.BigDecimal;

/**
 * Simple factory for creating Scenario instances.
 */
public class DefaultScenarioFactory implements ScenarioFactory {
    public Scenario createScenario(AuctionService auctionService,
                                   AdjustableTimeService adjustableTimeService,
                                   User seller,
                                   BigDecimal startingPrice,
                                   BigDecimal buyItNowPrice,
                                   int auctionLengthInDays)
            throws AuctionServiceException {

        return new ScenarioImpl(auctionService, adjustableTimeService,
                seller, startingPrice, buyItNowPrice, auctionLengthInDays);
    }
}
