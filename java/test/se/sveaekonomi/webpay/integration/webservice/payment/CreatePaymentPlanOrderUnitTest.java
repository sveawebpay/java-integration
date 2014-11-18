package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.ConfigurationProviderTestData;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CreatePaymentPlanOrderUnitTest {

	/// tests for, preparing order rows and fees price specification
	// paymentplan request	
	@Test
	public void test_orderRows_and_Fees_specified_exvat_and_vat_using_usePaymentPlanPayment_are_prepared_as_exvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(800.00)
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		// all shipping fee rows
		// all invoice fee rows		
		assertEquals( (Object)800.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
				
	}

	@Test
	public void test_orderRows_and_Fees_specified_incvat_and_vat_using_usePaymentPlanPayment_are_prepared_as_incvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(1000.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		
		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(10.00)
			.setVatPercent(25)
			.setName("incvatInvoiceFee")
		;		
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(20.00)
			.setVatPercent(25)
			.setName("incvatShippingFee")
		;	
		
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);
		
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();
		
		assertEquals( (Object)1000.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)100.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );

	}

	@Test
	public void test_orderRows_and_Fees_specified_incvat_and_exvat_using_usePaymentPlanPayment_are_prepared_as_incvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(1000.00)
			.setAmountExVat(800.00)
			.setQuantity(1.0)
			.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(100.00)
			.setAmountExVat(80.00)		
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		
		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(10.00)
			.setAmountExVat(8.00)
			.setName("incvatInvoiceFee")
		;		
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(20.00)
			.setAmountExVat(16.00)
			.setName("incvatShippingFee")
		;		

		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);
				
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		assertEquals( (Object)1000.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)100.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
	}
	
	//if no mixed specification types, default to sending order as incvat
	@Test
	public void test_that_createOrder_request_is_sent_as_incvat_iff_no_exvat_specified_anywhere_in_order() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
				.setAmountIncVat(720.00)
				.setVatPercent(20)			
				.setQuantity(1.0)
				.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(330.00)
			.setAmountExVat(300.00)
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		
		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(88.0)
			.setVatPercent(10)
			.setName("incvatInvoiceFee")
		;
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(176.0)
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();
		
		// all order rows
		assertEquals( (Object)720.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)330.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)176.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)88.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
	    // all discount rows
        // expected: fixedDiscount: 10 off incvat, order row amount are 66% at 20% vat, 33% at 10% vat  
        // 1.2*0.66x + 1.1*0.33x = 10 => x = 8.6580 => 5.7143ex @20% and 2.8571ex @10% => 6.86inc @20%, 3.14inc @10% 
		assertEquals( (Object)(-6.86), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
		assertEquals( (Object)(-3.14), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat );
	}
		
	//if mixed specification types, send order as exvat if at least one exvat + vat found
	@Test
	public void test_that_createOrder_request_is_sent_as_exvat_if_exvat_specified_anywhere_in_order() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
				.setAmountExVat(600.00)
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
			.setAmountIncVat(88.0)
			.setVatPercent(10)
			.setName("incvatInvoiceFee")
		;
		
		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
			.setAmountIncVat(176.0)
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();
		
		// all order rows
		assertEquals( (Object)600.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)300.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.00, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.00, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
	    // all discount rows
        // expected: fixedDiscount: 10 off incvat, order row amount are 66% at 20% vat, 33% at 10% vat  
        // 1.2*0.66x + 1.1*0.33x = 10 => x = 8.6580 => 5.7143ex @20% and 2.8571ex @10% = 
		assertEquals( (Object)(-5.71), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
		assertEquals( (Object)(-2.86), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat );
		// order total should be (72+33+17.6+8.8)-10 = 121.40, see integration test
	}
		
	/// relative discount examples:        
    // single order rows vat rate 
	@Test
	public void test_relativeDiscount_amount() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
				.setAmountExVat(800.00)
				.setVatPercent(25)			
				.setQuantity(1.0)
				.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(800.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountExVat(80.00)
			.setVatPercent(25)
			.setName("exvatInvoiceFee")
		;
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(160.00)
			.setVatPercent(25)
			.setName("exvatShippingFee")
		;	

		// expected: 10% off orderRow rows: 2x 800.00 @25% => -160.00 @25% discount
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		assertEquals( (Object)800.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)800.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
		// all discount rows
		// expected: 10% off orderRow rows: 2x 80.00 @25% => -16.00 @25% discount
		assertEquals( (Object)(-160.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
	}    
    
    // multiple order row vat rates
	@Test
	public void test_relativeDiscount_amount_multiple_vat_rates_in_order() {
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
		
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		assertEquals( (Object)600.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)300.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: 10% off orderRow rows: 1x60.00 @20%, 1x30@10% => split proportionally across order row (only) vat rate: -6.0 @20%, -3.0 @10%
		assertEquals( (Object)(-60.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
		assertEquals( (Object)(-30.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat );
	}	
	
    /// fixed discount examples:        
	@Test
	public void test_fixedDiscount_amount_with_set_exvat_vat_rate() {
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		assertEquals( (Object)600.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)300.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
		assertEquals( (Object)(-10.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
	}	
	
	@Test
	public void test_fixedDiscount_amount_with_set_incvat_vat_rate() {
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

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		assertEquals( (Object)600.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)300.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
		assertEquals( (Object)(-10.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
	}	

	@Test
	public void test_fixedDiscount_amount_with_calculated_vat_rate_exvat() {
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
			.setAmountExVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off exvat")
		;   
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);
		order.addDiscount(fixedDiscount);		

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

		SveaRequest<SveaCreateOrder> soapRequest = order.usePaymentPlanPayment(code).prepareRequest();

		// all order rows
		assertEquals( (Object)600.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)300.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)160.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
        // all discount rows
        // expected: fixedDiscount: 10 off exvat, order row amount are 66% at 20% vat, 33% at 10% vat => 6.67 @20% and 3.33 @10% 
		assertEquals( (Object)(-6.67), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
		assertEquals( (Object)(-3.33), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat );
	}	
    
	@Test
	public void test_fixedDiscount_amount_with_calculated_vat_rate_incvat() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
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
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;   
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);
		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)60.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)30.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
	    // all discount rows
        // expected: fixedDiscount: 10 off incvat, order row amount are 66% at 20% vat, 33% at 10% vat  
        // 1.2*0.66x + 1.1*0.33x = 10 => x = 8.6580 => 5.7143 @20% and 2.8571 @10% = 10kr
		assertEquals( (Object)(-5.71), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat );
		assertEquals( (Object)(-2.86), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat );
	}	
}	
