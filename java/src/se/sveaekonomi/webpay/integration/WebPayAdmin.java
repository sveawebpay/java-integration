package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.AddOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;

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
    	verifyConfig( config ); 		
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
    	verifyConfig( config ); 	
        return new QueryOrderBuilder(config);		
	}

	/**
     * The WebPayAdmin.deliverOrderRows entrypoint method is used to deliver individual order rows. 
     * Supports invoice and card orders. (To partially deliver PaymentPlan or Direct Bank orders, please contact Svea.)
     * 
	 * For Invoice orders, the order row status is updated at Svea following each successful request.
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
     *      response = request.deliverInvoiceOrderRows().doRequest();	// returns DeliverOrderRowsResponse
     *     	response = request.deliverCardOrderRows().doRequest()		// returns ConfirmTransactionResponse
     * ...
     * 
     */
    public static DeliverOrderRowsBuilder deliverOrderRows( ConfigurationProvider config ) {
    	verifyConfig( config ); 	
	    return new DeliverOrderRowsBuilder(config);	
    }    
    
	/**
     * The WebPayAdmin.cancelOrderRows entrypoint method is used to cancel rows in an order before it has been delivered.
     * Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported, see CreditOrderRows instead.)
     * 
     * For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.
     * 
     * For card orders, the request can only be sent once, and if all original order rows are cancelled, the order then 
     * receives status ANNULLED at Svea.
     * 
     * Get an order builder instance using the WebPayAdmin.cancelOrderRows entrypoint, then provide more information about 
     * the transaction and send the request using the CancelOrderRowsBuilder methods:
     * 
     * Use setRowToCancel() or setRowsToCancel() to specify the order row(s) to cancel. The order row indexes should 
     * correspond to those returned by i.e. WebPayAdmin.queryOrder();
     * 
     * For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order rows. 
     * The original order rows can be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute contains 
     * the serverside order rows w/indexes. Note that if a card order has been modified (i.e. rows cancelled or credited) 
     * after the initial order creation, the returned order rows will not be accurate.
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
     *     	response = request.cancelInvoiceOrderRows().doRequest();		// returns CancelOrderRowsResponse
     *     	response = request.cancelPaymentPlanOrderRows().doRequest();	// returns CancelOrderRowsResponse
     *     	response = request.cancelCardOrderRows().doRequest()			// returns LowerTransactionResponse
     * ...
     * 
     */    
    public static CancelOrderRowsBuilder cancelOrderRows( ConfigurationProvider config ) {
    	verifyConfig( config ); 	
	    return new CancelOrderRowsBuilder(config);	    	
    }

    /**
     * The WebPayAdmin.creditOrderRows entrypoint method is used to credit rows in an order after it has been delivered.
     * Supports invoice, card and direct bank orders. (To credit a payment plan order, please contact Svea customer service.)
     * 
     * If you wish to credit an amount not present in the original order, use addCreditOrderRow() or addCreditOrderRows() 
     * and supply a new order row for the amount to credit. This is the recommended way to credit a card or direct bank order.
     * 
     * If you wish to credit an invoice order row in full, you can specify the index of the order row to credit using setRowToCredit(). 
     * The corresponding order row at Svea will then be credited. (For card or direct bank orders you need to first query and then 
     * supply the corresponding numbered order rows using the addNumberedOrderRows() method.)
     * 
     * Following the request Svea will issue a credit invoice including the original order rows specified using setRowToCredit(), 
     * as well as any new credit order rows specified using addCreditOrderRow(). For card or direct bank orders, the order row amount
     * will be credited to the customer. 
     * 
     * Note: when using addCreditOrderRows, you may only use WebPayItem::orderRow with price specified as amountExVat and vatPercent.
     * 
     * Get an order builder instance using the WebPayAdmin.creditOrderRows entrypoint, then provide more information about the 
     * transaction and send the request using the creditOrderRowsBuilder methods:     
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
     *     response = request.creditInvoiceOrderRows().doRequest();    // returns CreditInvoiceRowsResponse
     *     response = request.creditCardOrderRows().doRequest();       // returns CreditTransactionResponse
     *     response = request.creditDirectBankOrderRows().doRequest(); // returns CreditTransactionResponse
     * ...
     * 
     * @author Kristian Grossman-Madsen
     */
    public static CreditOrderRowsBuilder creditOrderRows( ConfigurationProvider config ) {
    	verifyConfig( config );		    
	    return new CreditOrderRowsBuilder(config);	    	
    }

    /**
     * The WebPayAdmin.updateOrderRows() method is used to update individual order rows in non-delivered invoice and 
     * payment plan orders. Supports invoice and payment plan orders.
	 *
	 * The order row status of the order is updated at Svea to reflect the updated order rows. If the updated rows' 
	 * order total amount exceeds the original order total amount, an error is returned by the service.
	 * 
	 * Get an order builder instance using the WebPayAdmin.updateOrderRows() entrypoint, then provide more information 
     * about the transaction and send the request using the UpdateOrderRowsBuilder methods:   
	 * 
	 * Use setCountryCode() to specify the country code matching the original create order request.
	 * 
	 * Use addUpdateOrderRow() with a new WebPayItem.numberedOrderRow() object. Add the updated order row attributes 
	 * using the NumberedOrderRowBuilder member functions. Notably, the setRowNumber() method specifies which of the 
	 * original order rows will be replaced, in full, with the new NumberedOrderRow. 
	 * 
	 * Then use either updateInvoiceOrderRows() or updatePaymentPlanOrderRows() to get a request object, which ever 
	 * matches the payment method used in the original order.
	 * 
	 * Calling doRequest() on the request object will send the request to Svea and return an UpdateOrderRowsResponse.
	 * 
     * ...
     *     request = WebPayAdmin.updateOrderRows(config)
     *         .setOrderId()                  // required
     *         .setCountryCode()              // required
     *         .addUpdateOrderRow()           // required, NumberedOrderRowBuilder w/RowNumber attribute matching row index of original order row
     *     ;
     *     // then select the corresponding request class and send request
     *     response = request.updateInvoiceOrderRows().doRequest();     // returns UpdateOrderRowsResponse
     *     response = request.updatePaymentPlanOrderRows().doRequest(); // returns UpdateOrderRowsResponse
     * ...
     * 
     * @author Kristian Grossman-Madsen
     */        
    public static UpdateOrderRowsBuilder updateOrderRows( ConfigurationProvider config ) {
    	verifyConfig( config );
    	return new UpdateOrderRowsBuilder( config );
    }
    
    /**
     * The WebPayAdmin.addOrderRows() method is used to add individual order rows to undelivered or partially delivered
     * invoice and payment plan orders. Supports invoice and payment plan orders.
	 *
	 * The order row status of the order will be updated at Svea to reflect the added order rows following a successful
	 * request. If the new order total amount exceeds the original order total amount, a new credit control is first 
	 * made, which may result in the request being denied. For payment plan orders, the unew order total amount must be
	 * within the original order campaign limits, or the request will be denied.
	 * 
	 * Get an order builder instance using the WebPayAdmin.addOrderRows() entrypoint, then provide more information 
     * about the transaction and send the request using the AddOrderRowsBuilder methods:   
	 * 
	 * Use setCountryCode() to specify the country code matching the original create order request.
	 * 
	 * Use addUpdateOrderRow() with a new WebPayItem.orderRow() object, add the new order row attributes using the
	 * OrderRowBuilder member functions. Notably, the setRowNumber() method specifies which of the original order rows 
	 * to update. That order row will be replaced in full by the new NumberedOrderRow. 
	 * 
	 * Use addOrderRow() with a new WebPayItem.orderRow() object. Add the new order row attributes using the 
	 * OrderRowBuilder member functions.
	 * 
	 * Then use either addInvoiceOrderRows() or addPaymentPlanOrderRows() to get a request object, which ever 
	 * matches the payment method used in the original order.
	 * 
	 * Calling doRequest() on the request object will send the request to Svea and return an AddOrderRowsResponse.
	 * 
     * ...
     *     request = WebPayAdmin.addOrderRows(config)
     *         .setOrderId()                // required
     *         .setCountryCode()            // required
     *         .addOrderRow()           	// required, OrderRowBuilder containing the new order row data
     *     ;
     *     // then select the corresponding request class and send request
     *     response = request.addInvoiceOrderRows().doRequest();     // returns AddOrderRowsResponse
     *     response = request.addPaymentPlanOrderRows().doRequest(); // returns AddOrderRowsResponse
     * ...
     * 
     * @author Kristian Grossman-Madsen
     */        
    public static AddOrderRowsBuilder addOrderRows( ConfigurationProvider config ) {
    	verifyConfig( config );
    	return new AddOrderRowsBuilder( config );
    }
   

    private static void verifyConfig( ConfigurationProvider config) throws SveaWebPayException {
	    if (config == null) {
	    	throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
	    }    	
    }
}
