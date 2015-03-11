package se.sveaekonomi.webpay.integration.webservice.handleorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class DeliverOrderTest {
    
    private DeliverOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.deliverOrder(SveaConfig.getDefaultConfig());
    }
    
    @Test
    public void testBuildRequest() {
        DeliverOrderBuilder request = order
            .setOrderId(54086L);
        
        assertEquals(Long.valueOf(54086L), request.getOrderId());
    }
    
    @Test
    public void testDeliverInvoice() {
        SveaRequest<SveaDeliverOrder> request = order.addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addFee(TestingTool.createExVatBasedShippingFee())
        .addDiscount(WebPayItem.fixedDiscount()
            .setAmountIncVat(10))
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setOrderId(54086L)
        .setNumberOfCreditDays(1)
        .setCreditInvoice("123456")
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .deliverInvoiceOrder()
        .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).NumberOfUnits, 0);
        assertEquals("st", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).Unit);
        assertEquals(25, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).DiscountPercent, 0);
        
        //Second order row is shipment
        assertEquals("33", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).Description);
        assertEquals(50, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).NumberOfUnits, 0);
        assertEquals("st", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).Unit);
        assertEquals(25, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).DiscountPercent, 0);
        
        //discount
        assertEquals(-8.0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit, 0);
        
        assertEquals(1, request.request.deliverOrderInformation.deliverInvoiceDetails.NumberofCreditDays);
        assertEquals("Post", request.request.deliverOrderInformation.deliverInvoiceDetails.InvoiceDistributionType);
        assertTrue(request.request.deliverOrderInformation.deliverInvoiceDetails.IsCreditInvoice);
        assertEquals("123456", request.request.deliverOrderInformation.deliverInvoiceDetails.InvoiceIdToCredit);
        assertEquals("54086", request.request.deliverOrderInformation.sveaOrderId);
        assertEquals("Invoice", request.request.deliverOrderInformation.orderType);
    }
    
//    @Test
//    public void testDeliverPaymentPlanOrder() {
//        SveaRequest<SveaDeliverOrder> request = order
//                .setOrderId(54086L)
//                .setCountryCode(TestingTool.DefaultTestCountryCode)
//                .deliverPaymentPlanOrder()
//                .prepareRequest();
//        
//        assertEquals("54086", request.request.deliverOrderInformation.sveaOrderId);
//        assertEquals("PaymentPlan", request.request.deliverOrderInformation.orderType);
//    }

    /// tests preparing order rows price specification
  	// invoice request	
  	@Test
  	public void test_orderRows_and_Fees_specified_exvat_and_vat_using_useInvoicePayment_are_prepared_as_exvat_and_vat() {
  		
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;				
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  			.setAmountExVat(80.00)
  			.setVatPercent(25)			
  			.setQuantity(1.0)
  			.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(80.00)
  			.setVatPercent(25)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(8.00)
  			.setVatPercent(25)
  			.setName("exvatInvoiceFee")
  		;		
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(16.00)
  			.setVatPercent(25)
  			.setName("exvatShippingFee")
  		;	
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  	
  		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		assertEquals( (Object)16.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		assertEquals( (Object)8.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );  				
  	}

  	/// relative discount examples:        
  	@Test
  	public void test_relativeDiscount_with_single_vat_rates_order_sent_with_PriceIncludingVat_false() {
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;				
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  				.setAmountExVat(80.00)
  				.setVatPercent(25)			
  				.setQuantity(1.0)
  				.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(80.00)
  			.setVatPercent(25)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(8.00)
  			.setVatPercent(25)
  			.setName("exvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(16.00)
  			.setVatPercent(25)
  			.setName("exvatShippingFee")
  		;	

  		// expected: 10% off orderRow rows: 2x 80.00 @25% => -16.00 @25% discount
  		RelativeDiscountBuilder relativeDiscount = WebPayItem.relativeDiscount()
  			.setDiscountPercent(10.0)
  			.setDiscountId("TenPercentOff")
  			.setName("relativeDiscount")
  		;
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(relativeDiscount);		

  		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
  		// all order rows
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)16.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );  				
 		// all discount rows
  		// expected: 10% off orderRow rows: 2x 80.00 @25% => -16.00 @25% discount
  		assertEquals( (Object)(-16.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)25.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  	}    
      
  	//if mixed specification types, send order as exvat if at least one exvat + vat found 	
  	@Test
  	public void test_relativeDiscount_with_multiple_vat_rates_order_sent_with_PriceIncludingVat_false() {
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;					
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  				.setAmountExVat(60.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(30.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(8.00)
  			.setVatPercent(10)
  			.setName("exvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(16.00)
  			.setVatPercent(10)
  			.setName("exvatShippingFee")
  		;	
  	
  		RelativeDiscountBuilder relativeDiscount = WebPayItem.relativeDiscount()
  			.setDiscountPercent(10.0)
  			.setDiscountId("TenPercentOff")
  			.setName("relativeDiscount")
  		;
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(relativeDiscount);		
  	  		
		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)60.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)30.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)16.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
          // all discount rows
          // expected: 10% off orderRow rows: 1x60.00 @20%, 1x30@10% => split proportionally across order row (only) vat rate: -6.0 @20%, -3.0 @10%
  		assertEquals( (Object)(-6.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-3.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );
  	}	
  	
  	@Test
  	public void test_relativeDiscount_with_multiple_vat_rates_order_sent_with_PriceIncludingVat_true() {
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;			
  		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(72.00)
			.setVatPercent(20)			
			.setQuantity(1.0)
			.setName("incvatRow")
  		;
  		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
  			.setAmountIncVat(33.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("incvatRow2")
  		;		
  		
  		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountIncVat(8.80)
  			.setVatPercent(10)
  			.setName("incvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
  			.setAmountIncVat(17.60)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		RelativeDiscountBuilder relativeDiscount = WebPayItem.relativeDiscount()
  			.setDiscountPercent(10.0)
  			.setDiscountId("TenPercentOff")
  			.setName("relativeDiscount")
  		;
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(relativeDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)72.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)33.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)17.6, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.8, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: 10% off orderRow rows: 1x60.00 @20%, 1x30@10% => split proportionally across order row (only) vat rate: -6.0 @20%, -3.0 @10%
  		assertEquals( (Object)(-7.20), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-3.30), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );
  	}
  			  		
    // fixed discount examples:        
  	@Test
  	public void test_fixedDiscount_with_amount_specified_as_exvat_and_given_vat_rate_order_sent_with_PriceIncludingVat_false() {
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;					
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  				.setAmountExVat(60.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(30.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(8.00)
  			.setVatPercent(10)
  			.setName("exvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(16.00)
  			.setVatPercent(10)
  			.setName("exvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  				.setAmountExVat(10.0)
  				.setVatPercent(10.0)
  				.setDiscountId("ElevenCrownsOff")
  				.setName("fixedDiscount: 10 @10% => 11kr")
  			;   
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)60.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)30.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)16.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
          // all discount rows
          // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
  		assertEquals( (Object)(-10.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  	}	
  	
  	@Test
  	public void test_fixedDiscount_with_amount_specified_as_exvat_and_given_vat_rate_order_sent_with_PriceIncludingVat_true() {
  		long orderId = 9876543L; // fake
  		
  		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(orderId)
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
  		;			
  		OrderRowBuilder incvatRow = WebPayItem.orderRow()
  				.setAmountIncVat(72.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("incvatRow")
  		;
  		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
  			.setAmountIncVat(33.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("incvatRow2")
  		;		
  		
  		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountIncVat(8.80)
  			.setVatPercent(10)
  			.setName("incvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
  			.setAmountIncVat(17.60)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  				.setAmountExVat(10.0)
  				.setVatPercent(10.0)
  				.setDiscountId("ElevenCrownsOff")
  				.setName("fixedDiscount: 10 @10% => 11kr")
  			;   
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)72.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)33.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)17.6, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.8, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
  		assertEquals( (Object)(-11.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  	}	

  	@Test
  	public void test_fixedDiscount_with_amount_specified_as_incvat_and_given_vat_rate_order_sent_with_PriceIncludingVat_false() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
				.setOrderId(orderId)
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
		;			
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  				.setAmountExVat(60.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(30.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(8.00)
  			.setVatPercent(10)
  			.setName("exvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(16.00)
  			.setVatPercent(10)
  			.setName("exvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
			.setAmountIncVat(11.0)
			.setVatPercent(10.0)
			.setDiscountId("ElevenCrownsOff")
			.setName("fixedDiscount: 10 @10% => 11kr")
		;   
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)60.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)30.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)16.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
  		assertEquals( (Object)(-10.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  	}	

  	@Test
  	public void test_fixedDiscount_with_amount_specified_as_incvat_and_given_vat_rate_order_sent_with_PriceIncludingVat_true() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
		.setOrderId(orderId)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
		;			
  		OrderRowBuilder incvatRow = WebPayItem.orderRow()
  				.setAmountIncVat(72.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("incvatRow")
  		;
  		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
  			.setAmountIncVat(33.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("incvatRow2")
  		;		
  		
  		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountIncVat(8.80)
  			.setVatPercent(10)
  			.setName("incvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
  			.setAmountIncVat(17.60)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  				.setAmountIncVat(11.0)
  				.setVatPercent(10.0)
  				.setDiscountId("ElevenCrownsOff")
  				.setName("fixedDiscount: 10 @10% => 11kr")
  			;     
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)72.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)33.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)17.6, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)8.8, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as incvat + vat in request
  		assertEquals( (Object)(-11.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  	}	
  	
  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_exvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
		.setOrderId(orderId)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
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
  			.setAmountExVat(10.0)
  			.setDiscountId("TenCrownsOffExVat")
  			.setName("fixedDiscount: 10 off exvat")	
  		;   
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)600.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)300.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)160.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 off exvat, order row amount are 66% at 20% vat, 33% at 10% vat => 6.67 @20% and 3.33 @10% 
  		assertEquals( (Object)(-6.67), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-3.33), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );
  	}	

  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_exvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
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
  			.setAmountIncVat(172.00)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  			.setAmountExVat(10.0)
  			.setDiscountId("TenCrownsOffExVat")
  			.setName("fixedDiscount: 10 off exvat")	
  		;   
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)720.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)330.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)172.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)88.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 off exvat, order row amount are 66% at 20% vat, 33% at 10% vat => 6.67ex @20% = 8.00 inc and 3.33ex @10% = 3.66inc 
  		assertEquals( (Object)(-8.00), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-3.66), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );
  	}		
  	
  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
		.setOrderId(orderId)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
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

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)600.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)300.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)160.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)80.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
  	    // all discount rows
        // expected: fixedDiscount: 10 off incvat, order row amount are 66% at 20% vat, 33% at 10% vat  
        // 1.2*0.66x + 1.1*0.33x = 10 => x = 8.6580 => 5.7143 @20% and 2.8571 @10% = 10kr
  		assertEquals( (Object)(-5.71), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-2.86), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( false, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );	
  	}		

  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true() {
		long orderId = 9876543L; // fake
  		
		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
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
  			.setAmountIncVat(172.00)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  			.setAmountIncVat(10.0)
  			.setDiscountId("TenCrownsOff")
  			.setName("fixedDiscount: 10 off incvat")
  		;     
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaDeliverOrder> soapRequest = order.deliverInvoiceOrder().prepareRequest();
		// all order rows
  		assertEquals( (Object)720.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PriceIncludingVat );
  		assertEquals( (Object)330.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PriceIncludingVat );
  		// all shipping fee rows
  		assertEquals( (Object)172.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PriceIncludingVat );
  		// all invoice fee rows		
  		assertEquals( (Object)88.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(3).PriceIncludingVat );
  	    // all discount rows
        // expected: fixedDiscount: 10 off incvat, order row amount are 66% at 20% vat, 33% at 10% vat  
        // 1.2*0.66x + 1.1*0.33x = 10 => x = 8.6580 => 5.7143 @20% and 2.8571 @10% = 10kr
  		assertEquals( (Object)(-6.86), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)20.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(4).PriceIncludingVat );
  		assertEquals( (Object)(-3.14), (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
  		assertEquals( (Object)10.0, (Object)soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
  		assertEquals( true, soapRequest.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(5).PriceIncludingVat );	
  	}		
  	
  	// validation
	// .deliverPaymentPlanOrder() with orderrows fails if try to deliver part of order  // TODO => validation error + other validation tests
//    public void test_deliverOrder_deliverPaymentPlanOrder_with_orderrows() {	
//    	
//    	// create an order using defaults
//    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_deliverOrder_deliverPaymentPlanOrder_with_orderrows_passes_iff_all_order_rows_match");
//        assertTrue(order.isOrderAccepted());
//    	
//		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
//            //.addOrderRow(TestingTool.createPaymentPlanOrderRow("1"))			// TODO should validate this is not set!
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderId( order.orderId )
//			//.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )				// TODO should validate this is not set!			
//		;
//			
//		HandleOrder request = builder.deliverPaymentPlanOrder();
//		assertTrue( request instanceof Requestable );        
//		assertThat( request, instanceOf(HandleOrder.class) );
//		
//		DeliverOrderResponse response = request.doRequest();
//		assertThat( response, instanceOf(DeliverOrderResponse.class) );
//		assertEquals(true, response.isOrderAccepted());
//    }   
  	
  	
  	
}
