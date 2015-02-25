package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedRowFormatterTest {
    
    @Test
    public void testFormatOrderRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(WebPayItem.orderRow()
                .setArticleNumber("0")
                .setName("Tess")
                .setDescription("Tester")
                .setAmountExVat(4)
                .setVatPercent(25)
                .setQuantity(1.0)
                .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(500L, (long)newRow.getAmount());
        assertEquals(100L, (long)newRow.getVat());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatShippingFeeRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addFee(WebPayItem.shippingFee()
                .setShippingId("0")
                .setName("Tess")
                .setDescription("Tester")
                .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatShippingFeeRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addFee(WebPayItem.shippingFee()
                        .setAmountExVat(4)
                        .setVatPercent(25));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertTrue(500L == newRow.getAmount());
        assertTrue(100L == newRow.getVat());
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(WebPayItem.fixedDiscount()
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatFixedDiscountRowsAmount() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(WebPayItem.fixedDiscount()
                        .setAmountIncVat(4));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertEquals(-400L, (long)newRow.getAmount());
    }
    
    @Test
    public void testFormatFixedDiscountRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addDiscount(WebPayItem.fixedDiscount()
                        .setAmountIncVat(1)
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(1);
        
        assertEquals(-100L, (long)newRow.getAmount());
        assertEquals(-20L, (long)newRow.getVat());
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(WebPayItem.relativeDiscount()
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatRelativeDiscountRowsAmount() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addDiscount(WebPayItem.relativeDiscount()
                        .setDiscountPercent(10.0));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(1);
        
        assertEquals(-50L, (long)newRow.getAmount());
    }
    
    @Test
    public void testFormatRelativeDiscountRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addDiscount(WebPayItem.relativeDiscount()
                        .setDiscountPercent(10.0));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(1);
        
        assertEquals(-50L, (long)newRow.getAmount());
        assertEquals(-10L, (long)newRow.getVat());
    }
    
    @SuppressWarnings("deprecation")
	@Test
    public void testFormatTotalAmount() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setAmount(100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(200L, new HostedRowFormatter().formatTotalAmount(rows));
    }
    
    @SuppressWarnings("deprecation")
	@Test
    public void testFormatTotalVat() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(200L, new HostedRowFormatter().formatTotalVat(rows));
    }
    
    @SuppressWarnings("deprecation")
	@Test
    public void testFormatTotalVatNegative() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(-100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(-200L, new HostedRowFormatter().formatTotalVat(rows));
    }

    // ported over tests of discounts from WebserviceRowFormatterTest
    
    /// fixed discount
    // iff no specified vatPercent => split discount excl. vat over the diffrent tax rates present in order
    @Test
    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_single_vat_rate() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(4.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f1e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountExVat(1)
                                                                    .setUnit("st"));

        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(1);                        
        assertEquals("f1e", newRow.getSku());
        assertEquals("couponName", newRow.getName());
        assertEquals("couponDesc", newRow.getDescription());        
        //assertEquals("couponName: couponDesc", newRow.getDescription());
        assertEquals(-125L, (long)newRow.getAmount());
        assertEquals(-25L, (long)newRow.getVat());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
        
	    try {		    	
	    	// check order total        	    
	    	String paymentXml = order
                    .setCountryCode(COUNTRYCODE.SE)
    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
			    	.setCurrency(CURRENCY.SEK)
		    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                		.getPaymentForm()
            				.getXmlMessage();

	    	// 5.00 (1.00) - 1.25 (.25) = 3.75 (.75)
	    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>375</amount>"));
		    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>75</vat>"));
	    }
	    catch( Exception e ) {
	    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
	    }        
    }
    @Test
    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_multiple_vat_rates() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @25% = 125")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @6% = 106")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f100e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountExVat(100.00)
                                                                    .setUnit("st"));
        
		    ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);

	        // 100*200/300 = 66.67 ex. 25% vat => discount 83.34 (incl. 16.67 vat @25%)
	        // 100*100/300 = 33.33 ex. 6% vat => discount 35.33 (incl 2.00 vat @6%)
		    // => total discount is 118.67 (incl 18.67 vat @18.67%)
		    HostedOrderRowBuilder newRow = newRows.get(2);                        
		    assertEquals("f100e", newRow.getSku());
		    assertEquals("couponName", newRow.getName());
		    assertEquals("couponDesc", newRow.getDescription());        
		    //assertEquals("couponName: couponDesc", newRow.getDescription());
		    assertEquals(-11867, (long)newRow.getAmount(), 0.0001);
		    assertEquals(-1867, (long)newRow.getVat(), 0.0001);
		    assertEquals(1, newRow.getQuantity(), 0);
		    assertEquals("st", newRow.getUnit());    
     
		    try {		    	
		    	// check order total        
		    	String paymentXml = order
                        .setCountryCode(COUNTRYCODE.SE)
        				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
    				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
				    	.setCurrency(CURRENCY.SEK)
			    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
	                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
	                		.getPaymentForm()
                				.getXmlMessage();
	    		//	newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
	    		//	newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
	    		//	newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -83.34
		    	//	newRows.get(3).PricePerUnit * newRows.get(3).NumberOfUnits 		// -35.33
		    	//assertEquals( 237.33, Double.valueOf(String.format(Locale.ENGLISH,"%.2f",total)), 0.001 );		    
		    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>23733</amount>"));
			    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>3733</vat>"));
		    }
		    catch( Exception e ) {
		    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
		    }
    }    

    // iff no specified vatPercent => split discount incl. vat over the diffrent tax rates present in order
    @Test
    public void test_FixedDiscount_specified_using_amountIncVat_in_order_with_single_vat_rate() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(4.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f1e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountIncVat(1)
                                                                    .setUnit("st"));

        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = newRows.get(1);                        
        assertEquals("f1e", newRow.getSku());
        assertEquals("couponName", newRow.getName());
        assertEquals("couponDesc", newRow.getDescription());        
        //assertEquals("couponName: couponDesc", newRow.getDescription());
        assertEquals(-100L, (long)newRow.getAmount());
        assertEquals(-20L, (long)newRow.getVat());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
        
	    try {		    	
	    	// check order total        	    
	    	String paymentXml = order
                    .setCountryCode(COUNTRYCODE.SE)
    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
			    	.setCurrency(CURRENCY.SEK)
		    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                		.getPaymentForm()
            				.getXmlMessage();

	    	// 5.00 (1.00) - 1.00 (.20) = 4.00 (1.00)
	    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>400</amount>"));
		    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>100</vat>"));
	    }
	    catch( Exception e ) {
	    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
	    }
    }
  
    
    @Test
    public void test_FixedDiscount_specified_using_amountIncVat_in_order_with_multiple_vat_rates() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @25% = 125")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @6% = 106")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f100e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountIncVat(100.00)
                                                                    .setUnit("st"));
        
		    ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
		    
	        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
	        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
		    // => total discount is 100.00 (incl 15.73 vat)
		    HostedOrderRowBuilder newRow = newRows.get(2);                        
		    assertEquals("f100e", newRow.getSku());
		    assertEquals("couponName", newRow.getName());
		    assertEquals("couponDesc", newRow.getDescription());        
		    //assertEquals("couponName: couponDesc", newRow.getDescription());
		    assertEquals(-10000, (long)newRow.getAmount(), 0.0001);
		    assertEquals(-1573, (long)newRow.getVat(), 0.0001);
		    assertEquals(1, newRow.getQuantity(), 0);
		    assertEquals("st", newRow.getUnit());    
     
		    try {		    	
		    	// check order total        
		    	String paymentXml = order
                        .setCountryCode(COUNTRYCODE.SE)
        				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
    				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
				    	.setCurrency(CURRENCY.SEK)
			    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
	                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
	                		.getPaymentForm()
                				.getXmlMessage();
	    		//	newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
	    		//	newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
	    		//	newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -70.22 (14.04) + 29.78 (1.69) = -100.00 (-15.73)
		    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>25600</amount>"));
			    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>4027</vat>"));
		    }
		    catch( Exception e ) {
		    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
		    }
    }
    
    // iff specified vatPercent => add as single row with specified vat rate only honouring specified amount and vatPercent 
    @Test
    public void test_FixedDiscount_specified_using_IncVat_and_vatPercent_is_added_as_single_discount_row() {
    	
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(25)
									                                 .setQuantity(2.0))
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(6)
									                                 .setQuantity(1.0))
									                .addDiscount(WebPayItem.fixedDiscount()
									                                 .setAmountIncVat(111.00)
									                                 .setVatPercent(25));

        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);    	
    	
    	// -111.00 (-22.20)
	    HostedOrderRowBuilder newRow = newRows.get(2);                        
	    assertEquals(-11100, (long)newRow.getAmount(), 0.0001);
	    assertEquals(-2220, (long)newRow.getVat(), 0.0001);
	    assertEquals(1, newRow.getQuantity(), 0);
 
	    try {		    	
	    	// check order total        
	    	String paymentXml = order
                    .setCountryCode(COUNTRYCODE.SE)
    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
			    	.setCurrency(CURRENCY.SEK)
		    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                		.getPaymentForm()
            				.getXmlMessage();
    		//	newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
    		//	newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
    		//	newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -111.00 (-22.20)
	    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>24500</amount>"));
		    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>3380</vat>"));
	    }
	    catch( Exception e ) {
	    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
	    }    	
    }
    		
    @Test    	
    public void test_FixedDiscount_specified_using_ExVat_and_vatPercent_is_added_as_single_discount_row() {    	
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(25)
									                                 .setQuantity(2.0))
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(6)
									                                 .setQuantity(1.0))
									                .addDiscount(WebPayItem.fixedDiscount()
									                                 .setAmountExVat(88.80)
									                                 .setVatPercent(25));

        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);    	
    	
    	// 88.80ex @25% => -111.00 (-22.20)
	    HostedOrderRowBuilder newRow = newRows.get(2);                        
	    assertEquals(-11100, (long)newRow.getAmount(), 0.0001);
	    assertEquals(-2220, (long)newRow.getVat(), 0.0001);
	    assertEquals(1, newRow.getQuantity(), 0);
 
	    try {		    	
	    	// check order total        
	    	String paymentXml = order
                    .setCountryCode(COUNTRYCODE.SE)
    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
			    	.setCurrency(CURRENCY.SEK)
		    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                		.getPaymentForm()
            				.getXmlMessage();
    		//	newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
    		//	newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
    		//	newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -111.00 (-22.20)
	    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>24500</amount>"));
		    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>3380</vat>"));
	    }
	    catch( Exception e ) {
	    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
	    }    	
    }
    
    @Test    	
    public void test_FixedDiscount_specified_using_ExVat_and_IncVat_is_added_as_single_discount_row() {    	
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(25)
									                                 .setQuantity(2.0))
									                .addOrderRow(WebPayItem.orderRow()
									                                 .setAmountExVat(100.00)
									                                 .setVatPercent(6)
									                                 .setQuantity(1.0))
									                .addDiscount(WebPayItem.fixedDiscount()
									                                 .setAmountIncVat(111.00)
									                                 .setAmountExVat(88.80));

        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);    	
    	
    	// 111.00inc and 88.80ex => @25% => -111.00 (-22.20)
	    HostedOrderRowBuilder newRow = newRows.get(2);                        
	    assertEquals(-11100, (long)newRow.getAmount(), 0.0001);
	    assertEquals(-2220, (long)newRow.getVat(), 0.0001);
	    assertEquals(1, newRow.getQuantity(), 0);
 
	    try {		    	
	    	// check order total        
	    	String paymentXml = order
                    .setCountryCode(COUNTRYCODE.SE)
    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
			    	.setCurrency(CURRENCY.SEK)
		    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                		.getPaymentForm()
            				.getXmlMessage();
    		//	newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
    		//	newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
    		//	newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -111.00 (-22.20)
	    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>24500</amount>"));
		    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>3380</vat>"));
	    }
	    catch( Exception e ) {
	    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
	    }    	
    }
        
    // check that fixed discount split over vat rates ratios are present based on order item rows only, not shipping or invoice fees
    @Test
    public void test_FixedDiscount_specified_using_amountExVat_is_calculated_from_order_item_rows_only() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @25% = 125")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @6% = 106")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                           			.setName("shipping with price 50 @6% = 53")
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                                    .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f100e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountExVat(100.00)
                                                                    .setUnit("st"));
        
		    ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);

	        // 100*200/300 = 66.67 ex. 25% vat => discount 83.34 (incl. 16.67 vat @25%)
	        // 100*100/300 = 33.33 ex. 6% vat => discount 35.33 (incl 2.00 vat @6%)
		    // => total discount is 118.67 (incl 18.67 vat @18.67%)
		    HostedOrderRowBuilder newRow = newRows.get(4);                        
		    assertEquals("f100e", newRow.getSku());
		    assertEquals("couponName", newRow.getName());
		    assertEquals("couponDesc", newRow.getDescription());        
		    //assertEquals("couponName: couponDesc", newRow.getDescription());
		    assertEquals(-11867, (long)newRow.getAmount(), 0.0001);
		    assertEquals(-1867, (long)newRow.getVat(), 0.0001);
		    assertEquals(1, newRow.getQuantity(), 0);
		    assertEquals("st", newRow.getUnit());    
     
		    try {		    	
		    	// check order total        
		    	String paymentXml = order
                        .setCountryCode(COUNTRYCODE.SE)
        				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
    				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
				    	.setCurrency(CURRENCY.SEK)
			    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
	                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
	                		.getPaymentForm()
                				.getXmlMessage();
	    		// 250.00 (50.00)
	    		// 106.00 (6.00)
	    		// 53.00 (3.00)
	    		// 29.00 (5.80)
		    	// -118.67 (-18.67)
		    	// => 319.33 (46.13)
		    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>31933</amount>"));
			    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>4613</vat>"));
		    }
		    catch( Exception e ) {
		    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
		    }
    }    
    @Test
    public void test_FixedDiscount_specified_using_amountIncVat_is_calculated_from_order_item_rows_only() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @25% = 125")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                		   			.setName("product with price 100 @6% = 106")
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                           			.setName("shipping with price 50 @6% = 53")
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                                    .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f100e")
                                                                    .setName("couponName")
                                                                    .setDescription("couponDesc")
                                                                    .setAmountIncVat(100.00)
                                                                    .setUnit("st"));
        
		    ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);

	        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
	        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
		    // => total discount is 100.00 (incl 15.73 vat)
		    HostedOrderRowBuilder newRow = newRows.get(4);                        
		    assertEquals("f100e", newRow.getSku());
		    assertEquals("couponName", newRow.getName());
		    assertEquals("couponDesc", newRow.getDescription());        
		    //assertEquals("couponName: couponDesc", newRow.getDescription());
		    assertEquals(-10000, (long)newRow.getAmount(), 0.0001);
		    assertEquals(-1573, (long)newRow.getVat(), 0.0001);
		    assertEquals(1, newRow.getQuantity(), 0);
		    assertEquals("st", newRow.getUnit());    
     
		    try {		    	
		    	// check order total        
		    	String paymentXml = order
                        .setCountryCode(COUNTRYCODE.SE)
        				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
    				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
				    	.setCurrency(CURRENCY.SEK)
			    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
	                		.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
	                		.getPaymentForm()
                				.getXmlMessage();
	    		// 250.00 (50.00)
	    		// 106.00 (6.00)
	    		// 53.00 (3.00)
	    		// 29.00 (5.80)
	    		// -70.22 (14.04) + 29.78 (1.69) = -100.00 (-15.73)
		    	// => 338.00 (49.07)
		    	Assert.assertThat(paymentXml, CoreMatchers.containsString("<amount>33800</amount>"));
			    Assert.assertThat(paymentXml, CoreMatchers.containsString("<vat>4907</vat>"));
		    }
		    catch( Exception e ) {
		    	System.out.println( e.getCause().getMessage() );	// show validation errors et al.
		    }
        }
//    
//    
//    /// relative discount    
//    // iff no specified discount vat rate, check that calculated vat rate is split correctly across vat rates
//    @Test
//    public void test_RelativeDiscount_in_order_with_single_vat_rate_having_rows_specified_ex_vat_is_specified_ex_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//									                .addOrderRow(WebPayItem.orderRow()
//									                        .setAmountExVat(40.00)
//									                        .setVatPercent(25)
//									                        .setQuantity(2.0))
//							                        .addOrderRow(WebPayItem.orderRow()
//							                                .setAmountIncVat(50.00)
//							                                .setVatPercent(25)
//							                                .setQuantity(1.0))
//							                        .addOrderRow(WebPayItem.orderRow()
//							                                .setAmountExVat(40.00)
//							                                .setAmountIncVat(50.00)
//							                                .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.relativeDiscount()
//	                                                           .setDiscountId("r10%i")
//                                                               .setDiscountPercent(10.0)
//                                                               .setUnit("kr"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // 10% of (80ex + 40ex + 40ex =) 160ex @25% => -16ex @25% 
//        SveaOrderRow newRow = newRows.get(3);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals(-16.00, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayWebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("kr", newRow.Unit);
//    }    
//    @Test
//    public void test_RelativeDiscount_in_order_with_single_vat_rate_having_only_rows_specified_inc_vat_is_specified_inc_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//									                .addOrderRow(WebPayItem.orderRow()
//									                        .setAmountIncVat(50.00)
//									                        .setVatPercent(25)
//									                        .setQuantity(2.0))
//							                        .addOrderRow(WebPayItem.orderRow()
//							                                .setAmountIncVat(50.00)
//							                                .setVatPercent(25)
//							                                .setQuantity(1.0))
//							                        .addOrderRow(WebPayItem.orderRow()
//							                                .setAmountExVat(40.00)
//							                                .setAmountIncVat(50.00)
//							                                .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.relativeDiscount()
//	                                                           .setDiscountId("r10%i")
//                                                               .setDiscountPercent(10.0)
//                                                               .setUnit("kr"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // 10% of (100inc + 50inc + 50inc =) 200inc @25% => -20inc @25%
//        SveaOrderRow newRow = newRows.get(3);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals(-20.00, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayWebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("kr", newRow.Unit);
//    }
//        
//    // if we have two orders items with different vat rate, we need to create two discount order rows, one for each vat rate
//    @Test
//    public void test_RelativeDiscount_in_order_with_multiple_vat_rates_having_rows_specified_ex_vat_is_specified_ex_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(12)
//                                                                    .setQuantity(1.0))
//                                                    .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.relativeDiscount()
//                                                                    .setDiscountId("r10%i")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setDiscountPercent(10.0));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // 10% of 200ex @25%, 100ex @12%, 100ex @6% => -20ex @25%, -10ex @12%, -10ex @6% 
//        SveaOrderRow newRow = newRows.get(3);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-20.00, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(4);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (12%)", newRow.Description);
//        assertEquals(-10.00, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(12, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(5);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-10.00, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"        
//    }    
//    @Test
//    public void test_RelativeDiscount_in_order_with_multiple_vat_rates_having_only_rows_specified_inc_vat_is_specified_inc_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(112.00)
//                                                                    .setVatPercent(12)
//                                                                    .setQuantity(1.0))
//                                                    .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.relativeDiscount()
//                                                                    .setDiscountId("r10%i")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setDiscountPercent(10.0));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // 10% of 250inc @25%, 112inc @12%, 106inc @6% => -25inc @25%, -11,20inc @12%, -10,6inc @6% 
//        SveaOrderRow newRow = newRows.get(3);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-25.00, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(4);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (12%)", newRow.Description);
//        assertEquals(-11.20, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(12, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(5);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-10.60, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"        
//    }
//    
//    // check that relative discount split over vat rates ratios are present based on order item rows only, not shipping or invoice fees
//    @Test
//    public void test_RelativeDiscount_is_calculated_from_order_item_rows_only() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(112.00)
//                                                                    .setVatPercent(12)
//                                                                    .setQuantity(1.0))
//                                                    .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                    .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
//													   			.setName("shipping with price 50 @6% = 53")
//													   			.setAmountIncVat(53.00)
//													   			.setVatPercent(6))
//										   			.addFee(WebPayItem.invoiceFee()		// fee row should be ignored by discount calculation
//													   			.setAmountIncVat(29.00)
//													   			.setVatPercent(25))
//                                                   .addDiscount(WebPayItem.relativeDiscount()
//                                                                    .setDiscountId("r10%i")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setDiscountPercent(10.0));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // 10% of 250inc @25%, 112inc @12%, 106inc @6% => -25inc @25%, -11,20inc @12%, -10,6inc @6% 
//        SveaOrderRow newRow = newRows.get(5);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-25.00, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(6);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (12%)", newRow.Description);
//        assertEquals(-11.20, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(12, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
//
//        newRow = newRows.get(7);
//        assertEquals("r10%i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-10.60, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
//        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"        
//    }
//   			
   			
    
    @Test
	public void test_getOrderMeanVatRateBasedOnPriceIncVat_single_vatRate_in_order() {
    
    	HostedRowFormatter hrf = new HostedRowFormatter();
    	Double totalOrderAmount = 100.00;
    	Double totalOrderVat = 20.00;
    	
    	Double meanVatRate = hrf.getOrderMeanVatRateBasedOnPriceIncVat( totalOrderAmount, totalOrderVat );	
		
		// 100=inc (20=vat) => (inc-vat) * 1.v = inc => 1.v = inc/(inc-vat) = vatrate = round((1.v-1*100),2 => vatrate = 25.00)
		assertEquals( 25.00, meanVatRate, 0.0001 );
	} 
    
    @Test
	public void test_getOrderMeanVatRateBasedOnPriceIncVat_multiple_vatRates_in_order() {
	    
    	HostedRowFormatter hrf = new HostedRowFormatter();
    	Double totalOrderAmount = 356.00;
    	Double totalOrderVat = 56.00;
    	
    	Double meanVatRate = hrf.getOrderMeanVatRateBasedOnPriceIncVat( totalOrderAmount, totalOrderVat );	
		
    	// 2*125(25)+106(6) = 356(56) => 1.186666... => vatrate = 18.67
		assertEquals( 18.67, meanVatRate, 0.0001 );
	} 
    

    // helper
    private double convertExVatToIncVat(double amountExVat, double vatPercent) {
    	return amountExVat * (1+vatPercent/100);
    }



}
