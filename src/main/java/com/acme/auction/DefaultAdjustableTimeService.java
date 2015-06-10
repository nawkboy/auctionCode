package com.acme.auction;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default time service implementation.
 * Always returns UTC time.
 */
public class DefaultAdjustableTimeService implements AdjustableTimeService {
    private final TimeService timeService;
    private final AtomicLong atomicMillisOffset = new AtomicLong(0);

    @Inject
    public DefaultAdjustableTimeService(@Named("rawTimeService") TimeService timeService) {
        super();
        this.timeService = timeService;
    }

    public void incrementTimeOffset(long millisToAdd) {
        atomicMillisOffset.addAndGet(millisToAdd);
    }

    public Date getTime() {
        return new Date(timeService.getTime().getTime() + atomicMillisOffset.get());
    }
}
