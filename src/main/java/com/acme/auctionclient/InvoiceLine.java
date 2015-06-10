package com.acme.auctionclient;

import com.google.common.base.Objects;

import java.math.BigDecimal;

/**
 * Invoice line item charge.
 */
public class InvoiceLine {
    private final String auctionId;
    private final FeeType feeType;
    private final BigDecimal amount;

    public InvoiceLine(String auctionId, FeeType feeType, BigDecimal amount) {
        this.auctionId = auctionId;
        this.feeType = feeType;
        this.amount = amount;
    }

    public InvoiceLine(String auctionId, FeeType feeType, String amount) {
        this(auctionId, feeType, new BigDecimal(amount));
    }

    public String getAuctionId() {
        return auctionId;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("auctionId", auctionId)
                .add("feeType", feeType)
                .add("amount", amount)
                .toString();
    }
}
