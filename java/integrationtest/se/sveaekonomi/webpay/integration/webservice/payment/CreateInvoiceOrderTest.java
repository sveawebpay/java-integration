package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;






import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.ConfigurationProviderTestData;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CreateInvoiceOrderTest {

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
		NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, SveaConfig.getDefaultConfig(), PAYMENTTYPE.INVOICE);
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
	
	
	/// tests for sending orderRows to webservice, specified as exvat + vat in soap request
	@Test
	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false() {
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
		assertEquals( (Object)1304.00, response.amount );		
		//System.out.println( "test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false\n"
		//		+ "  Check logs that order rows were sent as exvat+vat for order row #"+response.orderId);		
	
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
	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true() {
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

		CreateOrderResponse response = order.useInvoicePayment().doRequest();

		assertTrue( response.isOrderAccepted() );
		assertEquals( (Object)1300.00, response.amount );		
		//System.out.println( "test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true\n"
		//		+ "  Check logs that order rows were sent as incvat+vat for order row #"+response.orderId);		
		
		//Expected log:
		//...
		//<web:OrderRows>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>incvatRow</web:Description>
        //   <web:PricePerUnit>720.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>20.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>incvatRow2</web:Description>
        //   <web:PricePerUnit>330.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>incvatShippingFee</web:Description>
        //   <web:PricePerUnit>172.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>
        //   </web:ArticleNumber>
        //   <web:Description>incvatInvoiceFee</web:Description>
        //   <web:PricePerUnit>88.0</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>TenCrownsOff</web:ArticleNumber>
        //   <web:Description>fixedDiscount: 10 off incvat (20%)</web:Description>
        //   <web:PricePerUnit>-6.86</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>20.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        // <web:OrderRow>
        //   <web:ArticleNumber>TenCrownsOff</web:ArticleNumber>
        //   <web:Description>fixedDiscount: 10 off incvat (10%)</web:Description>
        //   <web:PricePerUnit>-3.14</web:PricePerUnit>
        //   <web:PriceIncludingVat>true</web:PriceIncludingVat>
        //   <web:NumberOfUnits>1.0</web:NumberOfUnits>
        //   <web:Unit>
        //   </web:Unit>
        //   <web:VatPercent>10.0</web:VatPercent>
        //   <web:DiscountPercent>0.0</web:DiscountPercent>
        // </web:OrderRow>
        //</web:OrderRows>
	    // ...			
	}		
	
	// real world examples of response total order amount not matching shop order total (159.1 vs 159.0) 
	@Test
	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false_exvat_two_decimals_wrong() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
				.setAmountExVat(116.94)
				.setVatPercent(24)			
				.setQuantity(1.0)
				.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(7.26)
			.setVatPercent(24)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		OrderRowBuilder exvatRow3 = WebPayItem.orderRow()
				.setAmountExVat(4.03)
				.setVatPercent(24)			
				.setQuantity(1.0)
				.setName("exvatRow2")
			;	
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addOrderRow(exvatRow3);
	
		CreateOrderResponse response = order.useInvoicePayment().doRequest();
		
		//System.out.println(response.amount);
		assertTrue( response.isOrderAccepted() );
		assertEquals( (Object)159.01, response.amount );		
	}		

	@Test
	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false_exvat_many_decimals_wrong() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder exvatRow = WebPayItem.orderRow()
				.setAmountExVat(116.93548)
				.setVatPercent(24)			
				.setQuantity(1.0)
				.setName("exvatRow")
		;
		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
			.setAmountExVat(7.2580645)
			.setVatPercent(24)			
			.setQuantity(1.0)
			.setName("exvatRow2")
		;		
		OrderRowBuilder exvatRow3 = WebPayItem.orderRow()
				.setAmountExVat(4.032258)
				.setVatPercent(24)			
				.setQuantity(1.0)
				.setName("exvatRow2")
			;	
		
		order.addOrderRow(exvatRow);
		order.addOrderRow(exvatRow2);
		order.addOrderRow(exvatRow3);
	
		CreateOrderResponse response = order.useInvoicePayment().doRequest();
		
		//System.out.println(response.amount);
		assertTrue( response.isOrderAccepted() );
		assertEquals( (Object)159.01, response.amount );		
	}	
	@Test
	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true_incvat_intended() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		;				
		OrderRowBuilder incvatRow = WebPayItem.orderRow()
				.setAmountIncVat(145)
				.setVatPercent(24)			
				.setQuantity(1.0)
				.setName("incvatRow")
		;
		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
			.setAmountIncVat(9.00)
			.setVatPercent(24)			
			.setQuantity(1.0)
			.setName("incvatRow2")
		;		
		OrderRowBuilder incvatRow3 = WebPayItem.orderRow()
			.setAmountIncVat(5.00)
			.setVatPercent(24)			
			.setQuantity(1.0)
			.setName("incvatRow3")
		;
		
		order.addOrderRow(incvatRow);
		order.addOrderRow(incvatRow2);
		order.addOrderRow(incvatRow3);	

		CreateOrderResponse response = order.useInvoicePayment().doRequest();

		//System.out.println(response.amount);
		assertTrue( response.isOrderAccepted() );
		assertEquals( (Object)159.00, response.amount );		
		//System.out.println( "test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true\n"
		//		+ "  Check logs that order rows were sent as incvat+vat for order row #"+response.orderId);		
		
		
	}
}
