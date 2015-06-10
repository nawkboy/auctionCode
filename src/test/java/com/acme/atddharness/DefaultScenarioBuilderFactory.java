package com.acme.atddharness;

import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.google.inject.Inject;

/**
 * Default implementation of ScenarioBuilderFactory
 */
public class DefaultScenarioBuilderFactory implements ScenarioBuilderFactory {
    private final AuctionService auctionService;
    private final AdjustableTimeService adjustableTimeService;
    private final ScenarioFactory scenarioFactory;

    @Inject
    public DefaultScenarioBuilderFactory(AuctionService auctionService,
                                  AdjustableTimeService adjustableTimeService,
                                  ScenarioFactory scenarioFactory) {
        super();
        this.auctionService = auctionService;
        this.adjustableTimeService = adjustableTimeService;
        this.scenarioFactory = scenarioFactory;
    }

    public ScenarioBuilder createScenarioBuilder() {
        return new DefaultScenarioBuilder(auctionService, adjustableTimeService, scenarioFactory);
    }
}
