package se.sveaekonomi.webpay.integration.response.hosted;

/**
 * Handles the asynchronous response from the hosted payment solution 
 * Compatibility wrapper for new semantically named HostedPaymentResponse class
 * @author Kristian Grossman-Madsen
 */
public class SveaResponse extends HostedPaymentResponse {
	public SveaResponse( String responseXmlBase64, String secretWord) {
		super( responseXmlBase64, secretWord);
	}
}
