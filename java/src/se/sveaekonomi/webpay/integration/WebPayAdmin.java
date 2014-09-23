package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;

/**
 * @author Kristian Grossman-Madsen
 */
public class WebPayAdmin {

	
    /**
     * TODO javadoc documentation 
     * 
     * Cancel an undelivered/unconfirmed order. Supports Invoice, PaymentPlan
     * and Card orders. (For Direct Bank orders, see CreditOrder instead.)
     *
     * Use the following methods to set the order attributes needed in the request:
     * ->setOrderId(sveaOrderId or transactionId from createOrder response)
     * ->setCountryCode()
     *
     * Then select the correct ordertype and perform the request:
     * ->cancelInvoiceOrder() | cancelPaymentPlanOrder() | cancelCardOrder()
     *   ->doRequest
     *
     * The final doRequest() returns either a CloseOrderResult or an AnnulTransactionResponse
     *
     */
	public static CancelOrderBuilder cancelOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }    	
        
        return new CancelOrderBuilder(config);
	}

}
