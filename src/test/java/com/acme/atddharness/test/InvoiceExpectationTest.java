package com.acme.atddharness.test;

import com.acme.atddharness.InvoiceExpectation;
import com.acme.atddharness.InvoiceLineExpectation;
import com.acme.auctionclient.FeeType;
import com.acme.auctionclient.InvoiceLine;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static com.acme.auctionclient.FeeType.BUY_IT_NOW_FEE;
import static com.acme.auctionclient.FeeType.LISTING_FEE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The InvoiceExpectation test utility is complex enough to justify
 * it's own test case.
 */
public class InvoiceExpectationTest {

    private static final String AUCTION_ID = "auctionId";
    private static final String BUY_IT_NOW_FEE_AMOUNT = "5.00";
    private static final String LISTING_FEE_AMOUNT = "2.25";
    private static final String PURCHASE_FEE_AMOUNT = "10.00";

    @Test
    public void testSimpleUsage() {

        ImmutableList<InvoiceLine> invoiceLines = ImmutableList.of(
                new InvoiceLine(AUCTION_ID, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.LISTING_FEE, LISTING_FEE_AMOUNT));

        InvoiceExpectation invoiceExpectation = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, LISTING_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT));

        assertInvoicesMatch(invoiceExpectation, invoiceLines);
    }

    @Test
    public void testMissingInvoiceLine() {

        ImmutableList<InvoiceLine> invoiceLines = ImmutableList.of(
                new InvoiceLine(AUCTION_ID, FeeType.LISTING_FEE, LISTING_FEE_AMOUNT));

        InvoiceExpectation invoiceExpectation = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, LISTING_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT));

        assertInvoicesDoNotMatch(invoiceExpectation, invoiceLines);
    }

    @Test
    public void testExtraInvoiceLine() {

        ImmutableList<InvoiceLine> invoiceLines = ImmutableList.of(
                new InvoiceLine(AUCTION_ID, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.PURCHASE_FEE, PURCHASE_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.LISTING_FEE, LISTING_FEE_AMOUNT));

        InvoiceExpectation invoiceExpectation = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, LISTING_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT));

        assertInvoicesDoNotMatch(invoiceExpectation, invoiceLines);
    }

    @Test
    public void testDuplicateFees() {

        ImmutableList<InvoiceLine> invoiceLinesWithDuplicate = ImmutableList.of(
                new InvoiceLine(AUCTION_ID, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.LISTING_FEE, LISTING_FEE_AMOUNT));

        ImmutableList<InvoiceLine> invoiceLinesWithoutDuplicate = ImmutableList.of(
                new InvoiceLine(AUCTION_ID, FeeType.BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLine(AUCTION_ID, FeeType.LISTING_FEE, LISTING_FEE_AMOUNT));

        InvoiceExpectation invoiceExpectationWithDuplicate = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, LISTING_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT));

        InvoiceExpectation invoiceExpectationWithoutDuplicate = new InvoiceExpectation(
                new InvoiceLineExpectation(LISTING_FEE, LISTING_FEE_AMOUNT),
                new InvoiceLineExpectation(BUY_IT_NOW_FEE, BUY_IT_NOW_FEE_AMOUNT));

        assertInvoicesMatch(invoiceExpectationWithDuplicate, invoiceLinesWithDuplicate);
        assertInvoicesMatch(invoiceExpectationWithoutDuplicate, invoiceLinesWithoutDuplicate);

        assertInvoicesDoNotMatch(invoiceExpectationWithoutDuplicate, invoiceLinesWithDuplicate);
        assertInvoicesDoNotMatch(invoiceExpectationWithDuplicate, invoiceLinesWithoutDuplicate);
    }

    private void assertInvoicesMatch(InvoiceExpectation expectation, List<InvoiceLine> invoiceLines) {
        assertFalse(isAssertionErrorFired(expectation, invoiceLines));
    }

    private void assertInvoicesDoNotMatch(InvoiceExpectation expectation, List<InvoiceLine> invoiceLines) {
        assertTrue(isAssertionErrorFired(expectation, invoiceLines));
    }

    private boolean isAssertionErrorFired(InvoiceExpectation expectation, List<InvoiceLine> invoiceLines) {
        boolean caughtExpectation = false;
        try {
            expectation.assertEqual(invoiceLines);
        } catch (AssertionError error) {
            caughtExpectation = true;
        }
        return caughtExpectation;
    }
}
