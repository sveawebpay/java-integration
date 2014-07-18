/**
 * 
 */
package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.util.Hashtable;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;


/**
 * @author Kristian Grossman-Madsen
 *
 */
public abstract class HostedAdminRequest {

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
		
	/**
	 * returns the request fields to post to service
	 */
	public Hashtable<String,String> prepareRequest() {
		Hashtable<String,String> requestFields = new Hashtable<>();

		String merchantId = this.config.getMerchantId(PAYMENTTYPE.HOSTED, this.getCountryCode());
		String secretWord = this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode());		
		
    	String xmlMessage = getRequestMessageXml();
    	String xmlMessageBase64 = Base64Util.encodeBase64String(xmlMessage);
    	String macSha512 =  HashUtil.createHash(xmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512);			

    	requestFields.put("message", xmlMessageBase64);
    	requestFields.put("mac", macSha512);
    	requestFields.put("merchantid", merchantId);
    	
		return requestFields;
	}
	
	/**
	 * implemented by child classes, should return the request message xml for the method in question
	 */
	abstract String getRequestMessageXml();
	
}
