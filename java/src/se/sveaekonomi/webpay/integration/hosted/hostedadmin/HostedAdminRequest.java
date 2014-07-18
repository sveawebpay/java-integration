/**
 * 
 */
package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


/**
 * @author Kristian Grossman-Madsen
 *
 */
public class HostedAdminRequest {

	protected ConfigurationProvider config;
	protected String method;
	
	/** Used to disambiguate between the various credentials in ConfigurationProvider. */
	private COUNTRYCODE countryCode;
		
	public HostedAdminRequest(ConfigurationProvider config, String method) {
		this.config = config;
		this.method = method;
	}
	
    /**
     * Required. 
     */
    public HostedAdminRequest setCountryCode( COUNTRYCODE countryCode ) {
        this.countryCode = countryCode;
        return this;
    }	
    
	public COUNTRYCODE getCountryCode() {
		return countryCode;
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
