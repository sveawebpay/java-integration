package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.helper.WebserviceRowFormatter;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

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
    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_single_vat_rate_having_rows_specified_ex_vat() {
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
        assertEquals(-100L, (long)newRow.getAmount());
        assertEquals(-20L, (long)newRow.getVat());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
//    @Test
//    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_multiple_vat_rates_having_rows_specified_ex_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                		   			.setName("product with price 100 @25% = 125")
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                		   			.setName("product with price 100 @6% = 106")
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("f100e")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setAmountExVat(100.00)
//                                                                    .setUnit("st"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//        
//        
//        Double totalDiscountExVat = 0.0;
//        // 100*200/300 = 66.67 ex. 25% vat => discount 83.34 (incl. 16.67 vat @25%)
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals("f100e", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-66.67, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        totalDiscountExVat += newRow.PricePerUnit;
//        
//        // 100*100/300 = 33.33 ex. 6% vat => discount 35.33 (incl 2.00 vat @6%)
//        newRow = newRows.get(3);
//        assertEquals("f100e", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-33.33, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        totalDiscountExVat += newRow.PricePerUnit;
//        
//        assertEquals(-100.00, totalDiscountExVat, 0);
//        
//        // check order total        
//        double total = 
//        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
//        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 106.00
//        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// -66.67
//        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits 		// -33.33
//		;        
//        assertEquals( 237.33, Double.valueOf(String.format(Locale.ENGLISH,"%.2f",total)), 0.001 );
//    }    
//
//    // iff all order rows are specified inc. vat the new order rows should be specified inc. vat as well    
//    @Test
//    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_single_vat_rate_having_only_rows_specified_inc_vat_is_specified_inc_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(5.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("f1e")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setAmountExVat(1)
//                                                                    .setUnit("st"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//        
//        SveaOrderRow newRow = newRows.get(1);
//        assertEquals("f1e", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc", newRow.Description);
//        assertEquals(-1.25, newRow.PricePerUnit, 0);	// discount as incvat, as other order rows are incvat
//        assertTrue( newRow.PriceIncludingVat );			// discount as incvat, as other order rows are incvat
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        
//        // check order total        
//        double total = 
//        		newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 5.00
//        		newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits 		// -1.25
//		; 
//        assertEquals( 3.75, total, 0.001 );
//    } 
//    @Test
//    public void test_FixedDiscount_specified_using_amountExVat_in_order_with_multiple_vat_rates_having_only_rows_specified_inc_vat_is_specified_inc_vat() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                		   			.setName("product with price 100 @25% = 125")
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                		   			.setName("product with price 100 @6% = 106")
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("f100e")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setAmountExVat(100.00)	// discount specified exvat	
//                                                                    .setUnit("st"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//                
//        Double totalDiscountIncVat = 0.0;
//        // 100*200/300 = 66.67 ex. 25% vat => discount 83.34 (incl. 16.67 vat @25%)
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals("f100e", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-83.34, newRow.PricePerUnit, 0);	// discount as incvat, as other order rows are incvat
//        assertTrue( newRow.PriceIncludingVat );			// discount as incvat, as other order rows are incvat
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        totalDiscountIncVat += newRow.PricePerUnit;
//        
//        // 100*100/300 = 33.33 ex. 6% vat => discount 35.33 (incl 2.00 vat @6%)
//        newRow = newRows.get(3);
//        assertEquals("f100e", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-35.33, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        totalDiscountIncVat += newRow.PricePerUnit;
//
//        // mean vat based on exvat: (200/300=0,6667)*1,25 + (100/300=0,3333)*1,06) => 1.186673=1.1867
//        assertEquals(-100.00 * 1.1867, totalDiscountIncVat, 0.0001);	
//        
//        // check order total        
//        double total = 
//        		newRows.get(0).PricePerUnit * newRows.get(0).NumberOfUnits  +	// 250.00
//        		newRows.get(1).PricePerUnit * newRows.get(1).NumberOfUnits  +	// 106.00
//        		newRows.get(2).PricePerUnit * newRows.get(2).NumberOfUnits  +	// -83.34
//        		newRows.get(3).PricePerUnit * newRows.get(3).NumberOfUnits 		// -35.33
//		;        
//        assertEquals( 237.33, Double.valueOf(String.format(Locale.ENGLISH,"%.2f",total)), 0.001 );
//    }    
//
//    // iff no specified vatPercent => split discount incl. vat over the diffrent tax rates present in order
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
    }    
  
    
//    @Test
//    public void test_FixedDiscount_specified_using_amountIncVat_in_order_with_multiple_vat_rates() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("f100i")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setAmountIncVat(100)
//                                                                    .setUnit("st"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // first order row
//        SveaOrderRow newRow = newRows.get(0);
//        assertEquals(100.00, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(2, newRow.NumberOfUnits, 0);
//        
//        // second order row 
//        newRow = newRows.get(1);
//        assertEquals(100.00, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);        
//        
//        // discount row @25%        
//        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
//        newRow = newRows.get(2);
//        assertEquals("f100i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (25%)", newRow.Description);
//        assertEquals(-56.18, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//
//        // discount row @6%        
//        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
//        newRow = newRows.get(3);
//        assertEquals("f100i", newRow.ArticleNumber);
//        assertEquals("couponName: couponDesc (6%)", newRow.Description);
//        assertEquals(-28.09, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//        
//        // check order total        
//        double total = 
//        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
//        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 106.00
//        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// -70.22
//        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits 		// -29.78
//		;        
//        assertEquals( 256.00, Double.valueOf(String.format(Locale.ENGLISH,"%.2f",total)), 0.001 );
//    }
//    
//    // iff specified vatPercent => add as single row with specified vat rate only honouring specified amount and vatPercent 
//    @Test
//    public void test_FixedDiscount_specified_using_IncVat_and_vatPercent_having_rows_specified_ex_vat_is_added_as_single_exvat_discount_row() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setAmountIncVat(111)
//                                                                    .setVatPercent(25));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//    	// -111inc @25% <=> -88.80ex @25%
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals(-88.80, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        
//        assertEquals( 3, newRows.size() );   // no more rows        
//    }
//    @Test
//    public void test_FixedDiscount_specified_using_ExVat_and_vatPercent_having_rows_specified_ex_vat_is_added_as_single_exvat_discount_row() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setAmountExVat(88.80)
//                                                                    .setVatPercent(25));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//    	// -111inc @25% <=> -88.80ex @25%
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals(-88.80, newRow.PricePerUnit, 0);
//        assertFalse( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//
//        assertEquals( 3, newRows.size() );   // no more rows        
//    }
//    @Test
//    public void test_FixedDiscount_specified_using_IncVat_and_vatPercent_having_rows_specified_inc_vat_is_added_as_single_incvat_discount_row() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setAmountIncVat(111.00)
//                                                                    .setVatPercent(25));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//    	// -111inc @25% <=> -88.80ex @25%
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals(-111.00, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//
//        assertEquals( 3, newRows.size() );   // no more rows        
//    }
//    @Test
//    public void test_FixedDiscount_specified_using_ExVat_and_vatPercent_having_rows_specified_inc_vat_is_added_as_single_incvat_discount_row() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setAmountExVat(88.80)
//                                                                    .setVatPercent(25));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//    	// -111inc @25% <=> -88.80ex @25%
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals(-111.00, newRow.PricePerUnit, 0);
//        assertTrue( newRow.PriceIncludingVat );
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//
//        assertEquals( 3, newRows.size() );   // no more rows        
//    }
//    
//    // check that fixed discount split over vat rates ratios are present based on order item rows only, not shipping or invoice fees
//    // TODO 	test_FixedDiscount_specified_using_amountExVat_is_calculated_from_order_item_rows_only
//    @Test
//    public void test_FixedDiscount_specified_using_amountIncVat_is_calculated_from_order_item_rows_only() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(25)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
//                                                           			.setName("shipping with price 50 @6% = 53")
//                                                		   			.setAmountExVat(50.00)
//                                                		   			.setVatPercent(6))
//                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
//                                                		   			.setAmountExVat(23.20)
//                                                		   			.setVatPercent(25))
//                                		   		   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("f100i")
//                                                                    .setName("couponName")
//                                                                    .setDescription("couponDesc")
//                                                                    .setAmountIncVat(100)
//                                                                    .setUnit("st"));
//        
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//        // first order row
//        SveaOrderRow newRow = newRows.get(0);
//        assertEquals(100.00, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(2, newRow.NumberOfUnits, 0);
//        
//        // second order row 
//        newRow = newRows.get(1);
//        assertEquals(100.00, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);        
//        
//        // shipping fee row
//        newRow = newRows.get(2);
//        assertEquals(50.00, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        
//        // invoice fee row 
//        newRow = newRows.get(3);
//        assertEquals(23.20, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);  
//        
//        // discount row @25%          
//        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
//        newRow = newRows.get(4);
//        assertEquals(-56.18, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//
//        // discount row @6%        
//        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
//        newRow = newRows.get(5);
//        assertEquals(-28.09, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//
//        // check order total        
//        double total = 
//        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
//        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 106.00
//        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// 53.00
//        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits  +	// 29.00
//        		convertExVatToIncVat( newRows.get(4).PricePerUnit, newRows.get(4).VatPercent ) * newRows.get(4).NumberOfUnits  +	// -70.22
//        		convertExVatToIncVat( newRows.get(5).PricePerUnit, newRows.get(5).VatPercent ) * newRows.get(5).NumberOfUnits 		// -29.78
//		;
//        assertEquals( 338.00, Double.valueOf(String.format(Locale.ENGLISH,"%.2f",total)), 0.001 );    
//    }    
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
   			
   			

    // helper
    private double convertExVatToIncVat(double amountExVat, double vatPercent) {
    	return amountExVat * (1+vatPercent/100);
    }



}
