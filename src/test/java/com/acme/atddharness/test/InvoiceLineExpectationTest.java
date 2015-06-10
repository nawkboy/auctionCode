package com.acme.atddharness.test;

import com.acme.atddharness.InvoiceLineExpectation;
import com.acme.auctionclient.FeeType;
import com.acme.auctionclient.InvoiceLine;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The InvoiceLineExpectation test utility is complex enough to justify
 * it's own test case.
 */
public class InvoiceLineExpectationTest {
    @Test
    public void testMatchingInvoiceLine() {
        InvoiceLine invoiceLine = new InvoiceLine("dummyAuctionId", FeeType.BUY_IT_NOW_FEE, "5.00");
        InvoiceLineExpectation lineExpectation = new InvoiceLineExpectation(FeeType.BUY_IT_NOW_FEE, "5.00");
        assertTrue(lineExpectation.matchesInvoice(invoiceLine));
    }

    @Test
    public void testFeeTypeDiffersFromInvoiceLine() {
        InvoiceLine invoiceLine = new InvoiceLine("dummyAuctionId", FeeType.LISTING_FEE, "5.00");
        InvoiceLineExpectation lineExpectation = new InvoiceLineExpectation(FeeType.BUY_IT_NOW_FEE, "5.00");
        assertFalse(lineExpectation.matchesInvoice(invoiceLine));
    }

    @Test
    public void testAmountDiffersFromInvoiceLine() {
        InvoiceLine invoiceLine = new InvoiceLine("dummyAuctionId", FeeType.BUY_IT_NOW_FEE, "5.01");
        InvoiceLineExpectation lineExpectation = new InvoiceLineExpectation(FeeType.BUY_IT_NOW_FEE, "5.00");
        assertFalse(lineExpectation.matchesInvoice(invoiceLine));
    }

    @Test
    public void testNullInvoiceLine() {
        InvoiceLineExpectation lineExpectation = new InvoiceLineExpectation(FeeType.BUY_IT_NOW_FEE, "5.00");
        assertFalse(lineExpectation.matchesInvoice(null));
    }

}
