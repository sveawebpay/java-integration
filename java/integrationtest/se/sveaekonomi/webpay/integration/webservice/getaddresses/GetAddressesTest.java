package se.sveaekonomi.webpay.integration.webservice.getaddresses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class GetAddressesTest {
    
    @Test
    public void testGetAddresses() throws Exception {
        GetAddressesResponse response = WebPay.getAddresses()
            .setCountryCode(COUNTRYCODE.SE)
            .setIndividual("460509-2222")
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Persson, Tess T", response.getLegalName());
    }
    
    @Test
    public void testResultGetAddresses() throws Exception {
        GetAddressesResponse response = WebPay.getAddresses()
            .setCountryCode(COUNTRYCODE.SE)
            .setIndividual("194605092222")
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals(response.getFirstName(), "Tess T");
        assertEquals(response.getLastName(), "Persson");
        assertEquals(response.getAddressLine2(), "Testgatan 1");
        assertEquals(response.getPostcode(), "99999");
        assertEquals(response.getPostarea(), "Stan");
    }
}
