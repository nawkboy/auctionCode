package com.acme.auctionclient;

/**
 * Exception for use by AuctionService.
 */
public class AuctionServiceException extends Exception {
    public AuctionServiceException() {
        super();
    }

    public AuctionServiceException(String message) {
        super(message);
    }

    public AuctionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuctionServiceException(Throwable cause) {
        super(cause);
    }

    protected AuctionServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
