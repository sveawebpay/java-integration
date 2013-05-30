package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

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
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;


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
        .setCountryCode(COUNTRYCODE.SE)
        .deliverInvoiceOrder()
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);          
    }

    @Test
    public void testCompanyIdRequest() throws ValidationException, Exception {
    	 SveaRequest<SveaCreateOrder> request = WebPay.createOrder()                
            	.addOrderRow(Item.orderRow()
                    .setArticleNumber("1")
                    .setQuantity(2)
                    .setAmountExVat(100.00)
                    .setDescription("Specification")
                    .setName("Prod")
                    .setUnit("st")
                    .setVatPercent(25)
                    .setDiscountPercent(0))
                                 
    	        .addCustomerDetails(Item.companyCustomer()
    	        	.setNationalIdNumber("4354kj"))
               		
            	.setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.SEK)
                .useInvoicePayment()// returns an InvoiceOrder object
                .prepareRequest();

    	 assertEquals(request.request.Auth.ClientNumber.toString(), "79021"); 
    	 assertEquals(request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber, "4354kj");
    }
    
    @Test
    public void testCompanyIdResponse() throws ValidationException, Exception {
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
                                 
    	        .addCustomerDetails(Item.companyCustomer()
    	        	.setNationalIdNumber("4608142222"))
               		
            	.setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.SEK)
                .useInvoicePayment()// returns an InvoiceOrder object
                //.prepareRequest();
                    .doRequest();
    	assertEquals(response.isIndividualIdentity, false);
    	assertEquals(response.isOrderAccepted(), true);
    	 //assertEquals(request.request.Auth.ClientNumber.toString(), "79021"); 
    	 //assertEquals(request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber, "4354kj");
    }
    
    @Test
    public void testDECompanyIdentity() throws ValidationException, Exception {
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
                                 
    	        .addCustomerDetails(Item.companyCustomer()
    	        	.setNationalIdNumber("12345")
    	        	.setVatNumber("DE123456789")
    	        	.setStreetAddress("Adalbertsteinweg", "1")
    	        	.setZipCode("52070")
    	        	.setLocality("AACHEN"))
               		
            	.setCountryCode(COUNTRYCODE.DE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.EUR)
                .useInvoicePayment()// returns an InvoiceOrder object
              //  .setPasswordBasedAuthorization("germanytest", "germanytest", 14997)
                //.prepareRequest();
                    .doRequest();
    	 
    	assertEquals(response.isIndividualIdentity, false);
    	assertEquals(response.isOrderAccepted(), true);    
    }
    
    @Test
    public void testNLCompanyIdentity() throws ValidationException, Exception {
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
                
    	        .addCustomerDetails(Item.companyCustomer()
    	        	//.setNationalIdNumber("12345")
    	        	.setCompanyName("Svea bakkerij 123")
    	        	.setVatNumber("NL123456789A12")
    	        	.setStreetAddress("broodstraat", "1")
    	        	.setZipCode("1111 CD")
    	        	.setLocality("BARENDRECHT"))
               		
            	.setCountryCode(COUNTRYCODE.NL)
            	
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.EUR)
                .useInvoicePayment()// returns an InvoiceOrder object
               // .setPasswordBasedAuthorization("hollandtest", "hollandtest", 85997)
                    .doRequest();
    	 
    	assertEquals(false, response.isIndividualIdentity);
    	assertEquals(true, response.isOrderAccepted());
    }
    
    @Test
    public void testDeliverPaymentPlanOrderResult() throws Exception {
    	long orderId = createPaymentPlanAndReturnOrderId();
    	
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
        .deliverPaymentPlanOrder()
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);   
       
    }
    
    @Test
    public void testResultGetPaymentPlanParams() throws Exception {
        
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
        	.setCountryCode(COUNTRYCODE.SE)	
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
	        .setCountryCode(COUNTRYCODE.SE)
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
            .setPhoneNumber(999999)
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
            .usePaymentPlanPayment(code)  //returns a paymentPlanOrder object                 
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
           .setPhoneNumber(999999)
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
