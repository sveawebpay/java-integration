package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedRowFormatterTest {
    
    @Test
    public void testFormatOrderRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(Item.orderRow()
                .setArticleNumber("0")
                .setName("Tess")
                .setDescription("Tester")
                .setAmountExVat(4)
                .setVatPercent(25)
                .setQuantity(1.0)
                .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);
        
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
                .addFee(Item.shippingFee()
                .setShippingId("0")
                .setName("Tess")
                .setDescription("Tester")
                .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatShippingFeeRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addFee(Item.shippingFee()
                        .setAmountExVat(4)
                        .setVatPercent(25));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(0);
        
        assertTrue(500L == newRow.getAmount());
        assertTrue(100L == newRow.getVat());
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(Item.fixedDiscount()
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);
        
        assertEquals("0", newRow.getSku());
        assertEquals("Tess", newRow.getName());
        assertEquals("Tester", newRow.getDescription());
        assertEquals(1, newRow.getQuantity(), 0);
        assertEquals("st", newRow.getUnit());
    }
    
    @Test
    public void testFormatFixedDiscountRowsAmount() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(Item.fixedDiscount()
                        .setAmountIncVat(4));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(0);
        
        assertEquals(-400L, (long)newRow.getAmount());
    }
    
    @Test
    public void testFormatFixedDiscountRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addDiscount(Item.fixedDiscount()
                        .setAmountIncVat(1)
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertEquals(-100L, (long)newRow.getAmount());
        assertEquals(-20L, (long)newRow.getVat());
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addDiscount(Item.relativeDiscount()
                        .setDiscountId("0")
                        .setName("Tess")
                        .setDescription("Tester")
                        .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);
        
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
                .addDiscount(Item.relativeDiscount()
                        .setDiscountPercent(10.0));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertEquals(-50L, (long)newRow.getAmount());
    }
    
    @Test
    public void testFormatRelativeDiscountRowsVat() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addDiscount(Item.relativeDiscount()
                        .setDiscountPercent(10.0));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertEquals(-50L, (long)newRow.getAmount());
        assertEquals(-10L, (long)newRow.getVat());
    }
    
    @Test
    public void testFormatTotalAmount() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setAmount(100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(200L, new HostedRowFormatter().formatTotalAmount(rows));
    }
    
    @Test
    public void testFormatTotalVat() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(200L, new HostedRowFormatter().formatTotalVat(rows));
    }
    
    @Test
    public void testFormatTotalVatNegative() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(-100L)
            .setQuantity(2.0);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertEquals(-200L, new HostedRowFormatter().formatTotalVat(rows));
    }
}
