package se.sveaekonomi.webpay.integration.config;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class ConfigurationProviderTest {
    
	private ConfigurationProvider defaultConf;
	
	@Before
	public void setUp() {
		defaultConf = SveaConfig.getDefaultConfig();
	}
    
    @Test
    public void TestGetDefaultConfigSe() throws MalformedURLException
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.SE;

        assertEquals("sverigetest", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("sverigetest", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(79021, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(59999, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));

        assertEquals("1130", defaultConf.getMerchantId(PAYMENTTYPE.HOSTED, countryCode));

        assertEquals("8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3", 
        		defaultConf.getSecretWord(PAYMENTTYPE.HOSTED, countryCode));

        assertEquals(defaultConf.getEndPoint(PAYMENTTYPE.HOSTED), new URL("https://test.sveaekonomi.se/webpay/payment"));
        assertEquals(defaultConf.getEndPoint(PAYMENTTYPE.INVOICE), new URL("https://webservices.sveaekonomi.se/webpay_test/SveaWebPay.asmx?WSDL"));
    }

    @Test
    public void TestGetDefaultConfigDk()
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.DK;

        assertEquals("danmarktest2", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("danmarktest2", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(62008, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(64008, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));
    }

    @Test
    public void TestGetDefaultConfigDe()
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.DE;

        assertEquals("germanytest", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("germanytest", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(14997, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(16997, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));
    }

    @Test
    public void TestGetDefaultConfigFi()
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.FI;

        assertEquals("finlandtest2", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("finlandtest2", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(26136, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(27136, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));
    }

    @Test
    public void TestGetDefaultConfigNo()
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.NO;

        assertEquals("norgetest2", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("norgetest2", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(33308, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(32503, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));
    }

    @Test
    public void TestGetDefaultConfigNl()
    {
        final COUNTRYCODE countryCode = COUNTRYCODE.NL;

        assertEquals("hollandtest", defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals("hollandtest", defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(defaultConf.getUsername(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getUsername(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(defaultConf.getPassword(PAYMENTTYPE.PAYMENTPLAN, countryCode), defaultConf.getPassword(PAYMENTTYPE.INVOICE, countryCode));

        assertEquals(85997, defaultConf.getClientNumber(PAYMENTTYPE.INVOICE, countryCode));
        assertEquals(86997, defaultConf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, countryCode));
    }

    @Test
    public void TestProductionUrls() throws MalformedURLException
    {
        assertEquals(new URL("https://webpay.sveaekonomi.se/webpay/payment"), SveaConfig.getProdPayPageUrl());
        assertEquals(new URL("https://webservices.sveaekonomi.se/webpay/SveaWebPay.asmx?WSDL"), SveaConfig.getProdWebserviceUrl());
    }
}
