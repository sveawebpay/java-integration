package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class PaymentMethodTest {

    @Test
    public void testPayPagePaymentWithSetPaymentMethod() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(WebPayItem.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePaymentMethod(PAYMENTMETHOD.KORTCERT)
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
            
        // actual, expected response strings as parameter
        String expectedXml =
        		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><paymentmethod>KORTCERT</paymentmethod><customerrefno>33</customerrefno><currency>SEK</currency><amount>12500</amount><vat>2500</vat><returnurl>http://myurl.se</returnurl><iscompany>false</iscompany><customer><ssn>194605092222</ssn><country>SE</country></customer><orderrows><row><sku>1</sku><name>Prod</name><description>Specification</description><amount>12500</amount><vat>2500</vat><quantity>2.0</quantity><unit>st</unit></row><row><sku>1</sku><name>Relative</name><description>RelativeDiscount</description><amount>-12500</amount><vat>-2500</vat><quantity>1.0</quantity><unit>st</unit></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";        
        String actualXml = form.getXmlMessage();

        assertTrue( TestingTool.checkVersionInformationWithRequestXml( expectedXml, actualXml ) );
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        assertTrue(html.contains("<paymentmethod>KORTCERT</paymentmethod>"));
    }
    
    @Test
    public void testPayPagePaymentWithSetPaymentMethodNL() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(WebPayItem.individualCustomer()
                .setInitials("SB")
                .setBirthDate("19460509")
                .setName("Sneider", "Boasman")
                .setStreetAddress("Gate", "42")
                .setLocality("BARENDRECHT")
                .setZipCode("1102 HG"))
            .setCountryCode(COUNTRYCODE.NL)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePaymentMethod(PAYMENTMETHOD.INVOICE)
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        // actual, expected response strings as parameter
        String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}--><payment><paymentmethod>SVEAINVOICEEU_NL</paymentmethod><customerrefno>33</customerrefno><currency>SEK</currency><amount>12500</amount><vat>2500</vat><returnurl>http://myurl.se</returnurl><iscompany>false</iscompany><customer><ssn>19460509</ssn><firstname>Sneider</firstname><lastname>Boasman</lastname><initials>SB</initials><address>Gate</address><housenumber>42</housenumber><zip>1102 HG</zip><city>BARENDRECHT</city><country>NL</country></customer><orderrows><row><sku>1</sku><name>Prod</name><description>Specification</description><amount>12500</amount><vat>2500</vat><quantity>2.0</quantity><unit>st</unit></row><row><sku>1</sku><name>Relative</name><description>RelativeDiscount</description><amount>-12500</amount><vat>-2500</vat><quantity>1.0</quantity><unit>st</unit></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";
        String actualXml = form.getXmlMessage();

        assertTrue( TestingTool.checkVersionInformationWithRequestXml( expectedXML, actualXml ) );
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        assertTrue(html.contains("<paymentmethod>SVEAINVOICEEU_NL</paymentmethod>"));
    }
}
