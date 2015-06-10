package com.acme.auction;

import java.util.Date;

/**
 * Time service to be used in place of System.getTime()
 */
public interface TimeService {
    Date getTime();
}
