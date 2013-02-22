package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


public class CreateOrderResponseTest {
    
private CreateOrderBuilder orderBuilder;
    
    @Before
    public void setUp() {
        orderBuilder = new CreateOrderBuilder();
    }
        
    @Test
    public void testInvoiceRequestForCustomerIdentityIndividualFromSE() throws Exception {
        this.orderBuilder.setTestmode();
        orderBuilder.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setVatPercent(25)
                .setDiscountPercent(0));              
        orderBuilder.addCustomerDetails(Item.individualCustomer().setSsn(194605092222L));
        CreateOrderResponse response = orderBuilder
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency("SEK")
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
    public void testInvoiceCompanySe() throws Exception {
        this.orderBuilder
                .setTestmode()
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setCurrency("SEK");            
        orderBuilder.addCustomerDetails(Item.companyCustomer().setCompanyIdNumber("4608142222"));               
        orderBuilder.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setVatPercent(25)
                .setDiscountPercent(0));                
       CreateOrderResponse response = orderBuilder.useInvoicePayment()
                .doRequest();

        assertEquals(true, response.isOrderAccepted());
        assertEquals(true, response.sveaWillBuyOrder);
        //assertEquals("4608142222", response.customerIdentity.nationalIdNumber);
        assertEquals("SE", response.customerIdentity.getCountryCode());
    }
    
    @Test
    public void testInvoiceRequestObjectForCustomerIdentityIndividualFromNL() throws Exception {

        this.orderBuilder.setTestmode();
        orderBuilder.addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0));
        
        orderBuilder.addCustomerDetails(Item.individualCustomer()
           .setBirthDate(1955, 03, 07)
           .setInitials("SB")
           .setName("Sneider", "Boasman")
           .setStreetAddress("Gate",42)
           .setLocality("BARENDRECHT")               
           .setZipCode("1102 HG")           
           .setCoAddress("138"));
           
           CreateOrderResponse response = orderBuilder.setCountryCode(COUNTRYCODE.NL)           
           .setClientOrderNumber("33")
           .setOrderDate("2012-12-12")
           .setCurrency("EUR")
           .useInvoicePayment()// returns an InvoiceOrder object
           .setPasswordBasedAuthorization("hollandtest", "hollandtest", 85997)             
               .doRequest();  
        
          assertEquals(true, response.isOrderAccepted());
          assertEquals(true, response.sveaWillBuyOrder);
          assertEquals(250.00, response.amount, 0);
          assertEquals(0, response.getResultCode());
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
