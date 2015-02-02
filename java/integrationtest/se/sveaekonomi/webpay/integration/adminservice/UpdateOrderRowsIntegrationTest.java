package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class UpdateOrderRowsIntegrationTest {

	// handles errors
	@Test
    public void test_update_nonexistant_orderid_returns_error() {
    	
		UpdateOrderRowsBuilder builder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( "999999")              
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.99)	// 99.99inc = 80.637096 @24% => 80.64ex @24% = 99.9936 => 99.99inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse response = builder.updateInvoiceOrderRows().doRequest();        
        assertFalse(response.isOrderAccepted());        
        assertEquals( "20004", response.getResultCode() );
        assertEquals( "Order does not exist.", response.getErrorMessage() );
	} 
	
	// invoice
	@Test
	public void test_updateOrderRows_updateInvoiceOrderRows_original_exvat_update_exvat_sent_as_exvat() {

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

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updateInvoiceOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}

	@Test
	public void test_updateOrderRows_updateInvoiceOrderRows_original_incvat_update_incvat_sent_as_incvat() {

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

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updateInvoiceOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_updateOrderRows_updateInvoiceOrderRows_original_exvat_update_incvat_sent_as_exvat() {

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
		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updateInvoiceOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}
	
	@Test
	public void test_updateOrderRows_updateInvoiceOrderRows_original_incvat_update_exvat_sent_as_incvat() {

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

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updateInvoiceOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}
	
	// payment plan
	@Test
	public void test_updateOrderRows_updatePaymentPlanOrderRows_original_exvat_update_exvat_sent_as_exvat() {

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
			        .setAmountExVat(999.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
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
		assertEquals( Double.valueOf(999.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updatePaymentPlanOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}

	@Test
	public void test_updateOrderRows_updatePaymentPlanOrderRows_original_incvat_update_incvat_sent_as_incvat() {

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
			        .setAmountIncVat(1339.99)	// 999.99ex @24% = 1339.9876 => 1339.99inc
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
		assertEquals( Double.valueOf(1339.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(1339.99)	// 799.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updatePaymentPlanOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.99), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}	
	
	@Test
	public void test_updateOrderRows_updatePaymentPlanOrderRows_original_exvat_update_incvat_sent_as_exvat() {

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
			        .setAmountExVat(999.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
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
		assertEquals( Double.valueOf(999.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(1339.19)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updatePaymentPlanOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}

	@Test
	public void test_updateOrderRows_updatePaymentPlanOrderRows_original_incvat_update_exvat_sent_as_incvat() {

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
			        .setAmountIncVat(1239.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
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
		assertEquals( Double.valueOf(1239.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updatePaymentPlanOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(1339.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}

}
