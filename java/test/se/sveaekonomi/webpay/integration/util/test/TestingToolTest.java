package se.sveaekonomi.webpay.integration.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestingToolTest {
    
//	@Test
//	public void testCheckVersionInRequestXml_unlike_string_returns_false() {
//
//		String expectedXml = "2.0.1";
//        
//		String actualXml = "2.0.2";
//		
//        assertFalse( TestingTool.checkVersionInRequestXml(expectedXml, actualXml));
//	}
//
//	@Test
//	public void testCheckVersionInRequestXml_like_string_returns_true() {
//
//		String expectedXml = "2.0.2";
//        
//		String actualXml = "2.0.2";
//		
//        assertTrue( TestingTool.checkVersionInRequestXml(expectedXml, actualXml));
//	}

//	@Test
//	public void testCheckVersionInRequestXml_like_string_returns_true2() {
//
//		String expectedXml = "2.0.2";
//        
//		String actualXml = "2.0.2";
//		
//        assertTrue( TestingTool.checkVersionInRequestXml(expectedXml, actualXml));
//	}	
	
	@Test
	public void testCheckVersionInRequestXml_same_xmlstring_returns_true() {

		String expectedXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><customerrefno>33</customerrefno><currency>SEK</currency><subscriptiontype>RECURRING</subscriptiontype><amount>500</amount><vat>100</vat><returnurl>myurl</returnurl><iscompany>false</iscompany><orderrows><row><sku></sku><name></name><description></description><amount>500</amount><vat>100</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity><unit></unit></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";
        
		String actualXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><customerrefno>33</customerrefno><currency>SEK</currency><subscriptiontype>RECURRING</subscriptiontype><amount>500</amount><vat>100</vat><returnurl>myurl</returnurl><iscompany>false</iscompany><orderrows><row><sku></sku><name></name><description></description><amount>500</amount><vat>100</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity><unit></unit></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";
		
        assertTrue( TestingTool.checkVersionInformationWithRequestXml(expectedXml, actualXml));
	}	
	
	@Test
	public void testGetJsonFromXmlComment_null_if_xmlWithoutComment() {

		String xml = "{\"X-Svea-Library-Name\":\"Java Integration Package\"}-->";
        assertNull( TestingTool.getJsonFromXmlComment(xml));
	}	
	
	@Test
	public void testGetJsonFromXmlComment_matchesXmlWithComment() {
		
		String json = "{\"X-Svea-Library-Name\":\"Java Integration Package\"}";
		String xml = "<!--" + json + "-->";
        assertEquals( json, TestingTool.getJsonFromXmlComment(xml) );
	}	
	
	@Test
	public void testGetJsonFromXmlComment_matchesRealXml() {
		
		String json = 
			"{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}";
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><customerrefno>33</customerrefno><currency>SEK</currency><subscriptiontype>RECURRING</subscriptiontype><amount>500</amount><vat>100</vat><returnurl>myurl</returnurl><iscompany>false</iscompany><orderrows><row><sku></sku><name></name><description></description><amount>500</amount><vat>100</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity><unit></unit></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";

        assertEquals( json, TestingTool.getJsonFromXmlComment(xml) );
	}	
	
	@Test
	public void test_decodeJson() {
		assertEquals( "bar", TestingTool.decodeJson("{\"foo\":\"bar\"}", "foo"));
	}
	
	@Test
	public void test_getVersionString_from_request_xml_comment_json() {
		
		String xml = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><customerrefno>33</customerrefno><currency>SEK</currency><subscriptiontype>RECURRING</subscriptiontype><amount>500</amount><vat>100</vat><returnurl>myurl</returnurl><iscompany>false</iscompany><orderrows><row><sku></sku><name></name><description></description><amount>500</amount><vat>100</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity><unit></unit></row><row><name></name><description></description><amount>0</amount><vat>0</vat><quantity>1.0</quantity></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";
		
		assertEquals("2.0.2", TestingTool.decodeJson( TestingTool.getJsonFromXmlComment(xml), "X-Svea-Library-Version" ) ); 
	}
	
}
