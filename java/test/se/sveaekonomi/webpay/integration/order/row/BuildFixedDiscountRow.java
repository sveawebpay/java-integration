package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.BuilderCommand;
import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class BuildFixedDiscountRow implements BuilderCommand {
   
    public OrderBuilder run(OrderBuilder order) {
      /*  return order
                .beginFixedDiscount()
                .setName("Fixed")
                .setDescription("FixedDiscount")                
                .setUnit("st")
                .setDiscountId("1")
                .setDiscount((double) 100.00)
                .endRow();*/
        return null;
    }
}
