package se.sveaekonomi.webpay.integration.webservice.getaddresses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class GetAddressesTest {
    
    @Test
    public void testGetAddresses() {
        GetAddressesResponse response = WebPay.getAddresses()
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setIndividual("460509-2222")
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Persson, Tess T", response.getLegalName());
    }
    
    @Test
    public void testResultGetAddresses() {
        GetAddressesResponse response = WebPay.getAddresses()
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setIndividual(TestingTool.DefaultTestIndividualNationalIdNumber)
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Tess T", response.getFirstName());
        assertEquals("Persson", response.getLastName());
        assertEquals("Testgatan 1", response.getAddressLine2());
        assertEquals("99999", response.getPostcode());
        assertEquals("Stan", response.getPostarea());
    }
}
