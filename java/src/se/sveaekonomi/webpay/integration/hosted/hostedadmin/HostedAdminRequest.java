/**
 * 
 */
package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;


/**
 * @author Kristian Grossman-Madsen
 *
 */
public class HostedAdminRequest {

	private ConfigurationProvider config;
	private String method;
	
	public HostedAdminRequest(ConfigurationProvider config, String method) {
		this.config = config;
		this.method = method;
	}
	
//    /**
//     * Performs a request using HttpClient, parsing the response using SveaResponse 
//     * and returning the resulting HostedAdminResponse<T> instance.
//     */
//	public HostedAdminResponse doRequest() {
//		
//		
//		
//		
//		HostedAdminResponse response = new PreparePaymentResponse("foo", "bar");		
//		return response;
//	}
	
}
