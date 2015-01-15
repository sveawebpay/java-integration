package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;

/**
 * @author Kristian Grossman-Madsen
 */
public class WebPayAdmin {
	

    /**
     * The WebPayAdmin.cancelOrder() entrypoint method is used to cancel an order with Svea, 
     * that has not yet been delivered (invoice, payment plan) or confirmed (card).
     * 
     * Supports Invoice, Payment Plan and Card orders. For Direct Bank orders, use WebPayAdmin.creditOrderRows() instead.
     *  
     * Get an instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send 
     * the request using the CancelOrderBuilder methods:
     * 
     * ...
     *     request = WebPayAdmin.cancelOrder(config)
     *          .setOrderId()		// required, use SveaOrderId recieved with createOrder response
     *          .setTransactionId	// optional, card or direct bank only, alias for setOrderId 
     *          .setCountryCode()	// required, use same country code as in createOrder request      
     *     ;
     *     // then select the corresponding request class and send request
     *     response = request.cancelInvoiceOrder().doRequest();		// returns CloseOrderResponse
     *     response = request.cancelPaymentPlanOrder().doRequest();	// returns CloseOrderResponse
     *     response = request.cancelCardOrder().doRequest();		// returns AnnulTransactionResponse
     * ...
     * 
	 */
	public static CancelOrderBuilder cancelOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }    	
        
        return new CancelOrderBuilder(config);
	}
	
	/**
	 * The WebPayAdmin.queryOrder entrypoint method is used to get information about an order.
     * 
     * Note that for invoice and payment plan orders, the order rows name and description is merged into the 
     * description field in the query response.
     * 
     * Get an instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and 
     * send the request using the QueryOrderBuilder methods:
     * 
     * ...
     *     request = WebPayAdmin.queryOrder(config)
     *          .setOrderId()          // required
     *          .setTransactionId()	   // optional, card or direct bank only, alias for setOrderId 
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

	/**
     * The WebPayAdmin.deliverOrderRows entrypoint method is used to deliver individual order rows. 
     * 1.6.0: Supports card orders. To deliver invoice order rows, use WebPay.deliverOrder with specified order rows.
     * 
	 * For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.
	 * 
	 * For card orders, an order can only be delivered once, and any non-delivered order rows will be cancelled (i.e. 
	 * the order amount will be lowered by the sum of the non-delivered order rows). A delivered card order has status 
	 * CONFIRMED at Svea.
	 * 
     * Get an order builder instance using the WebPayAdmin.deliverOrderRows entrypoint,
     * then provide more information about the transaction and send the request using
     * the DeliverOrderRowsBuilder methods:
	 * 
	 * Use setRowToDeliver() or setRowsToDeliver() to specify the order row(s) to deliver. The order row indexes should 
	 * correspond to those returned by i.e. WebPayAdmin.queryOrder();
	 * 
	 * For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order 
	 * rows. The original order rows can be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute 
	 * contains the serverside order rows w/indexes. Note that if a card order has been modified (i.e. rows cancelled 
	 * or credited) after the initial order creation, the returned order rows will not be accurate.
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
     * For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.
     * 
     * For card orders, the request can only be sent once, and if all original order rows are cancelled, the order then receives status ANNULLED at Svea.
     * 
     * Get an order builder instance using the WebPayAdmin.cancelOrderRows entrypoint, then provide more information about the transaction and send the 
     * request using the CancelOrderRowsBuilder methods:
     * 
     * Use setRowToCancel() or setRowsToCancel() to specify the order row(s) to cancel. The order row indexes should correspond to those returned by 
     * i.e. WebPayAdmin.queryOrder();
     * 
     * For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order rows. The original order rows can 
     * be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute contains the serverside order rows w/indexes. Note that if a card 
     * order has been modified (i.e. rows cancelled or credited) after the initial order creation, the returned order rows will not be accurate.
     *
     * ...
     * 		request = WebPayAdmin.cancelOrderRows(config)
     *          .setOrderId()          			// required
     *          .setTransactionId()	   			// optional, card only, alias for setOrderId 
     *          .setCountryCode()      			// required    	
     *          .setRowToCancel()	   			// required, index of original order rows you wish to cancel 
     *          .addNumberedOrderRow()			// required for card orders, should match original row indexes 
     *     	;
     *     	// then select the corresponding request class and send request
     *     	response = request.deliverCardOrderRows().doRequest()	// returns LowerTransactionResponse
     * ...
     * 
     */    
    public static CancelOrderRowsBuilder cancelOrderRows( ConfigurationProvider config ) {
        if (config == null) {
        	throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
	    }    	
	    
	    return new CancelOrderRowsBuilder(config);	    	
    }

    /**
     * The WebPayAdmin.creditOrderRows entrypoint method is used to credit rows in an order after it has been delivered.
     * 1.6.0: Supports card and direct bank orders.
     * 
     * ...
     *     request = WebPay.creditOrder(config)
     *         .setInvoiceId()                // invoice only, required
     *         .setInvoiceDistributionType()  // invoice only, required
     *         .setOrderId()                  // card and direct bank only, required
     *         .setCountryCode()              // required
     *         .addCreditOrderRow()           // optional, use to specify a new credit row, i.e. for amounts not present in the original order
     *         .addCreditOrderRows()          // optional
     *         .setRowToCredit()              // optional, index of one of the original order row you wish to credit
     *         .setRowsToCredit()             // optional
     *         .addNumberedOrderRow()         // card and direct bank only, required with setRowToCredit()
     *         .addNumberedOrderRows()        // card and direct bank only, optional
     *     ;
     *     // then select the corresponding request class and send request
     *     response = request.creditCardOrderRows().doRequest();       // returns CreditTransactionResponse
     *     response = request.creditDirectBankOrderRows().doRequest(); // returns CreditTransactionResponse
     * ...
     * 
     * @author Kristian Grossman-Madsen
     */
    public static CreditOrderRowsBuilder creditOrderRows( ConfigurationProvider config ) {
        if (config == null) {
        	throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
	    }    	
	    
	    return new CreditOrderRowsBuilder(config);	    	
    }
}
