package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;


public class CreateOrderResponseTest {
            
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
    	assertEquals(25.52,response.amount, 0);
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
    	assertEquals(51.03,response.amount, 0);
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
}
