package se.sveaekonomi.webpay.integration.order.handle;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.adminservice.GetOrdersRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.QueryTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHODTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public class QueryOrderBuilder extends OrderBuilder<QueryOrderBuilder>{
	
    /** Required. */
    protected ConfigurationProvider config;
    /** Required. */
    private Long orderId;
    /** Required. */
    protected COUNTRYCODE countryCode;
    /** Required. */
    protected PAYMENTTYPE orderType;

    public ConfigurationProvider getConfig() {
        return this.config;
    }

    /** Required */	
    public QueryOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public COUNTRYCODE getCountryCode() {
        return this.countryCode;
    } 
    
    /** Required, invoice or payment plan only, order to get details for */
    public QueryOrderBuilder setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }
    public Long getOrderId() {
        return orderId;
    }
    
	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	} 
    
	/**
	 * Optional, card or direct bank only -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return QueryOrderBuilder
	 */
    public QueryOrderBuilder setTransactionId( String transactionId) {        
        return setOrderId( Long.parseLong(transactionId) );
    }   
    public Long getTransactionId() {
        return getOrderId();
    }

	public QueryOrderBuilder(ConfigurationProvider config) {
		this.config = config;
	}
        
	public QueryTransactionRequest queryCardOrder() {	
		
		// validate request and throw exception if validation fails
        String errors = validateQueryCardOrder();        
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
				
		QueryTransactionRequest request = (QueryTransactionRequest) new QueryTransactionRequest(this.getConfig())
			.setTransactionId( Long.toString(this.getOrderId()) )
			.setCountryCode( this.getCountryCode() )
		;
		return request;
	}	
	
	// validates  queryCardOrder (querytransactionid) required attributes
    public String validateQueryCardOrder() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        return errors;    
    }
	
    
    public GetOrdersRequest queryInvoiceOrder() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		// validation is done in GetOrdersRequest
		GetOrdersRequest request = new GetOrdersRequest( this );
		return request;        
    }
    
    public GetOrdersRequest queryPaymentPlanOrder() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		// validation is done in GetOrdersRequest
		GetOrdersRequest request = new GetOrdersRequest( this );
		return request;        
    }
  
}
