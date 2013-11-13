package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class SveaSoapBuilderTest {

    @Test
    public void testRequest() {
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
}
