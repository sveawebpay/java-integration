package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverInvoiceOrderTest {
    
    @Test
    public void testDeliverInvoiceOrderDoRequest() {
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(54086L)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        response.getErrorMessage();
    }
    
    @Test
    public void testDeliverInvoiceOrderResult() {
        long orderId = createInvoiceAndReturnOrderId();
        
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(orderId)
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals(DISTRIBUTIONTYPE.Post, response.getInvoiceDistributionType());
        assertNotNull(response.getOcr());
        assertTrue(0 < response.getOcr().length());
        assertEquals(0.0, response.getLowestAmountToPay(), 0);
    }
    
    private long createInvoiceAndReturnOrderId() {
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(WebPayItem.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .doRequest();
        
        return response.orderId;
    }


	/// tests for sending orderRows to webservice, specified as exvat + vat in soap request
	@Test
	public void test_deliverOrder_sent_with_PriceIncludingVat_false() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
				.setAmountExVat(600.00)
				.setVatPercent(20)			
				.setQuantity(1.0)
				.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(300.00)
			.setVatPercent(10)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountExVat(80.00)
			.setVatPercent(10)
			.setName("exvatInvoiceFee")
		;
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(160.00)
			.setVatPercent(10)
			.setName("exvatShippingFee")
		;	
	
		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;     
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);
		order.addDiscount(fixedDiscount);		
		
		CreateOrderResponse response = order.useInvoicePayment().doRequest();

		assertTrue( response.isOrderAccepted() );

  		long orderId = response.orderId;
  		
  		DeliverOrderBuilder deliverOrder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;	
		
  		deliverOrder.addOrderRow(exvatRow);
		deliverOrder.addOrderRow(exvatRow2);
		deliverOrder.addFee(exvatInvoiceFee);
		deliverOrder.addFee(exvatShippingFee);
		deliverOrder.addDiscount(fixedDiscount);		

		DeliverOrderResponse deliverResponse = deliverOrder.deliverInvoiceOrder().doRequest();

		assertTrue( deliverResponse.isOrderAccepted() );
		assertEquals( (Object)1304.00, deliverResponse.getAmount() );		
		//System.out.println( "test_deliverOrder_sent_with_PriceIncludingVat_false\n"
		//		+ "  Check logs that order rows were sent as incvat+vat for orderId/invoiceId #"+response.orderId+"/"+deliverResponse.getInvoiceId());		
					
		// Expected log:
		// ...
	    //<web:OrderRows>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>exvatRow</web:Description>
        //   <web:PricePerUnit>600.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>20.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>exvatRow2</web:Description>
        //   <web:PricePerUnit>300.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>exvatShippingFee</web:Description>
        //   <web:PricePerUnit>160.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>exvatInvoiceFee</web:Description>
        //   <web:PricePerUnit>80.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>TenCrownsOff</web:ArticleNumber>
        //   <web:Description>fixedDiscount: 10 off incvat (20%)</web:Description>
        //   <web:PricePerUnit>-5.71</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>20.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>TenCrownsOff</web:ArticleNumber>
        //   <web:Description>fixedDiscount: 10 off incvat (10%)</web:Description>
        //   <web:PricePerUnit>-2.86</web:PricePerUnit>
        //   <web:PriceIncludingVat>false</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        //</web:OrderRows>
	    // ...	
	
	}		

	@Test
	public void test_deliverOrder_sent_with_PriceIncludingVat_true_which_does_not_match_CreateOrder_PriceIncludingVat_false_is_redelivered_true() {

		// create order is sent with PriceIncludingVat = false
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(600.00)
			.setVatPercent(20)			
			.setQuantity(1.0)
			.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(300.00)
			.setVatPercent(10)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountExVat(80.00)
			.setVatPercent(10)
			.setName("exvatInvoiceFee")
		;
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(160.00)
			.setVatPercent(10)
			.setName("exvatShippingFee")
		;	
	
		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;     
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);
		order.addDiscount(fixedDiscount);		
		
		double expectedCreateOrderAmount = 1304.00;				
		
		CreateOrderResponse response = order.useInvoicePayment().doRequest();		
		//System.out.println("created order #"+response.orderId+" with total order amount "+response.amount);

		assertTrue( response.isOrderAccepted() );
		assertEquals( (Object)expectedCreateOrderAmount, response.amount );
		
  		long orderId = response.orderId;
		
		// deliver order is sent with PriceIncludingVat = true		
  		DeliverOrderBuilder deliverOrder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;	
		
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(720.00)
			.setVatPercent(20)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(330.00)
			.setVatPercent(10)			
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		
		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(88.00)
			.setVatPercent(10)
			.setName("incvatInvoiceFee")
		;
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(176.00)
			.setVatPercent(10)
			.setName("incvatShippingFee")
		;	
		
		deliverOrder.addOrderRow(incvatRow);
		deliverOrder.addOrderRow(incvatRow2);
		deliverOrder.addFee(incvatInvoiceFee);
		deliverOrder.addFee(incvatShippingFee);
		deliverOrder.addDiscount(fixedDiscount);		

		DeliverOrderResponse deliverResponse = deliverOrder.deliverInvoiceOrder().doRequest();
		//System.out.println("(re)delivered order #"+response.orderId+" with total order amount "+deliverResponse.getAmount());

		//System.out.println(deliverResponse.getResultCode());
		//System.out.println(deliverResponse.getErrorMessage());		
		assertEquals( true, deliverResponse.isOrderAccepted() );
		assertEquals( (Object)expectedCreateOrderAmount, deliverResponse.getAmount() );	
	}		
	
	@Test
	public void test_deliverOrder_sent_with_PriceIncludingVat_false_which_does_not_match_CreateOrder_PriceIncludingVat_true_is_redelivered_false() {

		// create order is sent with PriceIncludingVat = false
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(600.00)
			.setVatPercent(20)			
			.setQuantity(1.0)
			.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(300.00)
			.setVatPercent(10)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountExVat(80.00)
			.setVatPercent(10)
			.setName("exvatInvoiceFee")
		;
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(160.00)
			.setVatPercent(10)
			.setName("exvatShippingFee")
		;	
	
		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;     
		
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(720.00)
			.setVatPercent(20)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(330.00)
			.setVatPercent(10)			
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		
		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(88.00)
			.setVatPercent(10)
			.setName("incvatInvoiceFee")
		;
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(176.00)
			.setVatPercent(10)
			.setName("incvatShippingFee")
		;	
	
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);
		order.addDiscount(fixedDiscount); // same, ok		
		
		double expectedCreateOrderAmount = 1304.00;				
						
		CreateOrderResponse response = order.useInvoicePayment().doRequest();
		//System.out.println("created order #"+response.orderId+" with total order amount "+response.amount);
		
		assertTrue( response.isOrderAccepted() );

  		long orderId = response.orderId;
		
		// deliver order is sent with PriceIncludingVat = true		
  		DeliverOrderBuilder deliverOrder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;	
		
		deliverOrder.addOrderRow(exvatRow);
		deliverOrder.addOrderRow(exvatRow2);
		deliverOrder.addFee(exvatInvoiceFee);
		deliverOrder.addFee(exvatShippingFee);
		deliverOrder.addDiscount(fixedDiscount);	
		
		DeliverOrderResponse deliverResponse = deliverOrder.deliverInvoiceOrder().doRequest();
		//System.out.println("(re)delivered order #"+response.orderId+" with total order amount "+deliverResponse.getAmount());

		assertEquals( true, deliverResponse.isOrderAccepted() );
		assertEquals( (Object)expectedCreateOrderAmount, deliverResponse.getAmount() );		
	}			
}
