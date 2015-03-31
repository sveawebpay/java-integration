package se.sveaekonomi.webpay.integration.util.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;

public class GetRequestPropertiesTest {
    
	@Test
    public void testGetSveaLibraryProperties() {
    	HashMap<String,String> properties = GetRequestProperties.getSveaLibraryProperties();
        
        assertTrue(properties.get("library_version").startsWith("2."));
        assertEquals("Java Integration Package", properties.get("library_name") );
    }

	@Test
	public void testGetSveaIntegrationProperties() {
    	HashMap<String,String> properties = GetRequestProperties.getSveaIntegrationProperties( SveaConfig.getDefaultConfig() );
        
        assertEquals("Integration package default SveaTestConfigurationProvider.", properties.get("integrationcompany") );
        assertEquals("Integration package default SveaTestConfigurationProvider.", properties.get("integrationversion") );
        assertEquals("Integration package default SveaTestConfigurationProvider.", properties.get("integrationplatform") );
	}
	
}
