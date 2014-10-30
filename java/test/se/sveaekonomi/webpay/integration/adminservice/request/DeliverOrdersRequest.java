package se.sveaekonomi.webpay.integration.adminservice.request;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.adminservice.response.DeliverOrdersResponse;

public class DeliverOrdersRequest implements Requestable {

	
	public DeliverOrdersResponse doRequest() {
		return new DeliverOrdersResponse();
	};
}
