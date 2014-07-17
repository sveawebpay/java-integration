package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;

/**
 * AnnulTransaction is used to cancel (annul) a card transaction. 
 * The transaction must have status AUTHORIZED or CONFIRMED at Svea.
 * After a successful request the transaction will get the status ANNULLED.
 *
 * @author Kristian Grossman-Madsen
 */
public class AnnulTransactionRequest extends HostedAdminRequest {

	public String transactionId;
	
	public AnnulTransactionRequest( ConfigurationProvider config ) {
		super( config, "annul" );
	}	
	
//	public prepareRequest() {
//		
//		// check that all required setX() methods have been used
//		this.validateRequest();
//	
//		// create message xml
//		
//		// create message mac for the configured merchantid 
//		
//		// return request post fields
//	}
	
}
