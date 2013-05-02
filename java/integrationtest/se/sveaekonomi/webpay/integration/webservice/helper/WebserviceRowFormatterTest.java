package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebserviceRowFormatterTest {
    
    @Test
    public void testFormatOrderRows() {
    	CreateOrderBuilder order = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
            .setArticleNumber(0)
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1)
            .setUnit("st"));
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertTrue(4.0 == newRow.PricePerUnit);
        assertTrue(25.0 == newRow.VatPercent);
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
    
    @Test
    public void testFormatShippingFeeRows() throws ValidationException, Exception {
    	CreateOrderResponse response = WebPay.createOrder()
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
    	            .setArticleNumber(1)
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
    	         
    	                                 
    	        .setCountryCode(COUNTRYCODE.SE)
    	        .setOrderDate("2012-12-12")
    	        .setClientOrderNumber("33")
    	        .setCurrency(CURRENCY.SEK)
    	        .useInvoicePayment()// returns an InvoiceOrder object             
    	        .doRequest();
    	           
 /*   	CreateOrderBuilder order = WebPay.createOrder()
        .addOrderRow(Item.orderRow())
        .addFee(Item.shippingFee()  
            .setShippingId("0")
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setUnit("st"));        
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0,newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);*/
    }
    
    @Test
    public void testFormatInvoiceFeeRows() {
    	CreateOrderBuilder order = WebPay.createOrder()  
    		.addFee(Item.invoiceFee()       
    			.setDescription("Tester")
    			.setAmountExVat(4)
    			.setVatPercent(25)
    			.setUnit("st"));        
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);

        assertTrue("".equals(newRow.ArticleNumber));
        assertEquals("Tester",newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
    	CreateOrderBuilder order = WebPay.createOrder()
    		.addOrderRow(Item.orderRow()
    			.setAmountExVat(4)
    			.setVatPercent(25)
    			.setQuantity(1))
    		.addDiscount(Item.fixedDiscount()
    			.setDiscountId("0")
    			.setName("Tess")
    			.setDescription("Tester")
           // 	.setDiscount(1)
    			.setAmountIncVat(1)
    			.setUnit("st"));
         
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertEquals(-0.8,newRow.PricePerUnit, 0);
        assertTrue(25.0 == newRow.VatPercent);
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
    	CreateOrderBuilder order = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1))
        .addDiscount(Item.relativeDiscount()
            .setDiscountId("0")
            .setName("Tess")
            .setDescription("Tester")
            .setDiscountPercent(10)
            .setUnit("st"));
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertTrue(-0.4 == newRow.PricePerUnit);
        assertTrue(25 == newRow.VatPercent); //?
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
}
