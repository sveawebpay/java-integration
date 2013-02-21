package se.sveaekonomi.webpay.integration.webservice.svea_soap;


public class SveaDeliverOrderInformation {
    
    public String sveaOrderId;
    public String orderType;
    public SveaDeliverInvoiceDetails deliverInvoiceDetails;
    
    public SveaDeliverOrderInformation(String orderType) {
        this.orderType = orderType;
        if(this.orderType == "Invoice")
        this.deliverInvoiceDetails = new SveaDeliverInvoiceDetails();
    }
    
    public String getOrderId() {
        return sveaOrderId;
    }
    
    public void setOrderId(String orderId) {
        this.sveaOrderId = orderId;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public SveaDeliverInvoiceDetails getDeliverInvoiceDetails() {
        return this.deliverInvoiceDetails;
    }
    
    public void setDeliverInvoiceDetails(SveaDeliverInvoiceDetails deliverInvoiceDetails) {
        this.deliverInvoiceDetails = deliverInvoiceDetails;
    }
}
