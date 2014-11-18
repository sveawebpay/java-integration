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
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CreateInvoiceOrderUnitTest {

	@Test
	public void testInvoiceForIndividualFromSE() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertEquals("Invoice", response.orderType);
		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals(250.00, response.amount, 0);

		// CustomerIdentity
		assertEquals("Individual", response.customerIdentity.getCustomerType());
		assertEquals("194605092222", response.customerIdentity.getNationalIdNumber());
		assertEquals("Persson, Tess T", response.customerIdentity.getFullName());
		assertEquals("Testgatan 1", response.customerIdentity.getStreet());
		assertEquals("c/o Eriksson, Erik", response.customerIdentity.getCoAddress());
		assertEquals("99999", response.customerIdentity.getZipCode());
		assertEquals("Stan", response.customerIdentity.getCity());
		assertEquals("SE", response.customerIdentity.getCountryCode());
	}

	@Test
	public void testInvoiceRequestFailing() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber("")).setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertFalse(response.isOrderAccepted());
	}

	@Test
	public void testFormationOfDecimalsInCalculation() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(WebPayItem.orderRow().setArticleNumber("1").setQuantity(2.0).setAmountExVat(22.68).setDescription("Specification").setName("Prod").setVatPercent(6.0).setDiscountPercent(0.0))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
		assertEquals(48.08, response.amount, 0);
	}

	@Test
	public void testInvoiceCompanySe() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate)
				.setCurrency(TestingTool.DefaultTestCurrency).addCustomerDetails(TestingTool.createMiniCompanyCustomer()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).useInvoicePayment()
				.doRequest();

		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals("SE", response.customerIdentity.getCountryCode());
	}

	@Test
	public void testInvoiceForIndividualFromNl() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowNl())
				.addCustomerDetails(
						WebPayItem.individualCustomer().setBirthDate("19550307").setInitials("SB").setName("Sneider", "Boasman").setStreetAddress("Gate", "42").setLocality("BARENDRECHT")
								.setZipCode("1102 HG").setCoAddress("138")).setCountryCode(COUNTRYCODE.NL).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals(212.00, response.amount, 0);
		assertEquals("0", response.getResultCode());
		assertEquals("Invoice", response.orderType);

		// CustomerIdentity
		assertEquals("Individual", response.customerIdentity.getCustomerType());
		assertEquals("Sneider Boasman", response.customerIdentity.getFullName());
		assertNull(response.customerIdentity.getPhoneNumber());
		assertNull(response.customerIdentity.getEmail());
		assertNull(response.customerIdentity.getIpAddress());
		assertEquals("Gate", response.customerIdentity.getStreet());
		assertNull(response.customerIdentity.getCoAddress());
		assertEquals("42", response.customerIdentity.getHouseNumber());
		assertEquals("1102 HG", response.customerIdentity.getZipCode());
		assertEquals("BARENDRECHT", response.customerIdentity.getCity());
		assertEquals("NL", response.customerIdentity.getCountryCode());
	}

	@Test
	public void testInvoiceDoRequestWithIpAddressSetSE() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber).setIpAddress("123.123.123"))
				.setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testInvoiceRequestUsingAmountIncVatWithZeroVatPercent() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency)
				.setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testFailOnMissingCountryCodeOfCloseOrder() {
		Long orderId = 0L;
		SveaSoapBuilder soapBuilder = new SveaSoapBuilder();

		SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment()
				.prepareRequest();

		WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();

		String xml = xmlBuilder.getCreateOrderEuXml(request.request);
		String url = SveaConfig.getTestWebserviceUrl().toString();
		String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
		NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
		CreateOrderResponse response = new CreateOrderResponse(soapResponse);
		orderId = response.orderId;

		assertTrue(response.isOrderAccepted());

		CloseOrder closeRequest = WebPay.closeOrder(SveaConfig.getDefaultConfig()).setOrderId(orderId).closeInvoiceOrder();

		String expectedMsg = "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";

		assertEquals(expectedMsg, closeRequest.validateRequest());
	}

	@Test
	public void testConfiguration() {
		ConfigurationProviderTestData conf = new ConfigurationProviderTestData();
		CreateOrderResponse response = WebPay.createOrder(conf).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber).setIpAddress("123.123.123"))
				.setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testFormatShippingFeeRowsZero() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addFee(WebPayItem.shippingFee().setShippingId("0").setName("Tess").setDescription("Tester").setAmountExVat(0).setVatPercent(0).setUnit("st"))
				.addCustomerDetails(WebPayItem.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency)
				.setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testCompanyIdResponse() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(WebPayItem.companyCustomer().setNationalIdNumber(TestingTool.DefaultTestCompanyNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testInvoiceCompanyDe() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowDe())
				.addCustomerDetails(WebPayItem.companyCustomer().setNationalIdNumber("12345").setVatNumber("DE123456789").setStreetAddress("Adalbertsteinweg", "1").setZipCode("52070").setLocality("AACHEN"))
				.setCountryCode(COUNTRYCODE.DE).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment()
				.doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}

	@Test
	public void testInvoiceCompanyNl() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowNl())
				.addCustomerDetails(
						WebPayItem.companyCustomer().setCompanyName("Svea bakkerij 123").setVatNumber("NL123456789A12").setStreetAddress("broodstraat", "1").setZipCode("1111 CD").setLocality("BARENDRECHT"))
				.setCountryCode(COUNTRYCODE.NL).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment()
				.doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}
	
	
	/// tests for INTG-515, preparing order rows price specification
	// invoice request	
	@Test
	public void test_orderRows_and_Fees_specified_exvat_and_vat_using_useInvoicePayment_are_prepared_as_exvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
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
	
		// all order rows
		// all shipping fee rows
		// all invoice fee rows		
		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
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
	public void test_orderRows_and_Fees_specified_incvat_and_vat_using_useInvoicePayment_are_prepared_as_incvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(100.00)
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
		
		
		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		assertEquals( (Object)100.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
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
			
		// TODO other rows
	}

	@Test
	public void test_orderRows_and_Fees_specified_incvat_and_exvat_using_useInvoicePayment_are_prepared_as_incvat_and_vat() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountIncVat(100.00)
			.setAmountExVat(80.00)
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
				
		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		assertEquals( (Object)100.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
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
			
		// TODO other rows
	}
	
	
	
	//payment plan request	
	
	// TODO payment plan tests
	
//	@Test
//	public void test_orderRows_and_Fees_specified_exvat_and_vat_using_usePaymentPlanPayment_are_prepared_as_exvat_and_vat() {
//		
//		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
//			.setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
//		;				
//		OrderRowBuilder exvatRow = WebPayItem.orderRow()
//			.setAmountExVat(1000.00)
//			.setVatPercent(25)			
//			.setQuantity(1.0)
//			.setName("exvatRow")
//		;
//		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
//			.setAmountExVat(1000.00)
//			.setVatPercent(25)			
//			.setQuantity(1.0)
//			.setName("exvatRow2")
//		;		
//		
//		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
//			.setAmountExVat(10.00)
//			.setVatPercent(25)
//			.setName("exvatInvoiceFee")
//		;		
//		
//		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
//			.setAmountExVat(20.00)
//			.setVatPercent(25)
//			.setName("exvatShippingFee")
//		;	
//		
//		order.addOrderRow(exvatRow);
//		order.addOrderRow(exvatRow2);
//		order.addFee(exvatInvoiceFee);
//		order.addFee(exvatShippingFee);
//		
//    	// get payment plan params
//        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
//            .setCountryCode(TestingTool.DefaultTestCountryCode)
//            .doRequest();
//        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
//
//        CreateOrderResponse response = order.usePaymentPlanPayment(code).doRequest();
//    
//		assertTrue( response.isOrderAccepted() );
//		System.out.println( "Check logs that order rows were sent as exvat+vat for order row #"+response.orderId);		
//		// Expected log:
//		// ...
//		
//		// TODO
//		
//		// ...		
//	}


	//validation of same order row price/vat specification in same order
	@Test
	public void test_that_createOrder_with_uniform_orderRow_and_Fee_price_specifications_does_not_throw_validation_error() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("exvatRow")
		;
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountExVat(12.50)
			.setVatPercent(25)
			.setName("exvatInvoiceFee")
		;		
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(20.00)
			.setVatPercent(25)
			.setName("exvatShippingFee")
		;	
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(incvatRow);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);		

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		}
		catch (SveaWebPayException e){			
	        fail( "Unexpected SveaWebPayException thrown: "+e.getCause().getMessage() );		
		}
    }	
	
	@Test
	public void test_that_createOrder_with_mixed_orderRow_and_Fee_price_specifications_does_not_throw_validation_error() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("exvatRow")
		;
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
			.setAmountExVat(100.00)
			.setVatPercent(25)			
			.setQuantity(1.0)
			.setName("incvatRow")
		;		
		
		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
			.setAmountIncVat(12.50)
			.setVatPercent(25)
			.setName("exvatInvoiceFee")
		;		
		
		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
			.setAmountExVat(20.00)
			.setVatPercent(25)
			.setName("exvatShippingFee")
		;	
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(incvatRow);
		order.addFee(exvatInvoiceFee);
		order.addFee(exvatShippingFee);		

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		}
		catch (SveaWebPayException e){			
	        fail( "Unexpected SveaWebPayException thrown: "+e.getCause().getMessage() );		
		}		
	}
	
	
	//if mixed specification types, send order as exvat if at least one exvat + vat found
	@Test
	public void test_that_createOrder_request_is_sent_as_incvat_iff_no_exvat_specified_anywhere_in_order_only_order_rows() {
		
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
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
		
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)72.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)33.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)17.60, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)8.80, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat );
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
				.setAmountIncVat(72.00)
				.setVatPercent(20)			
				.setQuantity(1.0)
				.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(33.00)
			.setAmountExVat(30.00)
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
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;   
		
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);
		order.addDiscount(fixedDiscount);		

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)72.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat );
		assertEquals( (Object)33.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat );
		// all shipping fee rows
		assertEquals( (Object)17.60, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( true, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)8.80, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
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
		// order total should be (72+33+17.6+8.8)-10 = 121.40, see integration test
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
				.setAmountExVat(60.00)
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
			.setAmountIncVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off incvat")
		;   
		
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addFee(incvatInvoiceFee);
		order.addFee(incvatShippingFee);
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
		assertEquals( (Object)16.00, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		assertEquals( false, soapRequest.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat );
		// all invoice fee rows		
		assertEquals( (Object)8.00, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
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

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( (Object)80.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		// all shipping fee rows
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		// all invoice fee rows		
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
		// all discount rows
		// expected: 10% off orderRow rows: 2x 80.00 @25% => -16.00 @25% discount
		assertEquals( (Object)(-16.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)25.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
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
		
		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)60.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( (Object)30.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		// all shipping fee rows
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		// all invoice fee rows		
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation		
        // all discount rows
        // expected: 10% off orderRow rows: 1x60.00 @20%, 1x30@10% => split proportionally across order row (only) vat rate: -6.0 @20%, -3.0 @10%
		assertEquals( (Object)(-6.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation						
		assertEquals( (Object)(-3.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(5).VatPercent  ); // cast avoids deprecation		
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

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)60.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( (Object)30.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		// all shipping fee rows
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		// all invoice fee rows		
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
		assertEquals( (Object)(-10.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
	}	
	
	@Test
	public void test_fixedDiscount_amount_with_set_incvat_vat_rate() {
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

		SveaRequest<SveaCreateOrder> soapRequest = order.useInvoicePayment().prepareRequest();
		// all order rows
		assertEquals( (Object)60.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)20.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(0).VatPercent  ); // cast avoids deprecation		
		assertEquals( (Object)30.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(1).VatPercent  ); // cast avoids deprecation		
		// all shipping fee rows
		assertEquals( (Object)16.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(2).VatPercent  ); // cast avoids deprecation		
		// all invoice fee rows		
		assertEquals( (Object)8.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(3).VatPercent  ); // cast avoids deprecation				
        // all discount rows
        // expected: fixedDiscount: 10 @10% => 11kr, expressed as exvat + vat in request
		assertEquals( (Object)(-10.00), (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit  ); // cast avoids deprecation
		assertEquals( (Object)10.0, (Object)soapRequest.request.CreateOrderInformation.OrderRows.get(4).VatPercent  ); // cast avoids deprecation							
	}	

	@Test
	public void test_fixedDiscount_amount_with_calculated_vat_rate_exvat() {
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
			.setAmountExVat(10.0)
			.setDiscountId("TenCrownsOff")
			.setName("fixedDiscount: 10 off exvat")
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
