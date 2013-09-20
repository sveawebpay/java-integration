package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;

public class DoPaymentPlanPaymentTest {
    
	@Test
	public void testPaymentPlanRequestReturnsAcceptedResult() throws Exception {
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams()
				.setCountryCode(COUNTRYCODE.SE)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
		
		CreateOrderResponse response = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(1000.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber("194605092222")
            .setInitials("SB")
            .setBirthDate(1923, 12, 12)
            .setName("Tess", "Testson")
            .setEmail("test@svea.com")
            .setPhoneNumber("999999")
            .setIpAddress("123.123.123")
            .setStreetAddress("Gatan", "23")
            .setCoAddress("c/o Eriksson")
            .setZipCode("9999")
            .setLocality("Stan"))
                
        .setCountryCode(COUNTRYCODE.SE)
        .setCustomerReference("33")
        .setClientOrderNumber("nr26")
        .setOrderDate("2012-12-12")
        .setCurrency(CURRENCY.SEK)
        .setCountryCode(COUNTRYCODE.SE)
        .usePaymentPlanPayment(code) //returns a paymentPlanOrder object
        .doRequest();
		
		assertEquals(response.isOrderAccepted(), true);
	}
    
    @Test
    public void testDeliverPaymentPlanOrderResult() throws Exception {
    	long orderId = createPaymentPlanAndReturnOrderId();
    	
    	DeliverOrderResponse response = WebPay.deliverOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(1000.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
            
        .setOrderId(orderId)
        .setNumberOfCreditDays(1)
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setCountryCode(COUNTRYCODE.SE)
        .deliverPaymentPlanOrder()
        .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);
    }
			
	private long createPaymentPlanAndReturnOrderId() throws Exception {
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams()
				.setCountryCode(COUNTRYCODE.SE)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
		
		CreateOrderResponse response = WebPay.createOrder()
           .addOrderRow(Item.orderRow()
           .setArticleNumber("1")
           .setQuantity(2)
           .setAmountExVat(1000.00)
           .setDescription("Specification")
           .setName("Prod")
           .setUnit("st")
           .setVatPercent(25)
           .setDiscountPercent(0))
        .addCustomerDetails(Item.individualCustomer()
           .setNationalIdNumber("194605092222")
           .setInitials("SB")
           .setBirthDate(1923, 12, 12)
           .setName("Tess", "Testson")
           .setEmail("test@svea.com")
           .setPhoneNumber("999999")
           .setIpAddress("123.123.123")
           .setStreetAddress("Gatan", "23")
           .setCoAddress("c/o Eriksson")
           .setZipCode("9999")
           .setLocality("Stan"))
           
        .setCountryCode(COUNTRYCODE.SE)
        .setCustomerReference("33")
        .setClientOrderNumber("nr26")
        .setOrderDate("2012-12-12")
        .setCurrency(CURRENCY.SEK)
        .usePaymentPlanPayment(code)  //returns a paymentPlanOrder object
        .doRequest();
		
		return response.orderId;
	}
}
