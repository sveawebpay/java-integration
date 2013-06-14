package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.ConfigurationProviderInterfaceTest;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CreateInvoiceOrderTest {
    
    @Test
    public void testInvoiceRequestForCustomerIdentityIndividualFromSE() throws Exception {
    	CreateOrderResponse response = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .addCustomerDetails(Item.individualCustomer().setNationalIdNumber("194605092222"))
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()
            .doRequest();
    
        assertEquals(true, response.isOrderAccepted());
        assertEquals(true, response.sveaWillBuyOrder);
        assertEquals(250.00, response.amount, 0);
        
        //CustomerIdentity
        assertEquals("194605092222", response.customerIdentity.getNationalIdNumber());
        assertEquals("SE", response.customerIdentity.getCountryCode());
        assertEquals("Individual", response.customerIdentity.getCustomerType());
        assertEquals("Invoice", response.orderType);
        assertEquals("Persson, Tess T", response.customerIdentity.getFullName());
        assertEquals("Testgatan 1", response.customerIdentity.getStreet());
        assertEquals("c/o Eriksson, Erik", response.customerIdentity.getCoAddress());
        assertEquals("99999", response.customerIdentity.getZipCode());
        assertEquals("Stan", response.customerIdentity.getCity());
    }
    
    @Test
    public void testInvoiceRequestFailing() throws Exception {
    	CreateOrderResponse response = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(""))
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()
            .doRequest();
    
        assertEquals(false, response.isOrderAccepted());
    }
    
    @Test
    public void testCalculationWithDecimalsInVatPercent() throws ValidationException, Exception {
        CreateOrderResponse response = WebPay.createOrder()
    	        .addOrderRow(Item.orderRow()
    	            .setArticleNumber("1")
    	            .setQuantity(1)
    	            .setAmountExVat(22.68)
    	            .setDescription("Specification")
    	            .setName("Prod")
    	            .setVatPercent(12.5)
    	            .setDiscountPercent(0))
    	        .addCustomerDetails(Item.individualCustomer().setNationalIdNumber("194605092222"))
    	            .setCountryCode(COUNTRYCODE.SE)
    	            .setOrderDate("2012-12-12")
    	            .setClientOrderNumber("33")
    	            .setCurrency(CURRENCY.SEK)
    	            .useInvoicePayment()
    	            .doRequest();
    	
    	assertEquals(true, response.isOrderAccepted());
    	assertEquals(25.52, response.amount, 0);
    }
    
    @Test
    public void testFormationOfDecimalsInCalculation() throws ValidationException, Exception {
        CreateOrderResponse response = WebPay.createOrder()
    	        .addOrderRow(Item.orderRow()
    	            .setArticleNumber("1")
    	            .setQuantity(2)
    	            .setAmountExVat(22.68)    
    	            .setDescription("Specification")
    	            .setName("Prod")
    	            .setVatPercent(12.5)
    	            .setDiscountPercent(0))
    	        .addCustomerDetails(Item.individualCustomer().setNationalIdNumber("194605092222"))
    	            .setCountryCode(COUNTRYCODE.SE)
    	            .setOrderDate("2012-12-12")
    	            .setClientOrderNumber("33")
    	            .setCurrency(CURRENCY.SEK)
    	            .useInvoicePayment()
    	            .doRequest();
    	
    	assertEquals(true, response.isOrderAccepted());
    	assertEquals(51.03, response.amount, 0);
    }
    
    @Test
    public void testInvoiceCompanySe() throws Exception {
    	CreateOrderResponse response = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.SEK)
        .addCustomerDetails(Item.companyCustomer()
        		.setNationalIdNumber("4608142222"))
        .addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setVatPercent(25)
                .setDiscountPercent(0))
       .useInvoicePayment()
                .doRequest();

        assertEquals(true, response.isOrderAccepted());
        assertEquals(true, response.sveaWillBuyOrder);
        //assertEquals("4608142222", response.customerIdentity.nationalIdNumber);
        assertEquals("SE", response.customerIdentity.getCountryCode());
    }
    
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityIndividualFromNL() throws Exception {
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
           .setBirthDate(1955, 03, 07)
           .setInitials("SB")
           .setName("Sneider", "Boasman")
           .setStreetAddress("Gate","42")
           .setLocality("BARENDRECHT")
           .setZipCode("1102 HG")
           .setCoAddress("138"))
           
           .setCountryCode(COUNTRYCODE.NL)
           .setClientOrderNumber("33")
           .setOrderDate("2012-12-12")
           .setCurrency(CURRENCY.EUR)
           .useInvoicePayment()// returns an InvoiceOrder object
        //   .setPasswordBasedAuthorization("hollandtest", "hollandtest", 85997)
           .doRequest();  
        
          assertEquals(true, response.isOrderAccepted());
          assertEquals(true, response.sveaWillBuyOrder);
          assertEquals(250.00, response.amount, 0);
          assertEquals("0", response.getResultCode());
          assertEquals("Invoice" , response.orderType);
          
          //CustomerIdentity
          assertEquals(null, response.customerIdentity.getEmail());
          assertEquals(null, response.customerIdentity.getIpAddress());
          assertEquals("NL", response.customerIdentity.getCountryCode());
          assertEquals("23", response.customerIdentity.getHouseNumber());
          assertEquals("Individual", response.customerIdentity.getCustomerType());
          assertEquals(null, response.customerIdentity.getPhoneNumber());
          assertEquals("Sneider Boasman", response.customerIdentity.getFullName());
          assertEquals("Gate 42", response.customerIdentity.getStreet());
          assertEquals("138", response.customerIdentity.getCoAddress());
          assertEquals("1102 HG", response.customerIdentity.getZipCode());
          assertEquals("BARENDRECHT", response.customerIdentity.getCity());
    }
    
    @Test
    public void testInvoiceDoRequestWithIpAddressSetSE() throws Exception {
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
                
             .addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setVatPercent(25)
                .setDiscountPercent(0))
                                
             .addCustomerDetails(Item.individualCustomer()
                 .setNationalIdNumber("194605092222")
                 .setIpAddress("123.123.123"))
             .setCountryCode(COUNTRYCODE.SE)
             .setOrderDate("2012-12-12")
             .setClientOrderNumber("33")
             .setCurrency(CURRENCY.SEK)
             .useInvoicePayment()
             .doRequest();
        
    	assertEquals(response.isOrderAccepted(), true);
    }

    @Test
    public void testInvoiceRequestUsingAmountIncVatWithZeroVatPercent() throws Exception {
        CreateOrderResponse response = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setVatPercent(0)
            .setDiscountPercent(0))
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
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .setCustomerReference("33")
            .useInvoicePayment()
    	
    	.doRequest();
    	 
    	assertEquals(response.isOrderAccepted(), true);
    }
    
    @Test
    public void testFailOnMissingCountryCodeOfCloseOrder() throws Exception {
        Long orderId = 0L;
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        
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
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber("194605092222"))
        .setCountryCode(COUNTRYCODE.SE)
        .setClientOrderNumber("33")
        .setOrderDate("2012-12-12")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()
        .prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        
        try {
            String xml = xmlBuilder.getCreateOrderEuXml(request.request);
            String url = SveaConfig.getTestWebserviceUrl().toString();
            String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
            NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
            CreateOrderResponse response = new CreateOrderResponse(soapResponse);
            orderId = response.orderId;
            
            assertEquals(true, response.isOrderAccepted());
        } catch (Exception e) {
            throw e;
        }
       
        soapBuilder = new SveaSoapBuilder();
        
        CloseOrder closeRequest = WebPay.closeOrder()
                .setOrderId(orderId)
                //.setCountryCode(COUNTRYCODE.SE)
                .closeInvoiceOrder();
        
        String expectedMsg = "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        
        assertEquals(expectedMsg, closeRequest.validateRequest());
    }
    
	@Test
	public void testConfiguration() throws ValidationException, Exception {
		ConfigurationProviderInterfaceTest conf = new ConfigurationProviderInterfaceTest();
		CreateOrderResponse response = WebPay.createOrder(conf)
	    		.addOrderRow(Item.orderRow()
	                .setArticleNumber("1")
	                .setQuantity(2)
	                .setAmountExVat(100.00)
	                .setDescription("Specification")
	                .setName("Prod")
	                .setUnit("st")
	                .setVatPercent(25)
	                .setDiscountPercent(0))
	                
	             .addOrderRow(Item.orderRow()
	                .setArticleNumber("1")
	                .setQuantity(2)
	                .setAmountExVat(100.00)
	                .setDescription("Specification")
	                .setName("Prod")
	                .setVatPercent(25)
	                .setDiscountPercent(0))
	                    
	             .addCustomerDetails(Item.individualCustomer()
	                 .setNationalIdNumber("194605092222")
	                 .setIpAddress("123.123.123"))
	             .setCountryCode(COUNTRYCODE.SE)
	             .setOrderDate("2012-12-12")
	             .setClientOrderNumber("33")
	             .setCurrency(CURRENCY.SEK)
	             .useInvoicePayment()
	             .doRequest();
	        
	    assertEquals(response.isOrderAccepted(), true);
	}
    
    @Test
    public void testFormatShippingFeeRowsZero() throws ValidationException, Exception {
    	  CreateOrderResponse response = WebPay.createOrder()
    		        .addOrderRow(Item.orderRow()
    		            .setArticleNumber("1")
    		            .setQuantity(2)
    		            .setAmountExVat(10)
    		            .setDescription("Specification")
    		            .setName("Prod")
    		            .setVatPercent(0)
    		            .setDiscountPercent(0))
    		       
    		       .addFee(Item.shippingFee()  
			            .setShippingId("0")
			            .setName("Tess")
			            .setDescription("Tester")
			            .setAmountExVat(0)
			            .setVatPercent(0)
			            .setUnit("st"))
    		        
    		        .addCustomerDetails(Item.individualCustomer()
    		            .setNationalIdNumber("194605092222"))
    		    
    		            .setCountryCode(COUNTRYCODE.SE)
    		            .setOrderDate("2012-12-12")
    		            .setClientOrderNumber("33")
    		            .setCurrency(CURRENCY.SEK)
    		            .setCustomerReference("33")
    		            .useInvoicePayment()
    		        .doRequest();
    	  
    	  assertEquals(true, response.isOrderAccepted());
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
}
