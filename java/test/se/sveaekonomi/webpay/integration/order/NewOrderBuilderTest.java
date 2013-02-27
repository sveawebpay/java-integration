package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;


public class NewOrderBuilderTest {    
    
	@Test
	public void testBuildOrderRowArrayList() throws ValidationException {
		
		ArrayList<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>(); 
		orderRows.add(Item.orderRow()
				.setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
		
		orderRows.add(Item.orderRow()
				.setArticleNumber("2")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
		
		SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .setTestmode()
                
                .addOrderRows(orderRows)		
                .addCustomerDetails(Item.companyCustomer()
	                .setCompanyIdNumber("666666")
	                .setEmail("test@svea.com")
	                .setPhoneNumber(999999)
	                .setIpAddress("123.123.123.123")
	                .setStreetAddress("Gatan", 23)
	                .setCoAddress("c/o Eriksson")
	                .setZipCode("9999")
	                .setLocality("Stan"))
		
                .setCountryCode(COUNTRYCODE.SE)
                .setCustomerReference("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")                
                .useInvoicePayment()
                .prepareRequest();
		
		 assertEquals("666666", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
		 assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
		 assertEquals("2", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
	}
	
    @Test
    public void testBuildOrderWithCompanyCustomer() throws ValidationException {
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .setTestmode()
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
                .setCompanyIdNumber("666666")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123.123")
                .setStreetAddress("Gatan", 23)
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan"))
        
                .setCountryCode(COUNTRYCODE.SE)
                .setCustomerReference("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")                
                .useInvoicePayment()
                .prepareRequest();
        
        assertEquals("666666", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber); 
    }
    
   
}
