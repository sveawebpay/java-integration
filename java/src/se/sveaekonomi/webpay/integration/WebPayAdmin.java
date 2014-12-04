package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;

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
	
	// TODO check below javadoc
	/**
	 * The WebPayAdmin.queryOrder entrypoint method is used to get information about an order.
     * 
     * Note that for invoice and payment plan orders, the order rows name and description is merged into the description field in the query response.
     * 
     * Get an query builder instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send the request using the QueryOrderBuilder methods:
     * 
     * ...
     *     request = WebPayAdmin.queryOrder(config)
     *          .setOrderId()          // required
     *          .setTransactionId	   // optional, card or direct bank only, alias for setOrderId 
     *          .setCountryCode()      // required      
     *     ;
     *     // then select the corresponding request class and send request
     *     response = request.queryInvoiceOrder().doRequest();		// returns GetOrdersResponse
     *     response = request.queryPaymentPlanOrder().doRequest(); 	// returns GetOrdersResponse
     *     response = request.queryCardOrder().doRequest();        	// returns QueryTransactionResponse
     *     response = request.queryDirectBankOrder().doRequest();  	// returns QueryTransactionResponse
     * ...
     * 
	 */
	public static QueryOrderBuilder queryOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }    	
        
        return new QueryOrderBuilder(config);		
	}

	// TODO check below javadoc, backport docblock to php
	/**
     * The WebPayAdmin.deliverOrderRows entrypoint method is used to deliver individual order rows.
     * 1.6.0: Supports card orders. To deliver invoice order rows, use WebPay.deliverOrder with specified order rows.
     * 
     * Get an order builder instance using the WebPayAdmin.deliverOrderRows entrypoint,
     * then provide more information about the transaction and send the request using
     * the DeliverOrderRowsBuilder methods:
     *
     * ...
     * 		request = WebPayAdmin.deliverOrderRows(config)
     *          .setOrderId()          			// required
     *          .setTransactionId()	   			// optional, card only, alias for setOrderId 
     *          .setCountryCode()      			// required    	
     *          .setInvoiceDistributionType()	// required, invoice only
     *          .setRowToDeliver()	   			// required, index of original order rows you wish to deliver 
     *          .addNumberedOrderRow()			// required for card orders, should match original row indexes 
     *     	;
     *     	// then select the corresponding request class and send request
     *     	response = request.deliverCardOrderRows().doRequest()	// returns ConfirmTransactionResponse
     * ...
     * 
     */
    public static DeliverOrderRowsBuilder deliverOrderRows( ConfigurationProvider config ) {
        if (config == null) {
        	throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
	    }    	
	    
	    return new DeliverOrderRowsBuilder(config);	
    }    

    
	/**
     * The WebPayAdmin.cancelOrderRows entrypoint method is used to cancel rows in an order before it has been delivered.
     * 1.6.0: Supports card orders.
     * 
     * Get an order builder instance using the WebPayAdmin.cancelOrderRows entrypoint,
     * then provide more information about the transaction and send the request using
     * the CancelOrderRowsBuilder methods:
     *
     * ...
     * 		request = WebPayAdmin.cancelOrderRows(config)
     *          .setOrderId()          			// required
     *          .setTransactionId()	   			// optional, card only, alias for setOrderId 
     *          .setCountryCode()      			// required    	
     *          .setRowToCancel()	   			// required, index of original order rows you wish to deliver 
     *          .addNumberedOrderRow()			// required for card orders, should match original row indexes 
     *     	;
     *     	// then select the corresponding request class and send request
     *     	response = request.deliverCardOrderRows().doRequest()	// returns ConfirmTransactionResponse
     * ...
     * 
     */    
    public static CancelOrderRowsBuilder cancelOrderRows( ConfigurationProvider config ) {
        if (config == null) {
        	throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
	    }    	
	    
	    return new CancelOrderRowsBuilder(config);	    	
    }
}
