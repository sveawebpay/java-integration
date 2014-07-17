package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;

public class AnnulTransactionRequestTest extends TestCase {

	ConfigurationProvider config;
			
	@Before
	public void setUp() {
		this.config = SveaConfig.getDefaultConfig();
	}
	
    @Test
    public void test_AnnulTransactionRequest_class_exists() {
    	AnnulTransactionRequest request = new AnnulTransactionRequest(null);    	        
        assertThat( request, instanceOf(AnnulTransactionRequest.class) );
        assertThat( request, instanceOf(HostedAdminRequest.class) );
    }    
    
}
