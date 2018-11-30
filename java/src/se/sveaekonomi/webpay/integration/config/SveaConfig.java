package se.sveaekonomi.webpay.integration.config;

import java.net.MalformedURLException;
import java.net.URL;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public class SveaConfig {
    
    private static final String SWP_TEST_URL = "https://webpaypaymentgatewaystage.svea.com/webpay/payment";
    private static final String SWP_PROD_URL = "https://webpaypaymentgateway.svea.com/webpay/payment";
    private static final String SWP_TEST_WS_URL = "https://webpaywsstage.svea.com/SveaWebPay.asmx?WSDL";
    private static final String SWP_PROD_WS_URL = "https://webpayws.svea.com/SveaWebPay.asmx?WSDL";
    private static final String SWP_TEST_HOSTED_ADMIN_URL = "https://webpaypaymentgatewaystage.svea.com/webpay/rest/";		// ends with "/" as we need to add request method
    private static final String SWP_PROD_HOSTED_ADMIN_URL = "https://webpaypaymentgateway.svea.com/webpay/rest/";   // ends with "/" as we need to add request method
    
    private static final String SWP_TEST_ADMIN_URL = "https://webpayadminservicestage.svea.com/AdminService.svc/backward"; // /backward => SOAP 1.1
    private static final String SWP_PROD_ADMIN_URL = "https://webpayadminservice.svea.com/AdminService.svc/backward"; // /backward => SOAP 1.1
    
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
    
    public static URL getProdAdminServiceUrl() {
    	return getUrlFromString(SWP_PROD_ADMIN_URL);
    }
    
    public static URL getTestAdminServiceUrl() {
    	return getUrlFromString(SWP_TEST_ADMIN_URL);
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
