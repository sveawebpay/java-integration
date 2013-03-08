package se.sveaekonomi.webpay.integration.config;

import java.net.URL;

public interface Config {
	
	public static final String SWP_TEST_URL = "https://test.sveaekonomi.se/webpay/payment";
    public static final String SWP_PROD_URL = "https://webpay.sveaekonomi.se/webpay/payment";
    public static final String SWP_TEST_WS_URL = "https://webservices.sveaekonomi.se/webpay_test/SveaWebPay.asmx";
    public static final String SWP_PROD_WS_URL = "https://webservices.sveaekonomi.se/webpay/SveaWebPay.asmx";
    
	URL getWebserviceUrl();
	URL getPayPageUrl();
}
