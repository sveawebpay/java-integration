package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.TestingTool;
import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
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
            .setArticleNumber("0")
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
    	  SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    		        .addOrderRow(TestingTool.createOrderRow())
    		       
    		       .addFee(Item.shippingFee()  
			            .setShippingId("0")
			            .setName("Tess")
			            .setDescription("Tester")
			            .setAmountExVat(4)
			            .setVatPercent(25)
			            .setUnit("st"))
    		        
    		        .addCustomerDetails(Item.individualCustomer()
    		            .setNationalIdNumber("194605092222"))
    		    
    		            .setCountryCode(COUNTRYCODE.SE)
    		            .setOrderDate("2012-12-12")
    		            .setClientOrderNumber("33")
    		            .setCurrency(CURRENCY.SEK)
    		            .setCustomerReference("33")
    		            .useInvoicePayment()
    		        .prepareRequest();
    	  
    	assertEquals("0", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("Tess: Tester", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(4.0, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(25.0, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
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
