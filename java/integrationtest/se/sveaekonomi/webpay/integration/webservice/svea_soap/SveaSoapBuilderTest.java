package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class SveaSoapBuilderTest {
    
    @Test
    public void testRequest() throws Exception {
        CreateOrderResponse response = WebPay.createOrder()
            .addOrderRow(TestingTool.createOrderRow())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber("194605092222"))
            .setCountryCode(COUNTRYCODE.SE)
            .setClientOrderNumber("33")
            .setOrderDate("2012-12-12")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
}
