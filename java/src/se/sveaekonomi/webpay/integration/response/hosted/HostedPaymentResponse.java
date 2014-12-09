package se.sveaekonomi.webpay.integration.response.hosted;

/**
 * Handles the asynchronous response from the hosted payment solution 
 * Semantic wrapper for old SveaResponse class
 * @author Kristian Grossman-Madsen
 */
public class HostedPaymentResponse extends SveaResponse {
	public HostedPaymentResponse( String responseXmlBase64, String secretWord) {
		super( responseXmlBase64, secretWord);
	}
}
