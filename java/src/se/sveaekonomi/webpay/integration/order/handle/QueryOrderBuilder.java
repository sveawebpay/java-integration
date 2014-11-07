package se.sveaekonomi.webpay.integration.order.handle;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */
public class QueryOrderBuilder {
	
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
	 * card or direct bank only, optional -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return DeliverOrderBuilder
	 */
    public QueryOrderBuilder setTransactionId( String transactionId) {        
        return setOrderId( Long.parseLong(transactionId) );
    }   
    public long getTransactionId() {
        return getOrderId();
    }	
}
