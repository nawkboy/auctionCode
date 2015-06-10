package com.acme.atddharness;

import com.acme.auction.AuctionModule;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.Singleton;

/**
 * Guice module responsible for wiring up the auction
 * automated acceptance test framework as well
 * the auction system itself.
 */
public class ATDDAuctionModule extends AuctionModule {
    public ATDDAuctionModule() {
        super(true);
    }

    @Override
    protected void configure() {
        install(new GuiceBerryModule());
        super.configure();
        bind(ScenarioBuilderFactory.class)
                .to(DefaultScenarioBuilderFactory.class)
                .in(Singleton.class);
//        bind(ScenarioBuilder.class)
//                .to(DefaultScenarioBuilder.class)
//                .in(Singleton.class);
        bind(ScenarioFactory.class)
                .to(DefaultScenarioFactory.class)
                .in(Singleton.class);
    }
}
