package se.sveaekonomi.webpay.integration.order.handle;

import java.net.URL;

import se.sveaekonomi.webpay.integration.config.Config;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;

public class CloseOrderBuilder {
    
    private Long orderId;
    private String orderType;
    public final SveaConfig config = new SveaConfig();
    
    private Config configMode;
    
    public CloseOrderBuilder(Config configMode) {
    	this.configMode = configMode;
    }
    
    public URL getPayPageUrl() {
    	return this.configMode.getPayPageUrl();
    }
    
    public URL getWebserviceUrl() {
    	return this.configMode.getWebserviceUrl();
    }
    
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

    public CloseOrder closeInvoiceOrder() {
        orderType = "Invoice";
        return new CloseOrder(this);
    }

    public CloseOrder closePaymentPlanOrder() {
        orderType = "PaymentPlan";
        return new CloseOrder(this);
    }
}
