package se.sveaekonomi.webpay.integration.order.handle;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.QueryTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */
public class QueryOrderBuilder extends OrderBuilder<QueryOrderBuilder>{
	
    protected ConfigurationProvider config;
    protected COUNTRYCODE countryCode;
        
    public ConfigurationProvider getConfig() {
        return this.config;
    }
    
    private long orderId;


	public QueryOrderBuilder(ConfigurationProvider config) {
		this.config = config;
	}
    
	
    /**
     * required
     */	
    public QueryOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public COUNTRYCODE getCountryCode() {
        return this.countryCode;
    } 
    
    /**
     * required, invoice or payment plan only, order to get details for
     */
    public QueryOrderBuilder setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }
    public long getOrderId() {
        return orderId;
    }
    
	/**
	 * optional, card or direct bank only -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return DeliverOrderBuilder
	 */
    public QueryOrderBuilder setTransactionId( String transactionId) {        
        return setOrderId( Long.parseLong(transactionId) );
    }   
    public long getTransactionId() {
        return getOrderId();
    }


	public Requestable queryCardOrder() {		
		return new QueryTransactionRequest(this);
	}	
}
