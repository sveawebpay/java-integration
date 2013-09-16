package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class InvoicePaymentTest {
    
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityIndividualFromSE() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    		.addOrderRow(TestingTool.createOrderRow())
            .addOrderRow(TestingTool.createOrderRow())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber("194609052222"))
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()
            //	returns an InvoicePayment object
            .prepareRequest();
        
        //CustomerIdentity
        assertEquals("194609052222", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
        assertEquals(COUNTRYCODE.SE, request.request.CreateOrderInformation.CustomerIdentity.CountryCode);
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType);    
    }
    
    @Test
    public void testInvoiceRequestObjectWithAuth() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    		.addOrderRow(TestingTool.createOrderRow())
                
            .addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
                
           .addCustomerDetails(Item.individualCustomer()
                .setInitials("SB")
                .setName("Tess", "Testson")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123")
                .setStreetAddress("Gatan", "23")
                .setBirthDate(1923, 12, 12)
                .setCoAddress("c/o Eriksson")
                .setNationalIdNumber("194605092222")
                .setZipCode("2222")
                .setLocality("Stan"))
                                    
            .setClientOrderNumber("nr26")
            .setCountryCode(COUNTRYCODE.SE)
           // .setAddressSelector("ad33")
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()// returns an InvoiceOrder object 
                .prepareRequest();
        
        assertEquals("sverigetest", request.request.Auth.Username);
        assertEquals("sverigetest", request.request.Auth.Password);
        assertEquals(79021, request.request.Auth.ClientNumber, 0);
    }
    
    @Test
    public void testSetAuth() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request =  WebPay.createOrder()
    	.addOrderRow(TestingTool.createOrderRow())
        .addOrderRow(TestingTool.createOrderRow())
        .addCustomerDetails(Item.individualCustomer()
    		.setNationalIdNumber("194605092222"))
        .setCountryCode(COUNTRYCODE.SE)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()
        //returns an InvoicePayment object
        .prepareRequest();
        
        assertEquals(79021, request.request.Auth.ClientNumber, 0);
        assertEquals("sverigetest", request.request.Auth.Username);
        assertEquals("sverigetest", request.request.Auth.Password);
    }
    
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityIndividualFromNL() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(TestingTool.createOrderRow())
            
        .addCustomerDetails(Item.individualCustomer()
           .setInitials("SB")
           .setBirthDate(1923, 12, 12)
           .setName("Tess", "Testson")
           .setEmail("test@svea.com")
           .setPhoneNumber(999999)
           .setIpAddress("123.123.123")
           .setStreetAddress("Gatan","23")
           .setCoAddress("c/o Eriksson")
           .setZipCode("9999")
           .setLocality("Stan"))
    
        .addOrderRow(TestingTool.createOrderRow())
	    .setCountryCode(COUNTRYCODE.NL)
	    .setOrderDate("2012-12-12")
	    .setClientOrderNumber("33")
	    .setCurrency(CURRENCY.SEK)
	    .useInvoicePayment()// returns an InvoiceOrder object
	    .prepareRequest();
         
        //CustomerIdentity
        assertEquals("test@svea.com", request.request.CreateOrderInformation.CustomerIdentity.Email); //Check all in identity
        assertEquals("999999", request.request.CreateOrderInformation.CustomerIdentity.PhoneNumber); //Check all in identity
        assertEquals("123.123.123", request.request.CreateOrderInformation.CustomerIdentity.IpAddress); //Check all in identity
        assertEquals("Tess Testson", request.request.CreateOrderInformation.CustomerIdentity.FullName); //Check all in identity
        assertEquals("Gatan", request.request.CreateOrderInformation.CustomerIdentity.Street); //Check all in identity
        assertEquals("c/o Eriksson", request.request.CreateOrderInformation.CustomerIdentity.CoAddress); //Check all in identity
        assertEquals("9999", request.request.CreateOrderInformation.CustomerIdentity.ZipCode); //Check all in identity
        assertEquals("23", request.request.CreateOrderInformation.CustomerIdentity.HouseNumber); //Check all in identity
        assertEquals("Stan", request.request.CreateOrderInformation.CustomerIdentity.Locality); //Check all in identity
        assertEquals(COUNTRYCODE.NL, request.request.CreateOrderInformation.CustomerIdentity.CountryCode); //Check all in identity
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType); //Check all in identity
        assertEquals("Tess", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.FirstName); //Check all in identity
        assertEquals("Testson", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.LastName); //Check all in identity
        assertEquals("SB", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.Initials); //Check all in identity
        assertEquals("19231212", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.BirthDate); //Check all in identity
    }
    
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityCompanyFromNL() throws ValidationException {
    	 SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(TestingTool.createOrderRow())
        
        .addCustomerDetails(Item.individualCustomer()
           .setInitials("SB")
           .setBirthDate(1923,  12,  12)
           .setName("Svea bakkerij", "123")
           .setEmail("test@svea.com")
           .setPhoneNumber(999999)
           .setIpAddress("123.123.123")
           .setStreetAddress("Gatan", "23")
           .setCoAddress("c/o Eriksson")
           .setZipCode("9999")
           .setLocality("Stan"))
         //  .setCompanyIdNumber("NL123456789A12")
          // .setVatNumber("NL123456789A12")
          // .setCompanyName("Svea bakkerij 123"));
        
        .addOrderRow(TestingTool.createOrderRow())
        .setCountryCode(COUNTRYCODE.NL)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(CURRENCY.SEK)           
        .useInvoicePayment()// returns an InvoiceOrder object
        .prepareRequest();
     
        //CustomerIdentity
        assertEquals("test@svea.com", request.request.CreateOrderInformation.CustomerIdentity.Email); //Check all in identity
        assertEquals("999999", request.request.CreateOrderInformation.CustomerIdentity.PhoneNumber); //Check all in identity
        assertEquals("123.123.123", request.request.CreateOrderInformation.CustomerIdentity.IpAddress); //Check all in identity
        assertEquals("Svea bakkerij 123", request.request.CreateOrderInformation.CustomerIdentity.FullName); //Check all in identity
        assertEquals("Gatan", request.request.CreateOrderInformation.CustomerIdentity.Street); //Check all in identity
        assertEquals("c/o Eriksson", request.request.CreateOrderInformation.CustomerIdentity.CoAddress); //Check all in identity
        assertEquals("9999", request.request.CreateOrderInformation.CustomerIdentity.ZipCode); //Check all in identity
        assertEquals("23", request.request.CreateOrderInformation.CustomerIdentity.HouseNumber); //Check all in identity
        assertEquals("Stan", request.request.CreateOrderInformation.CustomerIdentity.Locality); //Check all in identity
        assertEquals(COUNTRYCODE.NL, request.request.CreateOrderInformation.CustomerIdentity.CountryCode); //Check all in identity
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType); //Check all in identity
    }
        
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityCompanyFromSE() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            .addOrderRow(TestingTool.createOrderRow())
            .addOrderRow(TestingTool.createOrderRow())
            .addCustomerDetails(Item.companyCustomer()
                .setNationalIdNumber("vat234"))
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()// returns an InvoiceOrder object
            .prepareRequest();
        
        // CustomerIdentity
        assertEquals("vat234", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber); // Check all in identity
        assertEquals(COUNTRYCODE.SE, request.request.CreateOrderInformation.CustomerIdentity.CountryCode); // Check all in identity
        assertEquals("Company", request.request.CreateOrderInformation.CustomerIdentity.CustomerType); // Check all in identity
    }
    
    @Test
    public void testInvoiceRequestObjectForSEorderOnOneProductRow() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(TestingTool.createOrderRow())
            
         .addFee(Item.shippingFee()
             .setShippingId("33")
             .setName("shipping")
             .setDescription("Specification")
             .setAmountExVat(50)
             .setUnit("st")
             .setVatPercent(25)
             .setDiscountPercent(0))
         
         .addFee(Item.invoiceFee()
             .setName("Svea fee")
             .setDescription("Fee for invoice")
             .setAmountExVat(50)
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
            .setZipCode("2222")
            .setLocality("Stan"))
                             
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .useInvoicePayment()// returns an InvoiceOrder object
                .prepareRequest();
        
        /**
         * Test that all data is in the right place for SoapRequest
         */
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);        
    }
   
    @Test
    public void testInvoiceRequestObjectWithRelativeDiscountOnDifferentProductVat() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder() 
    	.addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(240.00)
            .setDescription("CD")
            .setVatPercent(25))

        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(188.68)
            .setDescription("Bok")
            .setVatPercent(6))
        
        .addDiscount(Item.relativeDiscount()
            .setDiscountId("1")
            .setDiscountPercent(20)
            .setDescription("RelativeDiscount"))
         
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
            .setZipCode("2222")
            .setLocality("Stan"))
                
        .setCountryCode(COUNTRYCODE.SE)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(CURRENCY.SEK)
                
        .useInvoicePayment()
        .prepareRequest();
 
        //coupon row
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("RelativeDiscount", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(-85.74, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
    //    assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(16.64, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent); 
    }
    
    @Test
    public void testInvoiceRequestObjectWithFixedDiscountOnDifferentProductVat() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(240.00)
            .setDescription("CD")
            .setVatPercent(25))
        
        .addOrderRow(Item.orderRow()
             .setArticleNumber("1")
             .setQuantity(1)
             .setAmountExVat(188.68)
             .setDescription("Bok")
             .setVatPercent(6))
        
        .addDiscount(Item.fixedDiscount()
             .setDiscountId("1")
             //.setDiscount(100.00)
             .setAmountIncVat(100.00)
             .setDescription("FixedDiscount"))
       
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
            .setZipCode("2222")
            .setLocality("Stan"))
        
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
         .setOrderDate("2012-12-12")
         .setClientOrderNumber("33")
         .setCurrency(CURRENCY.SEK)
         .useInvoicePayment()
         .prepareRequest();
        
         //coupon row
         assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
         assertEquals("FixedDiscount", request.request.CreateOrderInformation.OrderRows.get(2).Description);
         assertEquals(-85.74, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
         assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
  //       assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
         assertEquals(16.64, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
         assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
 
    public void testInvoiceRequestObjectWithCreateOrderInformation() throws ValidationException{
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(TestingTool.createOrderRow())
			 
        .addOrderRow(TestingTool.createOrderRow())
            
        .addFee(Item.shippingFee()
                 .setShippingId("33")
                 .setName("shipping")
                 .setDescription("Specification")
                 .setAmountExVat(50)
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
            .setZipCode("2222")
            .setLocality("Stan"))
                      
        .setCountryCode(COUNTRYCODE.SE) 
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()// returns an InvoiceOrder object
        .prepareRequest();
         
        //Test that all data is in the right place for SoapRequest 
        //First order row is a product
        assertEquals("2012-12-12", request.request.CreateOrderInformation.OrderDate);
        assertEquals("33", request.request.CreateOrderInformation.CustomerReference);
        assertEquals("Invoice", request.request.CreateOrderInformation.OrderType);
        assertEquals("nr26", request.request.CreateOrderInformation.ClientOrderNumber); //check in identity
        assertEquals("ad33", request.request.CreateOrderInformation.AddressSelector); //check in identity
    }
    
    @Test
    public void testInvoiceRequestUsingAmountIncVatWithVatPercent() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
  		.addCustomerDetails(Item.companyCustomer()
            .setNationalIdNumber("194605092222")
            .setAddressSelector("ad33")
            .setEmail("test@svea.com")
            .setPhoneNumber(999999)
            .setIpAddress("123.123.123")
            .setStreetAddress("Gatan", "23")
            .setCoAddress("c/o Eriksson")
            .setZipCode("2222")
            .setLocality("Stan"))
                
         .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setAmountIncVat(125)
            .setDiscountPercent(0))
            
        .addFee(Item.shippingFee()
            .setShippingId("33")
            .setName("shipping")
            .setDescription("Specification")
            .setAmountIncVat(62.50)
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .addFee(Item.invoiceFee()
            .setName("Svea fee")
            .setDescription("Fee for invoice")
            .setAmountIncVat(62.50)
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
                                 
        .setCountryCode(COUNTRYCODE.SE)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()// returns an InvoiceOrder object
        .prepareRequest();
           
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
    
    @Test
    public void testInvoiceRequestUsingAmountIncVatWithAmountExVat() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    		.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountIncVat(125)
                .setAmountExVat(100)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setDiscountPercent(0)) 
                
        .addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountIncVat(62.50)
                .setAmountExVat(50)
                .setUnit("st")
                .setDiscountPercent(0)) 
                
        .addFee(Item.invoiceFee()
                .setName("Svea fee")
                .setDescription("Fee for invoice")
                .setAmountIncVat(62.50)
                .setAmountExVat(50)
                .setUnit("st")
                .setDiscountPercent(0))
                
        .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber("194605092222"))
              
       .setCountryCode(COUNTRYCODE.SE)
       .setOrderDate("2012-12-12")
       .setCurrency(CURRENCY.SEK)
       .useInvoicePayment()// returns InvoiceOrder object
       .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
   
    @Test
    public void testInvoiceRequestXML() throws Exception {
        String expectedXML = "<web:request><web:Auth><web:ClientNumber>79021</web:ClientNumber><web:Username>sverigetest</web:Username><web:Password>sverigetest</web:Password></web:Auth><web:CreateOrderInformation><web:ClientOrderNumber>33</web:ClientOrderNumber><web:OrderRows><web:OrderRow><web:ArticleNumber>1</web:ArticleNumber><web:Description>Prod: Specification</web:Description><web:PricePerUnit>100.0</web:PricePerUnit><web:NumberOfUnits>2</web:NumberOfUnits><web:Unit>st</web:Unit><web:VatPercent>25.0</web:VatPercent><web:DiscountPercent>0</web:DiscountPercent></web:OrderRow><web:OrderRow><web:ArticleNumber>1</web:ArticleNumber><web:Description>Prod: Specification</web:Description><web:PricePerUnit>100.0</web:PricePerUnit><web:NumberOfUnits>2</web:NumberOfUnits><web:Unit>st</web:Unit><web:VatPercent>25.0</web:VatPercent><web:DiscountPercent>0</web:DiscountPercent></web:OrderRow></web:OrderRows><web:CustomerIdentity><web:NationalIdNumber>194605092222</web:NationalIdNumber><web:Email></web:Email><web:PhoneNumber></web:PhoneNumber><web:IpAddress></web:IpAddress><web:FullName></web:FullName><web:Street></web:Street><web:CoAddress></web:CoAddress><web:ZipCode></web:ZipCode><web:HouseNumber></web:HouseNumber><web:Locality></web:Locality><web:CountryCode>SE</web:CountryCode><web:CustomerType>Individual</web:CustomerType></web:CustomerIdentity><web:OrderDate>2012-12-12</web:OrderDate><web:AddressSelector></web:AddressSelector><web:CustomerReference>33</web:CustomerReference><web:OrderType>Invoice</web:OrderType></web:CreateOrderInformation></web:request>";
        String xml = WebPay.createOrder()
        .addOrderRow(TestingTool.createOrderRow())
        .addOrderRow(TestingTool.createOrderRow()) 
        
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber("194605092222"))
    
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .setCustomerReference("33")
            .useInvoicePayment()
            .getXML();
        
        assertEquals(expectedXML, xml);
    }

    @Test
    public void testCompanyIdRequest() throws ValidationException, Exception {
    	 SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            	.addOrderRow(TestingTool.createOrderRow())
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
}
