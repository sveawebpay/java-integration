package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Date;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebServiceOrderValidatorTest {

    private OrderValidator orderValidator;
    
    public WebServiceOrderValidatorTest() {
        orderValidator = new WebServiceOrderValidator();
    }
        
    @Test
    public void testFailOnCustomerIdentityIsNull() {
         String expectedMessage = "MISSING VALUE - CustomerIdentity must be set.\n"
                 + "MISSING VALUE - CountryCode is required. Use setCountryCode().\n"
                 + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                 + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
         CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setValidator(new VoidValidator())
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .addCustomerDetails(null);
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCountryCodeOnCreateOrder() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setValidator(new VoidValidator())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .addCustomerDetails(Item.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber));
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCountryCodeOnDeliverOrder() {
        String expectedMessage ="MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        try {
            WebPay.deliverOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .setNumberOfCreditDays(1)
                .setOrderId(2345L)
                .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
                .deliverInvoiceOrder()
                .doRequest();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }
    
    @Test
    public void testFailOnMissingValuesForGetAddresses() {
        String expectedMessage ="MISSING VALUE - CountryCode is required, use setCountryCode(...).\n"
                +"MISSING VALUE - either nationalNumber or companyId is required. Use: setCompany(...) or setIndividual(...).\n";
        try {
            WebPay.getAddresses(SveaConfig.getDefaultConfig())
                .doRequest();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }
    
    @Test
    public void testFailOnMissingCustomerIdentity() {
        String expectedMessage = "MISSING VALUE - National number(ssn) is required for individual customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addCustomerDetails(Item.individualCustomer())
            .setValidator(new VoidValidator())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCountryCode(TestingTool.DefaultTestCountryCode);
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingOrderRows() {
        String expectedMessage = "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setValidator(new VoidValidator())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber));
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingOrderRowValues() {
        String expectedMessage = "MISSING VALUE - Quantity is required in Item object. Use Item.setQuantity().\n" 
                + "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two of: setAmountExVat(), setAmountIncVat or setVatPercent().\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .addOrderRow(Item.orderRow())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingSsnForSeOrder() {
        String expectedMessage = "MISSING VALUE - National number(ssn) is required for individual customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addCustomerDetails(Item.individualCustomer())
            .setValidator(new VoidValidator())
            .setCountryCode(TestingTool.DefaultTestCountryCode);
        
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
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
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
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig()) 
            .addCustomerDetails(Item.individualCustomer()
                .setName("Tess", "Testson")
                .setStreetAddress("Gatan", "23")
                .setZipCode("9999")
                .setLocality("Stan"))
            .setCountryCode(COUNTRYCODE.DE)
            .setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
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
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCountryCode(COUNTRYCODE.DE)
            .addCustomerDetails(Item.individualCustomer())
            .setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingVatNumberForCompanyOrderDE() {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use setVatNumber().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCountryCode(COUNTRYCODE.DE)
            .addOrderRow(TestingTool.createMiniOrderRow())
            .addCustomerDetails(Item.companyCustomer()
                    .setCompanyName("K. H. Maier gmbH")
                    .setStreetAddress("Adalbertsteinweg", "1")
                    .setLocality("AACHEN")
                    .setZipCode("52070"))
            .setOrderDate(TestingTool.DefaultTestDate)
            .setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingIdentityDataForNeOrder() {
        String expectedMessage = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n"
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.NL).build() 
            .addCustomerDetails(Item.individualCustomer());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCompanyIdentityForNeOrder() {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is NL. Use setVatNumber().\n"
                + "MISSING VALUE - Company name is required for individual customers when countrycode is NL. Use setName().\n"
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.NL).build() 
            .addCustomerDetails(Item.companyCustomer());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCompanyIdentityForDeOrder() {
        String expectedMessage = "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use setVatNumber().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
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
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addCustomerDetails(Item.individualCustomer()
                .setBirthDate("19231212")
                .setName("Tess", "Testson")
                .setStreetAddress("Gatan", "23")
                .setZipCode("9999")
                .setLocality("Stan"))
                .setCountryCode(COUNTRYCODE.NL)
            .setValidator(new VoidValidator());
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testMissingCountryCodeGetPaymentPlanParams() {
        String expectedMsg = "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        
        try {
            WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                    .doRequest();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMsg, e.getCause().getMessage());
        }
    }
    
    
    @Test 
    public void testFailOnMissingOrderIdOnDeliverOrder() {
        String expectedMessage = "MISSING VALUE - setOrderId is required.\n";
        
        HandleOrder handleOrder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder();
        
        assertEquals(expectedMessage, handleOrder.validateOrder());
    }
    
    @Test
    public void testFailOnMissingOrderTypeForInvoiceOrder() {
        String expectedMessage = "MISSING VALUE - setInvoiceDistributionType is requred for deliverInvoiceOrder.\n";
        
        HandleOrder handleOrder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setNumberOfCreditDays(1)
            .setOrderId(2345L)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder();
        
        assertEquals(expectedMessage, handleOrder.validateOrder());
    }
         
    @Test
    public void testFailCompanyCustomerUsingPaymentPlan() {
        String expectedMessage = "ERROR - CompanyCustomer is not allowed to use payment plan option.";
        
        try {
            WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createPaymentPlanOrderRow())
                .addCustomerDetails(TestingTool.createCompanyCustomer())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
                .usePaymentPlanPayment("camp1");
            
            //Fail on no exception
            fail();
        } catch (SveaWebPayException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    
    @Test
	public void test_that_createOrder_with_mixed_orderRow_and_Fee_price_specifications_does_not_throw_validation_error() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("exvatRow")
		;
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(12.50)
			.setVatPercent(25)
			.setName("exvatInvoiceFee")
		;		
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(20.00)
			.setVatPercent(25)
			.setName("exvatShippingFee")
		;	
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(incvatRow);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);		

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		}
		catch (SveaWebPayException e){			
	        fail( "Unexpected SveaWebPayException thrown: "+e.getCause().getMessage() );		
		}		
	}
	
    
}
