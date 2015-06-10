package com.acme.auction;

/**
 * Adjustable time service.
 */
public interface AdjustableTimeService extends TimeService {
    /**
     * Increment the offset from the real time.
     *
     * @param millisToAdd milliseconds to add to the time.
     */
    void incrementTimeOffset(long millisToAdd);
}
