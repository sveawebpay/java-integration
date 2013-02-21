package se.sveaekonomi.webpay.integration.order.handle;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;

public class CloseOrderBuilder {
    
    private Long orderId;
    private String orderType;
    private boolean testmode;
    public final SveaConfig config = new SveaConfig();
    
    public Long getOrderId() {
        return orderId;
    }
    
    public CloseOrderBuilder setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public CloseOrderBuilder setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }

    public boolean getTestmode() {
        return testmode;
    }

    public CloseOrderBuilder setTestmode() {
        this.testmode = true;
        return this;
    }
    
    public CloseOrder closeInvoiceOrder() {
        orderType = "Invoice";
        return new CloseOrder(this);
    }

    public CloseOrder closePaymentPlanOrder() {
        orderType = "PaymentPlan";
        return new CloseOrder(this);
    }
}
