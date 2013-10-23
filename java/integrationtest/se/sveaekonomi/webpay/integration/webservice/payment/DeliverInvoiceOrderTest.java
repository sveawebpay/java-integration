package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverInvoiceOrderTest {
    
    @Test
    public void testDeliverInvoiceOrderDoRequest() {
        DeliverOrderResponse response = WebPay.deliverOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(54086L)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        response.getErrorMessage();
    }
    
    @Test
    public void testDeliverInvoiceOrderResult() {
        long orderId = createInvoiceAndReturnOrderId();
        
        DeliverOrderResponse response = WebPay.deliverOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(orderId)
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Post", response.getInvoiceDistributionType());
        assertNotNull(response.getOcr());
        assertTrue(0 < response.getOcr().length());
        assertEquals(0.0, response.getLowestAmountToPay(), 0);
    }
    
    private long createInvoiceAndReturnOrderId() {
        CreateOrderResponse response = WebPay.createOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber("33")
            .setOrderDate("2012-12-12")
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .doRequest();
        
        return response.orderId;
    }
}
