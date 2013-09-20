package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverInvoiceOrderTest {
    
    @Test
    public void testDeliverInvoiceOrderDoRequest() throws Exception {
    	DeliverOrderResponse response = WebPay.deliverOrder()
    		.addOrderRow(TestingTool.createOrderRow())  
    		.setOrderId(54086L)
    		.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
    		.setCountryCode(COUNTRYCODE.SE)
    		.deliverInvoiceOrder()
    		.doRequest();
    
    	response.getErrorMessage();
    }
    
    @Test
    public void testDeliverInvoiceOrderResult() throws Exception {
    	long orderId = createInvoiceAndReturnOrderId();
    	
    	DeliverOrderResponse response = WebPay.deliverOrder()
            .addOrderRow(TestingTool.createOrderRow())
            .setOrderId(orderId)
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(COUNTRYCODE.SE)
            .deliverInvoiceOrder()
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);
        assertEquals(response.getInvoiceDistributionType(), "Post");
        assertTrue(null != response.getOcr() && 0 < response.getOcr().length());
        assertTrue(0.0 == response.getLowestAmountToPay());
    }
	
    private long createInvoiceAndReturnOrderId() throws Exception {
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
        
        return response.orderId;
    }
}
