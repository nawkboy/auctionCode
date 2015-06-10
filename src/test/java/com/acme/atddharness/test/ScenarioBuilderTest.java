package com.acme.atddharness.test;

import com.acme.atddharness.*;
import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.acme.atddharness.User.SELLER_FRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * The InvoiceLineExpectation test utility is complex enough to justify
 * it's own test case.
 */
public class ScenarioBuilderTest {
    private static final String EXCEPTION_MESSAGE = "ISeeDeadPeople";
    private AuctionService auctionService;
    private AdjustableTimeService adjustableTimeService;
    private ScenarioFactory scenarioFactory;
    private Scenario scenario;
    private ScenarioBuilder builder;

    //Note: With more refactoring, better test coverage is possible.

    @Before
    public void setUp() throws AuctionServiceException {
        auctionService = mock(AuctionService.class);
        adjustableTimeService = mock(AdjustableTimeService.class);
        scenarioFactory = mock(ScenarioFactory.class);
        scenario = mock(Scenario.class);


        builder = new DefaultScenarioBuilder(auctionService,
                adjustableTimeService, scenarioFactory);
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyBuilderThrowsIllegalStateException() throws AuctionServiceException {
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void testMissingStartingPriceThrowsIllegalStateException() throws AuctionServiceException {

        builder.withSeller(SELLER_FRED)
//                .withStartingPrice("100.00")
                .withBuyItNowPrice("250.00")
                .withAuctionLengthInDays(5)
                .build();
    }

    @Test
    public void testAuctionServiceExceptionDuringCreation() throws AuctionServiceException {
        mockUnHappyPathCreateScenarioBehavior();

        String startingPriceAsString = "100.00";
        String buyItNowPriceAsString = "250.00";
        int auctionLengthInDays = 5;
        builder.withSeller(SELLER_FRED)
                .withStartingPrice(startingPriceAsString)
                .withBuyItNowPrice(buyItNowPriceAsString)
                .withAuctionLengthInDays(auctionLengthInDays);

        boolean exceptionCaught = false;
        try {
            builder.build();
        } catch (AuctionServiceException e) {
            assertEquals(EXCEPTION_MESSAGE, e.getMessage());
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);

        verify(scenarioFactory, times(1)).createScenario(
                auctionService,
                adjustableTimeService,
                SELLER_FRED,
                new BigDecimal(startingPriceAsString),
                new BigDecimal(buyItNowPriceAsString),
                auctionLengthInDays);
    }

    @Test
    public void testMissingBuyItNowPriceIsAllowed() throws AuctionServiceException {
        mockHappyPathCreateScenarioBehavior();

        int auctionLengthInDays = 5;
        String startingPriceAsString = "100.00";
        Scenario scenario = builder.withSeller(SELLER_FRED)
                .withStartingPrice(startingPriceAsString)
//                .withBuyItNowPrice("250.00")
                .withAuctionLengthInDays(auctionLengthInDays)
                .build();

        verify(scenarioFactory, times(1)).createScenario(
                auctionService,
                adjustableTimeService,
                SELLER_FRED,
                new BigDecimal(startingPriceAsString),
                null,
                auctionLengthInDays);

    }

    @Test
    public void testTypicalUsage() throws AuctionServiceException {
        mockHappyPathCreateScenarioBehavior();

        String startingPriceAsString = "100.00";
        String buyItNowPriceAsString = "250.00";
        int auctionLengthInDays = 5;
        Scenario actualScenario = builder.withSeller(SELLER_FRED)
                .withStartingPrice(startingPriceAsString)
                .withBuyItNowPrice(buyItNowPriceAsString)
                .withAuctionLengthInDays(auctionLengthInDays)
                .build();

        assertEquals(scenario, actualScenario);

        verify(scenarioFactory, times(1)).createScenario(
                auctionService,
                adjustableTimeService,
                SELLER_FRED,
                new BigDecimal(startingPriceAsString),
                new BigDecimal(buyItNowPriceAsString),
                auctionLengthInDays);

    }

    private void mockUnHappyPathCreateScenarioBehavior() throws AuctionServiceException {
        when(scenarioFactory.createScenario(
                any(AuctionService.class),
                any(AdjustableTimeService.class),
                any(User.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyInt()))
                .thenThrow(new AuctionServiceException(EXCEPTION_MESSAGE));
    }

    private void mockHappyPathCreateScenarioBehavior() throws AuctionServiceException {
        when(scenarioFactory.createScenario(
                any(AuctionService.class),
                any(AdjustableTimeService.class),
                any(User.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyInt()))
                .thenReturn(scenario);
    }
}
