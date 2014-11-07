package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;

/**
 * @author Kristian Grossman-Madsen
 */
public class WebPayAdmin {
	
	// TODO javadoc
	public static CancelOrderBuilder cancelOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }    	
        
        return new CancelOrderBuilder(config);
	}
	
	/**
	 * TODO check below javadoc
     * The WebPayAdmin.queryOrder entrypoint method is used to get information about an order.
     * 
     * Note that for invoice and payment plan orders, the order rows name and description is merged into the description field in the query response.
     * 
     * Get an query builder instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send the request using the QueryOrderBuilder methods:
     * 
     * ...
     *     response = WebPayAdmin.queryOrder(config)
     *          .setOrderId()          // required
     *          .setCountryCode()      // required      
     *          .queryInvoiceOrder()   // select request class and
     *              .doRequest()       // perform the request, returns GetOrdersResponse
     * 
     *          //.queryPaymentPlanOrder().doRequest() // returns GetOrdersResponse
     *          //.queryCardOrder().doRequest()        // returns QueryTransactionResponse
     *          //.queryDirectBankOrder().doRequest()  // returns QueryTransactionResponse
     *     ;
     * ...
     * 
	 */
	public static QueryOrderBuilder queryOrderBuilder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }    	
        
        return new QueryOrderBuilder(config);		
	}

}
