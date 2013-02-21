package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.BuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;

public class BuildInvoiceFeeRow implements BuilderCommand {
    
    public OrderBuilder run(OrderBuilder order) {
  /*      return order
                .beginInvoiceFee()
                .setName("Svea fee")
                .setDescription("Fee for invoice")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .endRow();*/
        return null;
    }
}
