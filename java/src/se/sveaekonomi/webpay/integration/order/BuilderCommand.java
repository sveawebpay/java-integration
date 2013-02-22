package se.sveaekonomi.webpay.integration.order;

public interface BuilderCommand<T extends OrderBuilder<T>> {
    
    T run(OrderBuilder<T> order);
}
