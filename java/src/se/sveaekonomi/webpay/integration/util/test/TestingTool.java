package se.sveaekonomi.webpay.integration.util.test;

import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;

public class TestingTool {
    
    public static OrderRowBuilder createOrderRow() {
        return Item.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2)
                .setUnit("st")
                .setVatPercent(25)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createOrderRowDe() {
        return Item.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2)
                .setUnit("st")
                .setVatPercent(19)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createOrderRowNl() {
        return Item.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2)
                .setUnit("st")
                .setVatPercent(6)
                .setVatDiscount(0); 
    }
}
