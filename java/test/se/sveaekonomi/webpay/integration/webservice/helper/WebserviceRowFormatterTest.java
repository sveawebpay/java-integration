package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class WebserviceRowFormatterTest {
    
    @Test
    public void testFormatOrderRows() {
        OrderRowBuilder row = new OrderRowBuilder();
        row.setArticleNumber("0")
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1)
            .setUnit("st");
        ArrayList<OrderRowBuilder> rows = new ArrayList<OrderRowBuilder>();
        rows.add(row);
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(row);
        //order.setOrderRows(rows);
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertTrue(4.0 == newRow.PricePerUnit);
        assertTrue(25.0 == newRow.VatPercent);
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
    
    @Test
    public void testFormatShippingFeeRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow())
        .addFee(Item.shippingFee()  
            .setShippingId("0")
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setUnit("st"));        
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertEquals("0", newRow.ArticleNumber);
        assertEquals("Tess: Tester", newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0,newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatInvoiceFeeRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();        
        order.addFee(Item.invoiceFee()       
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setUnit("st"));        
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(0);

        assertTrue("".equals(newRow.ArticleNumber));
        assertEquals("Tester",newRow.Description);
        assertEquals(4.0, newRow.PricePerUnit, 0);
        assertEquals(25.0, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent);
        assertEquals(1, newRow.NumberOfUnits);
        assertEquals("st", newRow.Unit);
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow()
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1))
        .addDiscount(Item.fixedDiscount()
            .setDiscountId("0")
            .setName("Tess")
            .setDescription("Tester")
           // .setDiscount(1)
            .setAmountIncVat(1)
            .setUnit("st"));
         
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertEquals(-0.8,newRow.PricePerUnit, 0);
        assertTrue(25.0 == newRow.VatPercent);
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow()
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1))
        .addDiscount(Item.relativeDiscount()
            .setDiscountId("0")
            .setName("Tess")
            .setDescription("Tester")
            .setDiscountPercent(10)
            .setUnit("st"));
        
        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();
        SveaOrderRow newRow = newRows.get(1);

        assertTrue("0".equals(newRow.ArticleNumber));
        assertTrue("Tess: Tester".equals(newRow.Description));
        assertTrue(-0.4 == newRow.PricePerUnit);
        assertTrue(25 == newRow.VatPercent); //?
        assertTrue(0 == newRow.DiscountPercent);
        assertTrue(1 == newRow.NumberOfUnits);
        assertTrue("st".equals(newRow.Unit));
    }
}
