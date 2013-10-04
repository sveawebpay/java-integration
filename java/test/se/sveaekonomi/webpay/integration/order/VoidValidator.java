package se.sveaekonomi.webpay.integration.order;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.OrderValidator;

public class VoidValidator extends OrderValidator {

    public int noOfCalls = 0;
    
    public int getNoOfCalls() {
        return noOfCalls;
    }
    
    public String validate(CreateOrderBuilder order) {
        errors = "";
        noOfCalls++;
        return "";
    }
}
