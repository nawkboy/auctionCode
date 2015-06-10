package com.acme.auction;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the adjustable time service.
 */
public class AdjustableTimeServiceTest {
    private static final Date FIRST_DAY = new Date(0);
    private static final long THREE_DAYS_AND_ONE_SEC_OF_MILLIS = 60 * 60 * 24 * 1000 * 3 + 1000;
    private static final Date THIRD_DAY_AND_ONE_SEC = new Date(THREE_DAYS_AND_ONE_SEC_OF_MILLIS);

    @Test
    public void testTypicalUsage() {
        TimeService rawTimeService = mock(TimeService.class);
        when(rawTimeService.getTime()).thenReturn(FIRST_DAY);

        DefaultAdjustableTimeService adjustableTimeService = new DefaultAdjustableTimeService(rawTimeService);
        assertEquals(FIRST_DAY, adjustableTimeService.getTime());

        adjustableTimeService.incrementTimeOffset(THREE_DAYS_AND_ONE_SEC_OF_MILLIS);
        assertEquals(THIRD_DAY_AND_ONE_SEC, adjustableTimeService.getTime());

    }
}
