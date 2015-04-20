package se.sveaekonomi.webpay.integration.adminservice;

import java.util.HashMap;

import javax.xml.soap.MimeHeaders;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;

public class AdminServiceRequest {

	protected void setHeaderRequestProperties(MimeHeaders headers, ConfigurationProvider config) {
		HashMap<String,String> libraryproperties = GetRequestProperties.getSveaLibraryProperties();            
        
        headers.addHeader("X-Svea-Library-Name", libraryproperties.get("library_name") );
        headers.addHeader("X-Svea-Library-Version", libraryproperties.get("library_version") );
        
    	HashMap<String,String> integrationproperties = GetRequestProperties.getSveaIntegrationProperties( config );            

        headers.addHeader("X-Svea-Integration-Platform", integrationproperties.get("integrationplatform") );
        headers.addHeader("X-Svea-Integration-Company", integrationproperties.get("integrationcompany") );
        headers.addHeader("X-Svea-Integration-Version", integrationproperties.get("integrationversion") );
	}
	
}
