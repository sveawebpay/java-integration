package se.sveaekonomi.webpay.integration.util.request;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

public class GetLibraryPropertiesTest {
    
	@Test
    public void testGetSveaLibraryProperties() {
    	HashMap<String,String> properties = GetLibraryProperties.getSveaLibraryProperties();
        
        assertEquals("2.0.2", properties.get("library_version") );
        assertEquals("Java Integration Package", properties.get("library_name") );
    }
}
