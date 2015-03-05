package se.sveaekonomi.webpay.integration.response.hosted;

import se.sveaekonomi.webpay.integration.response.Response;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.PreparePaymentResponse;
import se.sveaekonomi.webpay.integration.response.webservice.WebServiceResponse;

public class PaymentUrl implements Response {

    private Boolean isOrderAccepted;
    private String resultCode;
    private String errorMessage;
    
    public Boolean isOrderAccepted() {
        return isOrderAccepted;
    }

    public void setOrderAccepted(Boolean isOrderAccepted) {
        this.isOrderAccepted = isOrderAccepted;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }	
	
	
	// passed on from preparepayment request response
	private String rawResponse;
	private Long id;
	private String created;
	// convenience methods providing the final url
	private String url;
	private String testUrl;
	
	public PaymentUrl( PreparePaymentResponse response ) {
				
		this.setOrderAccepted( response.isOrderAccepted() );
		this.setResultCode( response.getResultCode() );
		this.setErrorMessage( response.getErrorMessage() );		
		this.rawResponse = response.getRawResponse();
		
		if( response.isOrderAccepted() ) {
			this.id = response.getId();
			this.created = response.getCreated();
			this.url = "https://webpay.sveaekonomi.se/webpay/preparedpayment/".concat(String.valueOf(this.id));
			this.testUrl = "https://test.sveaekonomi.se/webpay/preparedpayment/".concat(String.valueOf(this.id));		
		}
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public Long getId() {
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
