package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

/**
 * Requestable classes validates that the orderbuilder holds all required attributes,
 * prepares the request data, and performs the soap request, then returns the correct
 * response class. 
 * 
 * @author Kristian Grossman-Madsen
 */
public interface Requestable {

	// TODO add validateOrder()
	
	// TODO add prepareRequest()
	public <T> T prepareRequest();
	
	/**
	 * doRequest() performs a soap request and returns the corresponding response class
	 */
	public <T extends Respondable> T doRequest();
}
