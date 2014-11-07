package se.sveaekonomi.webpay.integration.hosted.helper;

import se.sveaekonomi.webpay.integration.response.Response;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.PreparePaymentResponse;

public class PaymentUrl extends Response {

	// passed on from preparepayment request response
	private String rawResponse;
	private String id;
	private String created;
	// convenience methods providing the final url
	private String url;
	private String testUrl;
	
	public PaymentUrl( PreparePaymentResponse response ) {
		super( null ); // null as we're no soap message
				
		this.setOrderAccepted( response.isOrderAccepted() );
		this.setResultCode( response.getResultCode() );
		this.setErrorMessage( response.getErrorMessage() );		
		this.rawResponse = response.getRawResponse();
		
		if( response.isOrderAccepted() ) {
			this.id = response.getId();
			this.created = response.getCreated();
			this.url = "https://webpay.sveaekonomi.se/webpay/preparedpayment/".concat(this.id);
			this.testUrl = "https://test.sveaekonomi.se/webpay/preparedpayment/".concat(this.id);		
		}
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public String getId() {
		return id;
	}

	public String getCreated() {
		return created;
	}

	public String getUrl() {
		return url;
	}
	
	public String getTestUrl() {
		return testUrl;
	}    
}
