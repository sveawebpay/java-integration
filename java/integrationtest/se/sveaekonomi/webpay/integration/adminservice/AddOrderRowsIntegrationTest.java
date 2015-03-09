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
import se.sveaekonomi.webpay.integration.response.adminservice.AddOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.DeliverOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class AddOrderRowsIntegrationTest {

	// handles errors
	@Test
    public void test_add_to_nonexistant_orderid_returns_error() {
    	
		AddOrderRowsBuilder builder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( 999999L )              
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
	
	/// invoice	
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	        	.setOrderId(order.getOrderId())
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
	
	// test add rows to partially delivered order
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_to_partially_delivered_order() {
		
		// create an order using defaults	
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
	        .addOrderRow( WebPayItem.orderRow()
		        .setArticleNumber("first row")
		        .setName("Prod")
		        .setDescription("Specification")
		        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
		        .setQuantity(1.0)
		        .setUnit("st")
		        .setVatPercent(24)
		        .setVatDiscount(0)
			)
	        .addOrderRow( WebPayItem.orderRow()
	    		.setDescription("second row")
	    		.setQuantity(1.0)
	    		.setAmountIncVat(20.00)		// 16ex @25% = 20inc
	    		.setVatPercent(25)
			)    
			.addOrderRow( WebPayItem.orderRow()
	    		.setDescription("third row")
	    		.setQuantity(1.0)
	    		.setAmountIncVat(30.00)		// 24ex @25% = 30inc
	    		.setVatPercent(25)
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
		ArrayList<NumberedOrderRowBuilder> originalOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 3, originalOrderRows.size() );		

		// order tainted by exvat => all rows exvat?
		NumberedOrderRowBuilder orderRow = originalOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );			
		orderRow = originalOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(16.00), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 2, orderRow.getRowNumber() );			
		orderRow = originalOrderRows.get(2); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 3, orderRow.getRowNumber() );					
				
        // deliver first, second order rows and assert the response
        DeliverOrderRowsResponse deliver = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( order.getOrderId() )
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setRowToDeliver(1).setRowToDeliver(2)
            .deliverInvoiceOrderRows().doRequest();        
        assertTrue(deliver.isOrderAccepted());       

        // add two rows to order
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("first added")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("second added")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());   		
        
		// query and assert updated order rows
		QueryOrderBuilder queryAddedBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdded = queryAddedBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdded.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addedOrderRows = queryAdded.getNumberedOrderRows();
		assertEquals( 5, addedOrderRows.size() );		
	
		// original
		NumberedOrderRowBuilder addedOrderRow = addedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(addedOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(addedOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(addedOrderRow.getVatPercent()) );	
		assertEquals( 1, addedOrderRow.getRowNumber() );			
		addedOrderRow = addedOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(addedOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(16.00), Double.valueOf(addedOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(25.00), Double.valueOf(addedOrderRow.getVatPercent()) );	
		assertEquals( 2, addedOrderRow.getRowNumber() );			
		addedOrderRow = addedOrderRows.get(2); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(addedOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(addedOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(25.00), Double.valueOf(addedOrderRow.getVatPercent()) );	
		assertEquals( 3, addedOrderRow.getRowNumber() );			
		// new rows sent as incvat, but original order exvat => new rows sent rows exvat?
		addedOrderRow = addedOrderRows.get(3); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(addedOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(addedOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(addedOrderRow.getVatPercent()) );	
		assertEquals( 4, addedOrderRow.getRowNumber() );			
		addedOrderRow = addedOrderRows.get(4); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(addedOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(addedOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(addedOrderRow.getVatPercent()) );	
		assertEquals( 5, addedOrderRow.getRowNumber() );					
	}
	
	/// payment plan
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvat_added_exvat_sent_as_exvat() {

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
					
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1099.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}

	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_incvat_added_incvat_sent_as_incvat() {

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(1363.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1363.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(1339.19)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	// different priceIncludingVat on orders
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvat_added_incvat_sent_as_exvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1099.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(1339.19)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_incvat_added_exvat_sent_as_incvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(1363.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1363.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	// orderRows specified exvat + incvat 
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvatincvat_added_incvatvatpercent_sent_as_incvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
			        .setAmountIncVat(1363.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1363.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(1339.19)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvatincvat_added_exvatvatpercent_sent_as_incvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
			        .setAmountIncVat(1363.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1363.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvatvatpercent_added_exvatincvat_sent_as_exvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1099.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
	        	.setOrderId(order.getOrderId())
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setAmountIncVat(1339.19)
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
}
