package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.BuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;

public class BuildOrderRow implements BuilderCommand {
    
    public OrderBuilder run(OrderBuilder orderBuilder) {
/*        return orderBuilder.beginOrderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .endRow();*/
        return null;
    }
}
