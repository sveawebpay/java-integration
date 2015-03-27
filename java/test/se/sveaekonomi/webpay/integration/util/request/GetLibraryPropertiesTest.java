package se.sveaekonomi.webpay.integration.util.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

public class GetLibraryPropertiesTest {
    
	@Test
    public void testGetSveaLibraryProperties() {
    	HashMap<String,String> properties = GetLibraryProperties.getSveaLibraryProperties();
        
        assertTrue(properties.get("library_version").startsWith("2."));
        assertEquals("Java Integration Package", properties.get("library_name") );
    }
	
}
