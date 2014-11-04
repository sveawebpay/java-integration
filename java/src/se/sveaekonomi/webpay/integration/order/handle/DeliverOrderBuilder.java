package se.sveaekonomi.webpay.integration.order.handle;

import java.util.Date;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.adminservice.request.DeliverOrdersRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HandleOrderValidator;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;

/**
 * @author klar-sar, Kristian Grossman-Madsen
 */
public class DeliverOrderBuilder extends OrderBuilder<DeliverOrderBuilder> {

    private HandleOrderValidator validator;
    
    private long orderId;
    private String orderType;
    private String distributionType;
    private String invoiceIdToCredit;
    private Integer numberOfCreditDays;
    private String captureDate;
    
    public DeliverOrderBuilder(ConfigurationProvider config) {
        this.config = config;
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
    
    public DeliverOrderBuilder setInvoiceDistributionType(DISTRIBUTIONTYPE type) {
        this.distributionType = type.toString();	// TODO use enum instead
        return this;
    }
    
    public String getCreditInvoice() {
        return invoiceIdToCredit;
    }
    
    public DeliverOrderBuilder setCreditInvoice(String invoiceId) {
        this.invoiceIdToCredit = invoiceId;
        return this;
    }
    
    public Integer getNumberOfCreditDays() {
        return numberOfCreditDays;
    }
    
    public DeliverOrderBuilder setNumberOfCreditDays(int numberOfCreditDays) {
        this.numberOfCreditDays = numberOfCreditDays;
        return this;
    }
    
	/**
	 * card only, optional
	 * @param date -- date on format YYYY-MM-DD (similar to ISO8601 date)
	 * @return DeliverOrderBuilder
	 */
	public DeliverOrderBuilder setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
		return this;
	}
	
    public String getCaptureDate() {
        return captureDate;
    }
    
	/**
	 * card only, optional -- alias for setOrderId
	 * @param transactionId
	 * @return DeliverOrderBuilder
	 */
    public DeliverOrderBuilder setTransactionId(long transactionId) {        
        return setOrderId( transactionId );
    }
    
    public long getTransactionId() {
        return getOrderId();
    }
    
    
    /**
     * Updates the order builder with additional information and passes the
     * order builder to the correct request class.
     * @return Requestable
     */
    public <T extends Requestable> T deliverInvoiceOrder() {

    	if( this.orderRows.isEmpty() ) {
	    	this.orderType = "Invoice";		// TODO use enumeration instead
    		return (T) new DeliverOrdersRequest(this);
    	}
	    else {
	    	this.orderType = "Invoice";
	        return (T) new HandleOrder(this);
	    }
    }
    
    /**
     * Prepares the PaymentPlan order for delivery.
     * @return HandleOrder
     */
    public Requestable deliverPaymentPlanOrder() {
    	this.orderType = "PaymentPlan";		// TODO use enumeration instead
		return new DeliverOrdersRequest(this);
    }

    
    /**
     * deliverCardOrder() is used to set the status of a card order to CONFIRMED
     * 
     * A default capturedate equal to the current date will be supplied. This 
     * may be overridden using the ConfirmTransaction setCaptureDate() method 
     * on the returned ConfirmTransaction object.
     * 
     * @return DeliverPaymentPlan
     */    
	public Requestable deliverCardOrder() {
		this.orderType = "HOSTED"; // TODO use enumeration instead
		
        // if no captureDate set, use today's date as default.
        if( this.getCaptureDate() == null ) {
        	this.setCaptureDate( String.format("%tFT%<tRZ", new Date()) );
        }
		
		return new ConfirmTransactionRequest(this);
	}

}
