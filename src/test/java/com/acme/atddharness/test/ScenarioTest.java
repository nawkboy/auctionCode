package com.acme.atddharness.test;

import com.acme.atddharness.*;
import com.acme.auction.AdjustableTimeService;
import com.acme.auctionclient.AuctionService;
import com.acme.auctionclient.AuctionServiceException;
import com.acme.auctionclient.FeeType;
import com.acme.auctionclient.InvoiceLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * The InvoiceLineExpectation test utility is complex enough to justify
 * it's own test case.
 */
public class ScenarioTest {

    private static final String GEORGE_TOKEN = "georgeToken";
    private static final String SALLY_TOKEN = "sallyToken";
    private static final String FRED_TOKEN = "fredToken";
    private static final String DEFAULT_SELLER_TOKEN = "defaultSellerToken";
    private static final BigDecimal STARTING_PRICE = new BigDecimal("100.00");
    private static final BigDecimal BUY_IT_NOW_PRICE = new BigDecimal("200.00");
    private static final int AUCTION_LENGTH_IN_DAYS = 5;
    private static final String LISTING_ID = "listingId123";
    private AuctionService auctionServiceMock;
    private AdjustableTimeService adjustableTimeServiceMock;
    private ScenarioFactory scenarioFactory = new DefaultScenarioFactory();

    @Before
    public void setUp() {
        auctionServiceMock = mock(AuctionService.class);
        adjustableTimeServiceMock = mock(AdjustableTimeService.class);
    }

    @Test
    public void testConstructor() throws AuctionServiceException {
        createScenarioWhileVerifyingBehavior();
    }

    @Test
    public void testFetchInvoicesWithNoInvoiceLines() throws AuctionServiceException {
        Scenario scenario = createScenarioWhileVerifyingBehavior();
        InvoiceExpectation invoiceExpectation = mock(InvoiceExpectation.class);

        scenario.assertInvoices(User.SELLER_DEFAULT, invoiceExpectation);

        InOrder inOrder = inOrder(auctionServiceMock, invoiceExpectation);
        inOrder.verify(auctionServiceMock, times(1)).fetchInvoices(DEFAULT_SELLER_TOKEN, LISTING_ID);
        inOrder.verify(invoiceExpectation).assertEqual(Collections.<InvoiceLine>emptyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFetchInvoicesWithTwoInvoiceLines() throws AuctionServiceException {
        //assemble
        Scenario scenario = createScenarioWhileVerifyingBehavior();

        List<InvoiceLine> invoiceLines = Arrays.asList(
                new InvoiceLine(LISTING_ID, FeeType.LISTING_FEE, new BigDecimal("2.00")),
                new InvoiceLine(LISTING_ID, FeeType.BUY_IT_NOW_FEE, new BigDecimal("1.50"))
        );

        when(auctionServiceMock.fetchInvoices(DEFAULT_SELLER_TOKEN, LISTING_ID))
                .thenReturn(invoiceLines);
        InvoiceExpectation invoiceExpectation = mock(InvoiceExpectation.class);

        //act
        scenario.assertInvoices(User.SELLER_DEFAULT, invoiceExpectation);

        //assert
        verify(invoiceExpectation).assertEqual(invoiceLines);
    }

    @Test
    public void testBidding() throws AuctionServiceException {
        Scenario scenario = createScenarioWhileVerifyingBehavior();

        String bidAmountAsString = "120.00";
        scenario.bid(User.BUYER_GEORGE, bidAmountAsString);

        verify(auctionServiceMock).bid(GEORGE_TOKEN, LISTING_ID, new BigDecimal(bidAmountAsString));
    }

    @Test
    public void testLazyAuthTokenCaching() throws AuctionServiceException {
        Scenario scenario = createScenarioWhileVerifyingBehavior();

        String bidAmountAsString = "125.00";

        scenario.bid(User.BUYER_GEORGE, bidAmountAsString);
        scenario.bid(User.BUYER_SALLY, bidAmountAsString);
        scenario.bid(User.SELLER_DEFAULT, bidAmountAsString);
        scenario.bid(User.SELLER_DEFAULT, bidAmountAsString);
        scenario.bid(User.SELLER_FRED, bidAmountAsString);
        scenario.bid(User.SELLER_FRED, bidAmountAsString);
        scenario.bid(User.SELLER_DEFAULT, bidAmountAsString);
        scenario.bid(User.SELLER_FRED, bidAmountAsString);
        scenario.bid(User.BUYER_GEORGE, bidAmountAsString);
        scenario.bid(User.BUYER_SALLY, bidAmountAsString);
        scenario.buyItNow(User.BUYER_SALLY);

        verify(auctionServiceMock, times(1)).login(User.SELLER_DEFAULT.getUsername(), User.SELLER_DEFAULT.getPassword());
        verify(auctionServiceMock, times(1)).login(User.SELLER_FRED.getUsername(), User.SELLER_FRED.getPassword());
        verify(auctionServiceMock, times(1)).login(User.BUYER_GEORGE.getUsername(), User.BUYER_GEORGE.getPassword());
        verify(auctionServiceMock, times(1)).login(User.BUYER_SALLY.getUsername(), User.BUYER_SALLY.getPassword());
    }

    @Test
    public void testBuyItNow() throws AuctionServiceException {
        Scenario scenario = createScenarioWhileVerifyingBehavior();

        scenario.buyItNow(User.BUYER_GEORGE);

        verify(auctionServiceMock).buyItNow(GEORGE_TOKEN, LISTING_ID);
    }

    @Test
    public void testExpireAuctionNaturally() throws AuctionServiceException {
        Scenario scenario = createScenarioWhileVerifyingBehavior();

        scenario.expireAuctionNaturally();
        long secondsPerDay = 60 * 60 * 24 * 1000;
        long millisToAdd = secondsPerDay * AUCTION_LENGTH_IN_DAYS + 1000
                + ScenarioImpl.ADDITIONAL_EXPIRATION_MINUTES_PAD;

        verify(adjustableTimeServiceMock, times(1)).incrementTimeOffset(millisToAdd);

        verifyZeroInteractions(auctionServiceMock);
    }

    //NOTE: This class feels a bit too hard to test.
    private Scenario createScenarioWhileVerifyingBehavior() throws AuctionServiceException {
        /*
        Assemble Mocks
         */
        mockAuthTokensByUserBehavior(auctionServiceMock);
        mockCreateListingBehavior(auctionServiceMock);

        /**
         * Act (use constructor)
         */
        Scenario scenario = scenarioFactory.createScenario(auctionServiceMock, adjustableTimeServiceMock,
                User.SELLER_DEFAULT,
                STARTING_PRICE,
                BUY_IT_NOW_PRICE,
                AUCTION_LENGTH_IN_DAYS);

        /**
         * Assert behavior
         */
        //login must be called before creating a listing.
        InOrder inOrderVerifier = inOrder(auctionServiceMock);
        inOrderVerifier.verify(auctionServiceMock, times(1))
                .login(User.SELLER_DEFAULT.getUsername(), User.SELLER_DEFAULT.getPassword());

        inOrderVerifier.verify(auctionServiceMock, times(1))
                .createListing(DEFAULT_SELLER_TOKEN, STARTING_PRICE, BUY_IT_NOW_PRICE, AUCTION_LENGTH_IN_DAYS);

        inOrderVerifier.verifyNoMoreInteractions();

        return scenario;
    }

    private void mockCreateListingBehavior(AuctionService auctionService) throws AuctionServiceException {
        when(auctionService.createListing(anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenReturn(LISTING_ID);
    }

    private void mockAuthTokensByUserBehavior(AuctionService auctionService) throws AuctionServiceException {
        when(auctionService.login(eq(User.BUYER_GEORGE.getUsername()), any(String.class)))
                .thenReturn(GEORGE_TOKEN);
        when(auctionService.login(eq(User.BUYER_SALLY.getUsername()), any(String.class)))
                .thenReturn(SALLY_TOKEN);
        when(auctionService.login(eq(User.SELLER_FRED.getUsername()), any(String.class)))
                .thenReturn(FRED_TOKEN);
        when(auctionService.login(eq(User.SELLER_DEFAULT.getUsername()), any(String.class)))
                .thenReturn(DEFAULT_SELLER_TOKEN);
    }
}
