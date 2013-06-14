package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class WebServiceOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public WebServiceOrderValidatorTest() {
        orderValidator = new WebServiceOrderValidator();
    }
    
    @Test
    public void testCheckOfIdentityClass() {
        CreateOrderBuilder order = WebPay.createOrder()
        	.addCustomerDetails(Item.companyCustomer()
                .setNationalIdNumber("666666")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123.123")
                .setStreetAddress("Gatan", "23")
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan")); 
        orderValidator.validate(order);
    }
    
    @Test
    public void testCustomerIdentityIsNull() {
    	 String expectedMessage = "MISSING VALUE - CustomerIdentity must be set.\n"
    			 + "MISSING VALUE - CountryCode is required. Use setCountryCode().\n"
    			 + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
    			 + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
    	CreateOrderBuilder order = WebPay.createOrder()
            	.setValidator(new VoidValidator())
            	.setClientOrderNumber("1")
            	
            	.addCustomerDetails(null);
    	
    	assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCountryCodeOnCreateOrder() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setValidator(new VoidValidator())
        	.setClientOrderNumber("1")
        	.addCustomerDetails(Item.individualCustomer()
        			.setNationalIdNumber("194609052222"));
                
       assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testFailOnMissingCountryCodeOnDeliverOrder() throws Exception {
        String expectedMessage ="MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        try {
        WebPay.deliverOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .setNumberOfCreditDays(1)
        .setOrderId(2345L)
     //   .setCountryCode(COUNTRYCODE.SE)
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .deliverInvoiceOrder()
        .doRequest();
    	//check that exception is thrown
    	assertTrue(false);
    	
    	} catch (ValidationException e) {
    		assertEquals(e.getMessage(), expectedMessage);
    	}
    }
    
    @Test
    public void testFailOnMissingValuesForGetAddresses() throws Exception {
        
        String expectedMessage ="MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" 
        		+"MISSING VALUE - orderType is required, use one of: setOrderTypePaymentPlan() or setOrderTypeInvoice().\n"
        		+"MISSING VALUE - either nationalNumber or companyId is required. Use: setCompany(...) or setIndividual(...).\n";
        try {
        	WebPay.getAddresses()
        		.doRequest();
          //  .setIndividual("460509-2222");

        //check that exception is thrown
      	assertTrue(false);
	  	}
	  	catch (ValidationException e) {
	  		assertEquals(e.getMessage(), expectedMessage);
	  	}
    }
    
    @Test
    public void testFailOnMissingCustomerIdentity() {
        String expectedMessage = "MISSING VALUE - National number(ssn) is required for individual customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator())
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.SE);
 
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingOrderRows() {
        String expectedMessage = "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setValidator(new VoidValidator())
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.SE)
        	.addCustomerDetails(Item.individualCustomer()
        	.setNationalIdNumber("194609052222"));            

        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingOrderRowValues() {
        String expectedMessage = "MISSING VALUE - Quantity is required in Item object. Use Item.setQuantity().\n" 
                + "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two of: setAmountExVat(), setAmountIncVat or setVatPercent().\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setClientOrderNumber("1")
        	.addOrderRow(Item.orderRow())
        	.addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber("194605092222"))
                .setCountryCode(COUNTRYCODE.SE)
            .setValidator(new VoidValidator());
         
       assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingSsnForSeOrder() {
        String expectedMessage = "MISSING VALUE - National number(ssn) is required for individual customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        .addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.SE);
       
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingIdentityDataForDeOrder() {
       String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
               + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n"
               + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
               + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
               + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n"
               + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
               + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
       CreateOrderBuilder order = WebPay.createOrder()
        .setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.DE)
        	.addCustomerDetails(Item.individualCustomer());
               
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingBirthDateForDeOrder() {
        String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder() 
        	.addCustomerDetails(Item.individualCustomer()
	            .setName("Tess", "Testson")
	            .setStreetAddress("Gatan", "23")
	            .setZipCode("9999")
	            .setLocality("Stan"))
            .setCountryCode(COUNTRYCODE.DE)
        	.setValidator(new VoidValidator());
        assertEquals(orderValidator.validate(order), expectedMessage);
    }
    
    @Test
    public void testFailOnMissingVatNumberForIndividualOrderDE() {
        String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";

        CreateOrderBuilder order = WebPay.createOrder() 
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.DE)
        	.addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingVatNumberForCompanyOrderDE() {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use setVatNumber().\n";

        CreateOrderBuilder order = WebPay.createOrder()
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.DE)
            .addOrderRow(Item.orderRow()
            		.setAmountExVat(4)
            		.setVatPercent(25)
            		.setQuantity(1))
        	.addCustomerDetails(Item.companyCustomer()
        			.setCompanyName("K. H. Maier gmbH")
        			.setStreetAddress("Adalbertsteinweg", "1")
        			.setLocality("AACHEN")
        			.setZipCode("52070"))
        	.setOrderDate("2012-09-09")
        	.setValidator(new VoidValidator()); 
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingIdentityDataForNeOrder() throws ValidationException {
        String expectedMessage = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n"
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.NL).build() 
            .addCustomerDetails(Item.individualCustomer());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCompanyIdentityForNeOrder() throws ValidationException {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is NL. Use setVatNumber().\n"
        		+ "MISSING VALUE - Company name is required for individual customers when countrycode is NL. Use setName().\n"
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.NL).build() 
            .addCustomerDetails(Item.companyCustomer());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCompanyIdentityForDeOrder() throws ValidationException {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use setVatNumber().\n"
        		+ "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
        		+ "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
        		+ "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n"
        		+ "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.DE).build() 
            .addCustomerDetails(Item.companyCustomer());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    @Test
    public void testFailOnMissingInitialsForNLOrder() {
        String expectedMessage = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        CreateOrderBuilder order = WebPay.createOrder()
        	.addCustomerDetails(Item.individualCustomer()
        		.setBirthDate(1923, 12, 12)
        		.setName("Tess", "Testson")
        		.setStreetAddress("Gatan", "23")
        		.setZipCode("9999")
        		.setLocality("Stan"))
        		.setCountryCode(COUNTRYCODE.NL)
        	.setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testMissingCountryCodeGetPaymentPlanParams() throws Exception {
    	String expectedMsg = "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
    	try {
	        WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
	        		.doRequest();
	        //check that exception is thrown
	      	assertTrue(false);
	  	} catch (ValidationException e) {
	  		assertEquals(e.getMessage(), expectedMsg);
	  	}
    }
    
    @Test
    public void succeedOnGoodValuesSe() {
        CreateOrderBuilder order = WebPay.createOrder()
        	.addOrderRow(Item.orderRow()
	            .setAmountExVat(5.0)
	            .setVatPercent(25)
	            .setQuantity(1))
            .addCustomerDetails(Item.individualCustomer()
            		.setNationalIdNumber("194605092222"))
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-05-01")
            .setValidator(new VoidValidator());
        
        assertEquals("", orderValidator.validate(order));
    }
    
    @Test 
    public void testFailOnMissingOrderIdOnDeliverOrder() throws Exception {
        String expectedMessage = "MISSING VALUE - setOrderId is required.\n";
        HandleOrder handleOrder = WebPay.deliverOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .setNumberOfCreditDays(1)
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setCountryCode(COUNTRYCODE.SE)
        .deliverInvoiceOrder();
   
        assertEquals(expectedMessage, handleOrder.validateOrder());  
    }
    
    @Test
    public void testFailOnMissingOrderTypeForInvoiceOrder() throws ValidationException {
    	  String expectedMessage = "MISSING VALUE - setInvoiceDistributionType is requred for deliverInvoiceOrder.\n";
          HandleOrder handleOrder = WebPay.deliverOrder()
          .addOrderRow(Item.orderRow()
              .setArticleNumber("1")
              .setQuantity(2)
              .setAmountExVat(100.00)
              .setDescription("Specification")
              .setName("Prod")
              .setUnit("st")	
              .setVatPercent(25)
              .setDiscountPercent(0))
          .setNumberOfCreditDays(1)
          .setOrderId(2345L)
         // .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
          .setCountryCode(COUNTRYCODE.SE)
          .deliverInvoiceOrder();
     
          assertEquals(expectedMessage, handleOrder.validateOrder()); 
    }
     
    @Test
    public void testFailOnMissingRows() throws Exception {
    	  String expectedMessage = "MISSING VALUE - No order or fee has been included. Use addOrder(...) or addFee(...).\n";
    	  try {
	          WebPay.deliverOrder()
	          .setNumberOfCreditDays(1)
	          .setOrderId(2345L)
	          .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)     
	          .setCountryCode(COUNTRYCODE.SE)
	          .deliverInvoiceOrder()
	          .doRequest();
     
	        //check that exception is thrown
	      	assertTrue(false);
      	} catch (ValidationException e) {
      		assertEquals(e.getMessage(), expectedMessage);
      	}
    }
    
   @Test
    public void testFailCompanyCustomerUsingPaymentPlan() throws ValidationException, Exception {
    	String expectedMessage = "ERROR - CompanyCustomer is not allowed to use payment plan option.";
    	try {
    	WebPay.createOrder()
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
                .setNationalIdNumber("666666")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123.123")
                .setStreetAddress("Gatan", "23")
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan"))
        .setCountryCode(COUNTRYCODE.SE)
        .setOrderDate("2012-09-09")
       	.usePaymentPlanPayment("camp1");
    	
    	//check that exception is thrown
    	assertTrue(false);
    	
    	} catch (SveaWebPayException e) {
    		assertEquals(e.getMessage(), expectedMessage);
    	}
    }
}
