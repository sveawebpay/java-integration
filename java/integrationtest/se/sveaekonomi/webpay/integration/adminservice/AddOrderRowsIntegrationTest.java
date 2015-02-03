package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.AddOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class AddOrderRowsIntegrationTest {

	// handles errors
	@Test
    public void test_add_to_nonexistant_orderid_returns_error() {
    	
		AddOrderRowsBuilder builder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( "999999")              
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.99)	// 99.99inc = 80.637096 @24% => 80.64ex @24% = 99.9936 => 99.99inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse response = builder.addInvoiceOrderRows().doRequest();        
        assertFalse(response.isOrderAccepted());        
        assertEquals( "20004", response.getResultCode() );
        assertEquals( "Order does not exist.", response.getErrorMessage() );
	} 
	
	// invoice	
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvat_added_exvat_sent_as_exvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatPercent(24)
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}

	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_incvat_added_incvat_sent_as_incvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatPercent(24)
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	// different priceIncludingVat on orders
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvat_added_incvat_sent_as_exvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatPercent(24)
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_incvat_added_exvat_sent_as_incvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatPercent(24)
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	// orderRows specified exvat + incvat 
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvatincvat_added_incvatvatpercent_sent_as_incvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvatincvat_added_exvatvatpercent_sent_as_incvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatDiscount(0)
				)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvatvatpercent_added_exvatincvat_sent_as_exvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
			        .setQuantity(1.0)
			        .setUnit("st")
			        .setVatPercent(24)
			        .setVatDiscount(0)
			)
			.addCustomerDetails(WebPayItem.individualCustomer()
			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(TestingTool.DefaultTestDate)
		;		
		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setAmountIncVat(99.19)
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	
//	@Test
//	public void test_addOrderRows_addInvoiceOrderRows_original_incvat_update_incvat_sent_as_incvat() {
//
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.addInvoiceOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}	
//	
//	@Test
//	public void test_addOrderRows_addInvoiceOrderRows_original_exvat_update_incvat_sent_as_exvat() {
//
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.addInvoiceOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}
//	
//	@Test
//	public void test_addOrderRows_addInvoiceOrderRows_original_incvat_update_exvat_sent_as_incvat() {
//
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.addInvoiceOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}
//	
//	// payment plan
//	@Test
//	public void test_addOrderRows_updatePaymentPlanOrderRows_original_exvat_update_exvat_sent_as_exvat() {
//
//    	// get payment plan params
//		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
//				.setCountryCode(TestingTool.DefaultTestCountryCode)
//				.doRequest();
//		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
//				
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountExVat(999.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(999.99), Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.updatePaymentPlanOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}
//
//	@Test
//	public void test_addOrderRows_updatePaymentPlanOrderRows_original_incvat_update_incvat_sent_as_incvat() {
//
//    	// get payment plan params
//		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
//				.setCountryCode(TestingTool.DefaultTestCountryCode)
//				.doRequest();
//		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
//							
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountIncVat(1339.99)	// 999.99ex @24% = 1339.9876 => 1339.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(1339.99), Double.valueOf(orderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountIncVat(1339.99)	// 799.99ex @24% = 1339.1876 => 1339.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.updatePaymentPlanOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(1339.99), Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}	
//	
//	@Test
//	public void test_addOrderRows_updatePaymentPlanOrderRows_original_exvat_update_incvat_sent_as_exvat() {
//
//    	// get payment plan params
//		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
//				.setCountryCode(TestingTool.DefaultTestCountryCode)
//				.doRequest();
//		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
//			
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountExVat(999.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( Double.valueOf(999.99), Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountIncVat(1339.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.updatePaymentPlanOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}
//
//	@Test
//	public void test_addOrderRows_updatePaymentPlanOrderRows_original_incvat_update_exvat_sent_as_incvat() {
//
//    	// get payment plan params
//		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
//				.setCountryCode(TestingTool.DefaultTestCountryCode)
//				.doRequest();
//		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
//			
//		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addOrderRow( 
//				WebPayItem.orderRow()
//			        .setArticleNumber("original")
//			        .setName("Prod")
//			        .setDescription("Specification")
//			        .setAmountIncVat(1239.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
//			        .setQuantity(1.0)
//			        .setUnit("st")
//			        .setVatPercent(24)
//			        .setVatDiscount(0)
//				)
//			.addCustomerDetails(WebPayItem.individualCustomer()
//			    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//			)
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(TestingTool.DefaultTestDate)
//		;		
//		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
//		assertTrue(order.isOrderAccepted());		
//		
//		// query and assert original order row
//		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
//		assertEquals( 1, numberedOrderRows.size() );		
//		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(1239.99), Double.valueOf(orderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
//		assertEquals( 1, orderRow.getRowNumber() );	
//
//		// add order row
//		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
//		    .setOrderId( String.valueOf(order.orderId) ) 
//		    .setCountryCode( COUNTRYCODE.SE ) 
//		    .addOrderRows( 
//	    		WebPayItem.orderRow()
//	    			// OrderRow attributes:
//	                .setArticleNumber("credit")
//                    .setName("Prod")
//                    .setDescription("Specification")
//	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
//	                .setQuantity(1.0)
//	                .setUnit("st")
//	                .setVatPercent(24)
//	                .setVatDiscount(0)
//	                // NumberedOrderRow attributes:
//	                .setRowNumber(1)
//	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
//	                .setCreditInvoiceId("9999999")
//	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
//			)
//		;
//		AddOrderRowsResponse update = addBuilder.updatePaymentPlanOrderRows().doRequest();        
//        assertTrue(update.isOrderAccepted());        
//
//		// query and assert updated order row
//		QueryOrderBuilder queryaddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
//		        .setOrderId(order.orderId)
//		        .setCountryCode(COUNTRYCODE.SE)
//		;
//		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
//		assertTrue(queryOriginal.isOrderAccepted());			
//		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryUpdate.getNumberedOrderRows();
//		assertEquals( 1, addOrderRows.size() );		
//		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(0); 			
//		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
//		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
//		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
//		assertEquals( 1, updateOrderRow.getRowNumber() );			
//	}

}
