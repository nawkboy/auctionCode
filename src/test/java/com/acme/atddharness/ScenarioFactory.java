package com.acme.atddharness;

import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;

import java.math.BigDecimal;

/**
 * Interface for creating a Scenario. Typically used by a {@link ScenarioBuilder} implementation.
 */
public interface ScenarioFactory {
    Scenario createScenario(AuctionService auctionService,
                            AdjustableTimeService adjustableTimeService,
                            User seller,
                            BigDecimal startingPrice,
                            BigDecimal buyItNowPrice, int auctionLengthInDays) throws AuctionServiceException;
}
