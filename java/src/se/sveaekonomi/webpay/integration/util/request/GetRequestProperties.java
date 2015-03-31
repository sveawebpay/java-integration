package se.sveaekonomi.webpay.integration.util.request;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;

/**
 * Class to get integration package name and version from manifest file for request versioning
 */
public class GetRequestProperties {
       
	public static HashMap<String,String> getSveaLibraryProperties() {		
		
		HashMap<String,String> libraryProperties = new HashMap<>();
		
		// get library name and version from manifest file
		Properties prop = new Properties();
    	InputStream input = null;
 
    	try { 
    		String filename = "info.properties";
    		
    		input = WebPay.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    return libraryProperties;
    		}
 
    		//load a properties file from class path, inside static method
    		prop.load(input);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
 
		String library_name = prop.getProperty("library_name");
		String library_version = prop.getProperty("library_version");

		libraryProperties.put("library_name", library_name);
		libraryProperties.put("library_version", library_version);				
		return libraryProperties;
	}

	public static HashMap<String, String> getSveaIntegrationProperties( ConfigurationProvider config ) {
		
		HashMap<String,String> integrationProperties = new HashMap<>();

		// get integration platform, version and company from ConfigurationProvider, if implemented, else use default
		String integrationcompany = getIntegrationMethodIfAvailable(config, "getIntegrationCompany", "Integration company not available");
		String integrationversion = getIntegrationMethodIfAvailable(config, "getIntegrationVersion", "Integration version not available");
		String integrationplatform = getIntegrationMethodIfAvailable(config, "getIntegrationPlatform", "Integration platform not available");
				
		integrationProperties.put("integrationcompany", integrationcompany);
		integrationProperties.put("integrationversion", integrationversion);				
		integrationProperties.put("integrationplatform", integrationplatform);				
		return integrationProperties;		
	}

	private static String getIntegrationMethodIfAvailable( ConfigurationProvider config, String integrationMethod, String integrationMethodDefaultReturnValue) {
		String getIntegrationMethodReturnValue = "";
		Method getIntegrationMethod;
		try {
			getIntegrationMethod = config.getClass().getMethod( integrationMethod );	
			getIntegrationMethodReturnValue = (String) getIntegrationMethod.invoke(config);
		}
		catch (	NoSuchMethodException | // from getMethod()
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) // from invoke(), shouldn't happen
		{
			getIntegrationMethodReturnValue = integrationMethodDefaultReturnValue;			
		}
		return getIntegrationMethodReturnValue;
	}
	
    /**
     * Given a ConfigurationProvider, return a json string containing the Svea integration package (library) 
     * and integration (from config) name, version et al. Used by HostedService requests.
     */
    public static String getLibraryAndPlatformPropertiesAsJson( ConfigurationProvider config ) {
    	
		HashMap<String,String> libraryproperties = GetRequestProperties.getSveaLibraryProperties();            
		HashMap<String,String> integrationproperties = GetRequestProperties.getSveaIntegrationProperties( config );            
      
		// write json string in well-defined order, i.e in the order the test suite currently expects it
		String json = String.format(
				"{\"X-Svea-Integration-Version\":\"%s\",\"X-Svea-Integration-Platform\":\"%s\",\"X-Svea-Library-Name\":\"%s\",\"X-Svea-Integration-Company\":\"%s\",\"X-Svea-Library-Version\":\"%s\"}", 
				integrationproperties.get("integrationversion"),
				integrationproperties.get("integrationplatform"),
				libraryproperties.get("library_name"),
				integrationproperties.get("integrationcompany"),
				libraryproperties.get("library_version")
			)
		;		
		return json;
    }
}