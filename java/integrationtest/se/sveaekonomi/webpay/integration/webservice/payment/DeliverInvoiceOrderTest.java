package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;

public class DeliverInvoiceOrderTest {
    
    @Test
    public void testDeliverInvoiceOrderDoRequest() throws Exception {
    	DeliverOrderResponse response = WebPay.deliverOrder()
    		.addOrderRow(Item.orderRow()
    			.setArticleNumber("1")
    			.setQuantity(2)
    			.setAmountExVat(100.00)
    			.setDescription("Specification")
    			.setName("Prod")
    			.setUnit("st")
    			.setVatPercent(25)
    			.setDiscountPercent(0))  
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
            .addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
            .setOrderId(orderId)
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(COUNTRYCODE.SE)
            .deliverInvoiceOrder()
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);
    }
	
    private long createInvoiceAndReturnOrderId() throws Exception {
    	CreateOrderResponse response = WebPay.createOrder()
        	.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
	        .addCustomerDetails(Item.individualCustomer()
           		.setNationalIdNumber("194605092222"))
        	.setCountryCode(COUNTRYCODE.SE)
            .setClientOrderNumber("33")
            .setOrderDate("2012-12-12")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment() // returns an InvoiceOrder object
            .doRequest();
        
        return response.orderId;
    }
}
