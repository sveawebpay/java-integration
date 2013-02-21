package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.BuilderCommand;
import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class BuildRelativeDiscountRow implements BuilderCommand  {
    
    public OrderBuilder run(OrderBuilder order) {
     /*   return order
                .beginRelativeDiscount()
                .setDiscountId("1")
                .setDiscountPercent(50)
                .setUnit("st")
                .setName("Name")
                .setDescription("RelativeDiscount")
                .endRow();*/
        return null;
    }
}
