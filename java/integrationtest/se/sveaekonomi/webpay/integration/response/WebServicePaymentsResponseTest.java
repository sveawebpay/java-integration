package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;


public class WebServicePaymentsResponseTest {
    
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
        .deliverInvoiceOrder()
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);        
    }

    
    @Test
    public void testResultGetPaymentPlanParams() throws Exception {
        
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.createTestConfig())
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);
        assertEquals(response.getCampaignCodes().get(0).getCampaignCode(), "213060");
        assertEquals(response.getCampaignCodes().get(0).getDescription(), "Köp nu betala om 3 månader (räntefritt)");
        assertEquals(response.getCampaignCodes().get(0).getPaymentPlanType(), "InterestAndAmortizationFree");
        assertEquals(response.getCampaignCodes().get(0).getContractLengthInMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getInitialFee(), "100");
        assertEquals(response.getCampaignCodes().get(0).getNotificationFee(), "29");
        assertEquals(response.getCampaignCodes().get(0).getInterestRatePercent(), "0");
        assertEquals(response.getCampaignCodes().get(0).getNumberOfInterestFreeMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getNumberOfPaymentFreeMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getFromAmount(), "1000");
        assertEquals(response.getCampaignCodes().get(0).getToAmount(), "50000");
    }
    
    
	@Test
	public void testResultGetAddresses() throws Exception {
	    
	    GetAddressesResponse request = WebPay.getAddresses()
	        .setCountryCode("SE")
	        .setOrderTypeInvoice()
	        .setIndividual("194605092222")
	        .doRequest();
	    
	    assertEquals(request.isOrderAccepted(), true);
	    assertEquals(request.getFirstName(), "Tess T");
	    assertEquals(request.getLastName(), "Persson");
	    assertEquals(request.getAddressLine2(), "Testgatan 1");
	    assertEquals(request.getPostcode(), "99999");
	    assertEquals(request.getPostarea(), "Stan");
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
            .useInvoicePayment()// returns an InvoiceOrder object
                .doRequest();
      
        return response.orderId;
    }
}
