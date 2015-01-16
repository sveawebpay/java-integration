package se.sveaekonomi.webpay.integration.adminservice;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;

/**
 * DeliverOrdersRequest handles requests to Svea Admin WebService DeliverOrders method
 * 
 * @author Kristian Grossman-Madsen
 */
public class DeliverOrdersRequest implements Requestable {
	
	private DeliverOrderBuilder order;

    public DeliverOrdersRequest(DeliverOrderBuilder orderBuilder) {
        this.order =  orderBuilder;
    }

    // TODO
	public String prepareRequest() {
		return "foo";
	};    
    
	// TODO
	public DeliverOrdersResponse doRequest() {
		return new DeliverOrdersResponse();
	};
}
