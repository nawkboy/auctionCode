package com.acme.auctionclient;

import com.acme.atddharness.User;
import com.acme.auction.AuctionListingFactory;
import com.acme.auction.DefaultAuctionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.mockito.Mockito.mock;

/**
 * Login focused tests of DefaultAuctionService.
 * This is created as a separate test class to support use of JUnit's
 * parameterized test functionality.
 */
@RunWith(value = Parameterized.class)
public class AuctionServiceLoginMethodTest {
    private static final Date FIRST_DAY = new Date(0);

    private final String description;
    private final String userName;
    private final String userPassword;
    private final boolean isHappyPath;
    private AuctionService auctionService;

    public AuctionServiceLoginMethodTest(String description, String userName, String userPassword, boolean isHappyPath) {
        super();
        this.description = description;
        this.userName = userName;
        this.userPassword = userPassword;
        this.isHappyPath = isHappyPath;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {"Fred good user/pass", User.SELLER_FRED.getUsername(), User.SELLER_FRED.getPassword(), true},
                {"George good user/pass", User.BUYER_GEORGE.getUsername(), User.BUYER_GEORGE.getPassword(), true},
                {"Sally good user/pass", User.BUYER_SALLY.getUsername(), User.BUYER_SALLY.getPassword(), true},
                {"Sally user, George pass", User.BUYER_SALLY.getUsername(), User.BUYER_GEORGE.getPassword(), false},
                {"Unknown user", "evilUser", User.BUYER_SALLY.getPassword(), false},
                {"Null user", null, User.BUYER_SALLY.getPassword(), false},
                {"Null password", User.BUYER_SALLY.getUsername(), null, false},
                {"Bad password", User.BUYER_SALLY.getUsername(), "evilPassword", false}
        };
        return Arrays.asList(data);
    }

    @Test
    public void testUsage() throws AuctionServiceException {
        if (isHappyPath) {
            assertHappyPath();
        } else {
            assertUnhappyPath();
        }
    }

    @Before
    public void setUp() {
        AuctionListingFactory auctionListingFactory = mock(AuctionListingFactory.class);
        auctionService = new DefaultAuctionService(auctionListingFactory);
    }

    private void assertHappyPath() throws AuctionServiceException {


        String fredAuthToken = auctionService.login(this.userName, this.userPassword);

        Assert.assertNotNull("Test Case: " + this.description, fredAuthToken);
    }

    private void assertUnhappyPath() {
        boolean exceptionCaught = false;
        try {
            auctionService.login(this.userName, this.userPassword);
        } catch (AuctionServiceException e) {
            exceptionCaught = true;
        }

        Assert.assertTrue("Test Case: " + this.description, exceptionCaught);
    }
}
