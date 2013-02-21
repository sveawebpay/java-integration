package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.BuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class BuildShippingFeeRow implements BuilderCommand {
    
    public OrderBuilder newRun(CreateOrderBuilder orderBuilder) {
        return orderBuilder.addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
       /* return orderBuilder
                .beginShippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .endRow();*/
    }

    public OrderBuilder run(OrderBuilder order) {        
        return newRun((CreateOrderBuilder)order);
    }

   

}
