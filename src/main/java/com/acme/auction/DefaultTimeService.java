package com.acme.auction;

import java.util.Date;

/**
 * Default time service implementation.
 * Always returns UTC time.
 */
public class DefaultTimeService implements TimeService {

    public DefaultTimeService() {
        super();
    }

    public Date getTime() {
        return new Date();
    }
}
