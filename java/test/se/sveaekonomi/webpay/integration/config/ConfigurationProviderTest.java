package se.sveaekonomi.webpay.integration.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class ConfigurationProviderTest {
    
    @Test
    public void testDefaultTestConfig() {
        ConfigurationProvider conf = SveaConfig.getDefaultConfig();
        
        assertEquals("sverigetest", conf.getUsername(PAYMENTTYPE.INVOICE, COUNTRYCODE.SE));
        assertEquals("sverigetest", conf.getPassword(PAYMENTTYPE.PAYMENTPLAN, COUNTRYCODE.SE));
        assertEquals(36000, conf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, COUNTRYCODE.NO));
        assertEquals("1130", conf.getMerchantId(PAYMENTTYPE.HOSTED, COUNTRYCODE.NL));
        assertEquals("https://webservices.sveaekonomi.se/webpay_test/SveaWebPay.asmx?WSDL", conf.getEndPoint(PAYMENTTYPE.INVOICE).toString());
    }
}
