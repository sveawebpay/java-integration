package se.sveaekonomi.webpay.integration.adminservice.request;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.adminservice.response.DeliverOrdersResponse;
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
		
	public DeliverOrdersResponse doRequest() {
		return new DeliverOrdersResponse();
	};
}
