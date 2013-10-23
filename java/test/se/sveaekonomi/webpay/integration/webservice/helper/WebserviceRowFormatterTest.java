package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebserviceRowFormatterTest {

    @Test
    public void testFormatOrderRows() {
        CreateOrderBuilder order = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("0")
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1)
            .setUnit("st"));
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);
        
        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatShippingFeeRows() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                    .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                    .addFee(Item.shippingFee()
                        .setShippingId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setUnit("st"))
                    .addCustomerDetails(Item.individualCustomer()
                        .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                    .setCountryCode(TestingTool.DefaultTestCountryCode)
                    .setOrderDate("2012-12-12")
                    .setClientOrderNumber("33")
                    .setCurrency(TestingTool.DefaultTestCurrency)
                    .setCustomerReference("33")
                    .useInvoicePayment()
                    .prepareRequest();
        
        assertEquals("0", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("Tess: Tester", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(4.0, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(25.0, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
    }
    
    @Test
    public void testFormatInvoiceFeeRows() {
        CreateOrderBuilder order = WebPay.createOrder()
            .addFee(Item.invoiceFee()
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
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder()
            .addOrderRow(TestingTool.createMiniOrderRow())
            .addDiscount(Item.fixedDiscount()
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
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder()
        .addOrderRow(TestingTool.createMiniOrderRow())
        .addDiscount(TestingTool.createRelativeDiscount());
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);
        
        assertEquals("1", newRow.ArticleNumber);
        assertEquals("Relative: RelativeDiscount", newRow.Description);
        assertEquals(-2.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
}
