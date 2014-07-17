package se.sveaekonomi.webpay.integration.config;

import java.net.MalformedURLException;
import java.net.URL;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public class SveaConfig {
    
    private static final String SWP_TEST_URL = "https://test.sveaekonomi.se/webpay/payment";
    private static final String SWP_PROD_URL = "https://webpay.sveaekonomi.se/webpay/payment";
    private static final String SWP_TEST_WS_URL = "https://webservices.sveaekonomi.se/webpay_test/SveaWebPay.asmx?WSDL";
    private static final String SWP_PROD_WS_URL = "https://webservices.sveaekonomi.se/webpay/SveaWebPay.asmx?WSDL";
    private static final String SWP_TEST_HOSTED_ADMIN_URL = "https://test.sveaekonomi.se/webpay/rest/";		// ends with "/" as we need to add request method
    private static final String SWP_PROD_HOSTED_ADMIN_URL = "https://webpay.sveaekonomi.se/webpay/rest/";   // ends with "/" as we need to add request method 
    
    
    public static URL getProdWebserviceUrl() {
        return getUrlFromString(SWP_PROD_WS_URL);
    }
    
    public static URL getProdPayPageUrl() {
        return getUrlFromString(SWP_PROD_URL);
    }
    
    public static URL getTestWebserviceUrl() {
        return getUrlFromString(SWP_TEST_WS_URL);
    }
    
    public static URL getTestPayPageUrl() {
        return getUrlFromString(SWP_TEST_URL);
    }
    
    public static URL getProdHostedAdminUrl() {
    	return getUrlFromString(SWP_PROD_HOSTED_ADMIN_URL);
    }

    public static URL getTestHostedAdminUrl() {
    	return getUrlFromString(SWP_TEST_HOSTED_ADMIN_URL);
    }
    
    
    private static URL getUrlFromString(String swpTestWsUrl) {
        try {
            return new URL(swpTestWsUrl);
        } catch (MalformedURLException e) {
            throw new SveaWebPayException("Bad URL", e);
        }
    }
    
    public static ConfigurationProvider getDefaultConfig() {
        return new SveaTestConfigurationProvider();
    }
}
