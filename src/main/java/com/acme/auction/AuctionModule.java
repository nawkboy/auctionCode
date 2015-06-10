package com.acme.auction;

import com.acme.auctionclient.AuctionService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Guice module responsible for wiring up the auction system.
 * In a real client/server system there would be separate client
 * and server configurations. Since this is a trivial fluent
 * acceptance test driven development focused demo, no
 * real client/server communication is involved.
 */
public class AuctionModule extends AbstractModule {
    private final boolean useAdjustableTimeService;

    public AuctionModule(boolean useAdjustableTimeService) {
        super();
        this.useAdjustableTimeService = useAdjustableTimeService;
    }

    @Override
    protected void configure() {
        bindTimeServiceClasses();

        bind(AuctionService.class)
                .to(DefaultAuctionService.class)
                .in(Singleton.class);
        bind(AuctionListingFactory.class)
                .to(DefaultAuctionListingFactory.class)
                .in(Singleton.class);
    }

    private void bindTimeServiceClasses() {
        if (useAdjustableTimeService) {
            bind(AdjustableTimeService.class)
                    .to(DefaultAdjustableTimeService.class)
                    .in(Singleton.class);

            //Bind TimeService interface to use the same provider as the AdjustableTimeService interface
            //If I was to bind straight to the DefaultAdjustableTimeService it would result in a different
            //singleton instance.  Unless the instances are the same time manipulation will not work.
            bind(TimeService.class).to(AdjustableTimeService.class);

            //The DefaultAdjustableTimeService requires the raw time service itself.
            bind(TimeService.class).annotatedWith(Names.named("rawTimeService"))
                    .to(DefaultTimeService.class)
                    .in(Singleton.class);
        } else {
            bind(TimeService.class)
                    .to(DefaultTimeService.class)
                    .in(Singleton.class);
        }
    }
}
