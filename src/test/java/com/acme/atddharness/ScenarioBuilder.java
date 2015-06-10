package com.acme.atddharness;

import com.acme.auctionclient.AuctionServiceException;

/**
 * Implements builder pattern for building a scenario.
 */
public interface ScenarioBuilder {
    ScenarioBuilder withSeller(User seller);

    ScenarioBuilder withStartingPrice(String priceAsString);

    ScenarioBuilder withBuyItNowPrice(String priceAsString);

    ScenarioBuilder withAuctionLengthInDays(int auctionLengthInDays);

    /**
     * Non-reenterant method that constructs the Scenario instance.
     *
     * @return instance of Scenario.
     * @throws AuctionServiceException
     */
    Scenario build() throws AuctionServiceException;
}
