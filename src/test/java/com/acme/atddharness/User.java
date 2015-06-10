package com.acme.atddharness;

/**
 * Enumerated list of test users.
 */
public enum User {
    SELLER_DEFAULT("default.seller@acme.com", "letsSell"),
    SELLER_FRED("fred.seller@acme.com", "sellingIsFun"),
    BUYER_SALLY("sally.buyer@acme.com", "gotToBuy"),
    BUYER_GEORGE("george.buyer@acme.com", "sallyIsAnnoying");

    private final String username;
    private final String password;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
