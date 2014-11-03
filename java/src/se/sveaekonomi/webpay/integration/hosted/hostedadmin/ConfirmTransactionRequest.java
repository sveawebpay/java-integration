package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;

public class ConfirmTransactionRequest implements Requestable {

	private DeliverOrderBuilder builder;
	
	public ConfirmTransactionRequest( DeliverOrderBuilder builder ) {
		this.builder = builder;
	}
	
	
	@Override
	public ConfirmTransactionResponse doRequest() {
		// TODO stub
		return new ConfirmTransactionResponse();
	}



	@Override
	public <T> T prepareRequest() {
		// TODO Auto-generated method stub
		return null;
	}

}
