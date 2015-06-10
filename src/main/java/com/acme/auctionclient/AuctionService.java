package com.acme.auctionclient;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Example of a trivial auction service webservice API.
 */
public interface AuctionService {

    String login(String username, String password) throws AuctionServiceException;

    String createListing(String authToken, BigDecimal startingPrice,
                         @Nullable BigDecimal buyItNowPrice, int auctionLength)
            throws AuctionServiceException;

    void bid(String authToken, String listingId, BigDecimal bidAmount) throws AuctionServiceException;

    void buyItNow(String authToken, String listingId) throws AuctionServiceException;

    List<InvoiceLine> fetchInvoices(String authToken, String listingId) throws AuctionServiceException;
}
