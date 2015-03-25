package se.sveaekonomi.webpay.integration.util.request;

import java.util.HashMap;

/**
 * Class to get integration package name and version from manifest file for request versioning
 */
public class GetLibraryProperties {
       
	public static HashMap<String,String> getSveaLibraryProperties() {		
		
		HashMap<String,String> libraryProperties = new HashMap<>();
		
		// get library name and version from manifest file
		String name = GetLibraryProperties.class.getName();
		Package mypackage = GetLibraryProperties.class.getPackage();
		String library_name = GetLibraryProperties.class.getPackage().getImplementationTitle();
		String library_version = GetLibraryProperties.class.getPackage().getImplementationVersion();

		libraryProperties.put("library_name", library_name);
		libraryProperties.put("library_version", library_version);				
		return libraryProperties;	
	}
}