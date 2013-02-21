package se.sveaekonomi.webpay.integration.order.handle;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HandleOrderValidator;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;

public class DeliverOrderBuilder extends OrderBuilder<DeliverOrderBuilder> {
    private HandleOrderValidator validator;
    
    private long orderId;
    private String orderType;
    private String distributionType;
    private String invoiceIdToCredit;
    private Integer numberOfCreditDays;
    public final SveaConfig config = new SveaConfig();
    
    public DeliverOrderBuilder() {
        
    }

    public enum DistributionType {
        Post, Email 
    }
    public HandleOrderValidator getValidator() {
        return validator;
    }
    
    public DeliverOrderBuilder setValidator(HandleOrderValidator validator) {
        this.validator = validator;
        return this;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public DeliverOrderBuilder setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getInvoiceDistributionType() {
        return distributionType;
    }
    
    public DeliverOrderBuilder setInvoiceDistributionType(DistributionType type) {
        this.distributionType = type.toString();
        return this;
    }
    
/*    public DeliverOrderBuilder setInvoiceDistributionTypeAsEmail() {
        this.distributionType = "Email";
        return this;
    }*/
    
    public String getInvoiceIdToCredit() {
        return invoiceIdToCredit;
    }
    
    public DeliverOrderBuilder setInvoiceIdToCredit(String invoiceIdToCredit) {
        this.invoiceIdToCredit = invoiceIdToCredit;
        return this;
    }

    public Integer getNumberOfCreditDays() {
        return numberOfCreditDays;
    }
    
    public DeliverOrderBuilder setNumberOfCreditDays(int numberOfCreditDays) {
        this.numberOfCreditDays = numberOfCreditDays;
        return this;
    }
    
    public HandleOrder deliverInvoiceOrder() throws ValidationException {
        orderType = "Invoice";
        return new HandleOrder(this);
    }

    public HandleOrder deliverPaymentPlanOrder() throws ValidationException {
        orderType = "PaymentPlan";
        return new HandleOrder(this);
    }
}
