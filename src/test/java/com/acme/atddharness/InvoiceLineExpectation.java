package com.acme.atddharness;

import com.acme.auctionclient.FeeType;
import com.acme.auctionclient.InvoiceLine;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.math.BigDecimal;


/**
 * Test utility responsible for helping to specify invoice line expectations.
 */
public class InvoiceLineExpectation {
    private final FeeType feeType;
    private final BigDecimal amount;

    public InvoiceLineExpectation(FeeType feeType, BigDecimal amount) {
        this.feeType = feeType;
        this.amount = amount;
    }

    public InvoiceLineExpectation(FeeType feeType, String amount) {
        this(feeType, new BigDecimal(amount));
    }

    public boolean matchesInvoice(@Nullable InvoiceLine invoiceLine) {
        return invoiceLine != null &&
                feeType.equals(invoiceLine.getFeeType()) &&
                amount.equals(invoiceLine.getAmount());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("feeType", feeType)
                .add("amount", amount)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceLineExpectation that = (InvoiceLineExpectation) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (feeType != that.feeType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = feeType.hashCode();
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
