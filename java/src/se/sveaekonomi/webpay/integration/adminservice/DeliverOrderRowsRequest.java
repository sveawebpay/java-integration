package se.sveaekonomi.webpay.integration.adminservice;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;

/**
 * Handles Admin Webservice DeliverOrderRows method
 * 
 * @author Kristian Grossman-Madsen
 */


public class DeliverOrderRowsRequest  {

	// NOTE: validates on order level, don't validate request attributes in itself (rationale: bad request will return error from webservice)

	private String action;
	private DeliverOrderRowsBuilder builder;
		
	public DeliverOrderRowsRequest( DeliverOrderRowsBuilder builder) {
		this.action = "GetOrders";
		this.builder = builder;
	}

    // TODO
	public String prepareRequest() {
		return "foo";
	};    
    
	// TODO
	public DeliverOrderRowsResponse doRequest() {
		return new DeliverOrderRowsResponse();
	};
}
