package se.sveaekonomi.webpay.integration.order;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public interface CreateBuilderCommand<T> {
    
    T run(CreateOrderBuilder order);
}
