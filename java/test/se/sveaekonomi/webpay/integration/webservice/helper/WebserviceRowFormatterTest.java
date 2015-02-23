package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebserviceRowFormatterTest {

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
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);
        
        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatShippingFeeRows() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig())
                    .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                    .addFee(WebPayItem.shippingFee()
                        .setShippingId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setUnit("st"))
                    .addCustomerDetails(WebPayItem.individualCustomer()
                        .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                    .setCountryCode(TestingTool.DefaultTestCountryCode)
                    .setOrderDate(TestingTool.DefaultTestDate)
                    .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                    .setCurrency(TestingTool.DefaultTestCurrency)
                    .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
                    .useInvoicePayment()
                    .prepareRequest();
        
        assertEquals("0", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("Tess: Tester", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(4.0, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(25.0, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits, 0);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
    }
    
    @Test
    public void testFormatInvoiceFeeRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addFee(WebPayItem.invoiceFee()
                .setDescription("Tester")
                .setAmountExVat(4)
                .setVatPercent(25)
                .setUnit("st"));
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);
        
        assertEquals("", newRow.ArticleNumber);
        assertEquals("Tester",newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(WebPayItem.orderRow()
            		.setAmountExVat(4)
            		.setVatPercent(25)
            		.setQuantity(1.0))
            .addDiscount(WebPayItem.fixedDiscount()
                .setDiscountId("0")
                .setName("Tess")
                .setDescription("Tester")
                .setAmountIncVat(1)
                .setUnit("st"));
         
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);
        
        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(-0.8,newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
        .addOrderRow(TestingTool.createMiniOrderRow())
        .addDiscount(TestingTool.createRelativeDiscount());
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);
        
        assertEquals("1", newRow.ArticleNumber);
        assertEquals("Relative: RelativeDiscount", newRow.Description);
        assertEquals(-2.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }
    
    // only amountIncVat => calculate mean vat split into diffrent tax rates present
    // if we have two orders items with different vat rate, we need to create
    // two discount order rows, one for each vat rate
    @Test
    public void testFormatFixedDiscountRowsAmountIncVatWithDifferentVatRatesPresent() {	// same as php test_FixedDiscount_specified_using_amountIncVat_in_order_with_multiple_vat_rates()
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
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("testFormatFixedDiscountRowsWithDifferentVatRatesPresent")
                                                                    .setAmountIncVat(100)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        // first order row
        SveaOrderRow newRow = newRows.get(0);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(2, newRow.NumberOfUnits, 0);
        
        // second order row 
        newRow = newRows.get(1);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);        
        
        // discount row @25%        
        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
        newRow = newRows.get(2);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setAmountIncVat(100): testFormatFixedDiscountRowsWithDifferentVatRatesPresent (25%)", newRow.Description);
        assertEquals(-56.18, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);

        // discount row @6%        
        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
        newRow = newRows.get(3);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setAmountIncVat(100): testFormatFixedDiscountRowsWithDifferentVatRatesPresent (6%)", newRow.Description);
        assertEquals(-28.09, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    @Test
    public void testFormatFixedDiscountRowsAmountIncVatWithDifferentVatRatesPresentCalculatedFromOrderItemRowsOnly() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                		   		   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("testFormatFixedDiscountRowsAmountIncVatWithDifferentVatRatesPresentCalculatedFromOrderItemRowsOnly")
                                                                    .setAmountIncVat(100)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        // first order row
        SveaOrderRow newRow = newRows.get(0);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(2, newRow.NumberOfUnits, 0);
        
        // second order row 
        newRow = newRows.get(1);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);        
        
        // shipping fee row
        newRow = newRows.get(2);
        assertEquals(50.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        // invoice fee row 
        newRow = newRows.get(3);
        assertEquals(23.20, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);  
        
        // discount row @25%          
        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
        newRow = newRows.get(4);
        assertEquals(-56.18, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);

        // discount row @6%        
        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
        newRow = newRows.get(5);
        assertEquals(-28.09, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
    }    
    
    @Test
    public void test_FixedDiscount_specified_using_amountIncVat_are_calculated_from_order_item_rows_only() {	// same as php test_FixedDiscount_specified_using_amountIncVat_are_calculated_from_order_item_rows_only()
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row should be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("f100i")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("test_FixedDiscount_specified_using_amountIncVat_are_calculated_from_order_item_rows_only")
                                                                    .setAmountIncVat(100)
                                                                    .setUnit("kr"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();


        // 100*250/250 = 100 discount incl. 25% vat => 20 discount vat as amount
        SveaOrderRow newRow = newRows.get(3);
        assertEquals(-80.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        double total = 
        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 53.00
        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// 29.00
        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits 		// -100.00
		;
        
        assertEquals( 232.00, total, 0.0001 );
        
    }    
    
 
    
    // only amountIncVat => calculate mean vat split into diffrent tax rates present
    // if we have two orders items with different vat rate, we need to create
    // two discount order rows, one for each vat rate
//    @Test
//    public void testFormatFixedDiscountRowsMixedItemVatSpecAmountIncVatWithDifferentVatRatesPresent() {
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountExVat(100.00)
//                                                                    .setAmountIncVat(125.00)
//                                                                    .setQuantity(2.0))
//                                                   .addOrderRow(WebPayItem.orderRow()
//                                                                    .setAmountIncVat(106.00)
//                                                                    .setVatPercent(6)
//                                                                    .setQuantity(1.0))
//                                                   .addDiscount(WebPayItem.fixedDiscount()
//                                                                    .setDiscountId("42")
//                                                                    .setName(".setAmountIncVat(100)")
//                                                                    .setDescription("testFormatFixedDiscountRowsWithDifferentVatRatesPresent")
//                                                                    .setAmountIncVat(100)
//                                                                    .setUnit("st"));
//
//        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
//
//
//        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
//        SveaOrderRow newRow = newRows.get(2);
//        assertEquals("42", newRow.ArticleNumber);
//        assertEquals(".setAmountIncVat(100): testFormatFixedDiscountRowsWithDifferentVatRatesPresent (25%)", newRow.Description);
//        assertEquals(-56.18, newRow.PricePerUnit, 0);
//        assertEquals(25, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//
//        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
//        newRow = newRows.get(3);
//        assertEquals("42", newRow.ArticleNumber);
//        assertEquals(".setAmountIncVat(100): testFormatFixedDiscountRowsWithDifferentVatRatesPresent (6%)", newRow.Description);
//        assertEquals(-28.09, newRow.PricePerUnit, 0);
//        assertEquals(6, newRow.VatPercent, 0);
//        assertEquals(0, newRow.DiscountPercent, 0);
//        assertEquals(1, newRow.NumberOfUnits, 0);
//        assertEquals("st", newRow.Unit);
//    }


    // amountIncVat and vatPercent => add as one row with specified vat rate only
    @Test
    public void testFormatFixedDiscountRowsAmountIncVatAndVatPercentWithSingleVatRatePresent() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(4.0)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("0")
                                                                    .setName(".setAmountExVat(4.0), .setVatPercent(25)")
                                                                    .setDescription("testFormatFixedDiscountRowsamountIncVatAndVatPercentWithSingleVatRatePresent")
                                                                    .setAmountIncVat(1.0)
                                                                    .setVatPercent(25)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        SveaOrderRow newRow = newRows.get(1);
        assertEquals("0", newRow.ArticleNumber);
        assertEquals(".setAmountExVat(4.0), .setVatPercent(25): testFormatFixedDiscountRowsamountIncVatAndVatPercentWithSingleVatRatePresent", newRow.Description);
        assertEquals(-0.8, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    // amountIncVat and vatPercent => add as one row with specified vat rate only
    @Test
    public void testFormatFixedDiscountRowsAmountIncVatAndVatPercentWithDifferentVatRatesPresent() {
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
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(111), .vatPercent(25)")
                                                                    .setDescription("testFormatFixedDiscountRowsamountIncVatAndVatPercentWithDifferentVatRatesPresent")
                                                                    .setAmountIncVat(111)
                                                                    .setVatPercent(25)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();


        // 100 @25% vat = -80 excl. vat
        SveaOrderRow newRow = newRows.get(2);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setAmountIncVat(111), .vatPercent(25): testFormatFixedDiscountRowsamountIncVatAndVatPercentWithDifferentVatRatesPresent", newRow.Description);
        assertEquals(-88.80, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    // amountExVat and vatPercent => add as one row with specified vat rate only
    @Test
    public void testFormatFixedDiscountRowsAmountExVatAndVatPercentWithSingleVatRatePresent() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(4.0)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("0")
                                                                    .setName("Tess")
                                                                    .setDescription("Tester")
                                                                    .setAmountExVat(1.0)
                                                                    .setVatPercent(25)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        SveaOrderRow newRow = newRows.get(1);
        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(-1.0, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    // amountExVat and vatPercent => add as one row with specified vat rate only
    @Test
    public void testFormatFixedDiscountRowsAmountExVatAndVatPercentWithDifferentVatRatesPresent() {
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
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("testFormatFixedDiscountRowsWithDifferentVatRatesPresent")
                                                                    .setAmountExVat(111)
                                                                    .setVatPercent(25)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();


        // 100 @25% vat = -80 excl. vat
        SveaOrderRow newRow = newRows.get(2);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setAmountIncVat(100): testFormatFixedDiscountRowsWithDifferentVatRatesPresent", newRow.Description);
        assertEquals(-111.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    @Test
    public void testFormatRelativeDiscountRowsWithSingleVatRatePresent() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(12)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.relativeDiscount()
                                                                    .setDiscountId("0")
                                                                    .setName(".setDiscountPercent(20)")
                                                                    .setDescription("testFormatRelativeDiscountRowsWithSingleVatRatePresent")
                                                                    .setDiscountPercent(20.0)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        SveaOrderRow newRow = newRows.get(1);
        assertEquals("0", newRow.ArticleNumber);
        assertEquals(".setDiscountPercent(20): testFormatRelativeDiscountRowsWithSingleVatRatePresent", newRow.Description);
        assertEquals(-20.00, newRow.PricePerUnit, 0);
        assertEquals(12, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayWebPayItem...
        assertEquals(1, newRow.NumberOfUnits, 0);
        assertEquals("st", newRow.Unit);
    }

    // if we have two orders items with different vat rate, we need to create
    // two discount order rows, one for each vat rate
    @Test
    public void testFormatRelativeDiscountRowsWithDifferentVatRatesPresent() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addDiscount(WebPayItem.relativeDiscount()
                                                                    .setDiscountId("42")
                                                                    .setName(".setDiscountPercent(10)")
                                                                    .setDescription("testFormatRelativeDiscountRowsWithDifferentVatRatesPresent")
                                                                    .setDiscountPercent(10.0)
                                                                    .setUnit("st"));

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();


        SveaOrderRow newRow = newRows.get(2);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setDiscountPercent(10): testFormatRelativeDiscountRowsWithDifferentVatRatesPresent (25%)", newRow.Description);
        assertEquals(-20.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
        assertEquals("st", newRow.Unit);

        newRow = newRows.get(3);
        assertEquals("42", newRow.ArticleNumber);
        assertEquals(".setDiscountPercent(10): testFormatRelativeDiscountRowsWithDifferentVatRatesPresent (6%)", newRow.Description);
        assertEquals(-10.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0); // not the same thing as in our WebPayItem...
        assertEquals(1, newRow.NumberOfUnits, 0); // 1 "discount unit"
        assertEquals("st", newRow.Unit);
    }


    private double convertExVatToIncVat(double amountExVat, double vatPercent) {
    	return amountExVat * (1+vatPercent/100);
    }

}


