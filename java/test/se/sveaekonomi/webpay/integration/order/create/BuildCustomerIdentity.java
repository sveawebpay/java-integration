package se.sveaekonomi.webpay.integration.order.create;

import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class BuildCustomerIdentity implements CreateBuilderCommand<CreateOrderBuilder> {
    
    public CreateOrderBuilder run(CreateOrderBuilder orderBuilder) {
        return orderBuilder.addCustomerDetails(TestingTool.createIndividualCustomer());
    }
}
