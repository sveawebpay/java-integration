package se.sveaekonomi.webpay.integration.util.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import se.sveaekonomi.webpay.integration.WebPay;

/**
 * Class to get integration package name and version from manifest file for request versioning
 */
public class GetLibraryProperties {
       
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
}