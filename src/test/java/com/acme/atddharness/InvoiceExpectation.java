package com.acme.atddharness;

import com.acme.auctionclient.InvoiceLine;
import com.google.common.base.Objects;
import junit.framework.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test utility responsible for helping to specify invoice expectations.
 */
public class InvoiceExpectation {

    private final Map<InvoiceLineExpectation, Integer> lineExpectationsMap
            = new HashMap<InvoiceLineExpectation, Integer>();

    public InvoiceExpectation(List<InvoiceLineExpectation> invoiceLineExpectations) {
        for (InvoiceLineExpectation lineExpectation : invoiceLineExpectations) {
            Integer count = lineExpectationsMap.get(lineExpectation);
            if (count == null) {
                lineExpectationsMap.put(lineExpectation, 1);
            } else {
                lineExpectationsMap.put(lineExpectation, ++count);
            }
        }
    }

    public InvoiceExpectation(InvoiceLineExpectation... expectations) {
        this(Arrays.asList(expectations));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("lineExpectationsMap", lineExpectationsMap)
                .toString();
    }

    public void assertEqual(List<InvoiceLine> invoiceLines) {

        int expectedOverallCount = 0;
        for (InvoiceLineExpectation lineExpectation : lineExpectationsMap.keySet()) {
            int expectedCount = lineExpectationsMap.get(lineExpectation);
            expectedOverallCount += expectedCount;

            int observedCount = 0;
            for (InvoiceLine invoiceLine : invoiceLines) {
                if (lineExpectation.matchesInvoice(invoiceLine)) {
                    observedCount++;
                }
            }
            Assert.assertEquals("Expecting to see " + expectedCount + " invoice lines matching " + lineExpectation
                    + " Observed invoice lines were: " + invoiceLines
                    + " Invoice expectations were: " + lineExpectationsMap,
                    expectedCount, observedCount);
        }

        Assert.assertEquals("overall number of invoice lines matches expectations"
                + " Observed invoice lines were: " + invoiceLines
                + " Invoice expectations were: " + lineExpectationsMap, expectedOverallCount, invoiceLines.size());
    }
}
