package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class PaymentFormTest {

    private static final String SecretWord = "secret";
    private static final String MerchantId = "1234";
    
    @Test
    public void testSetForm() {
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
        
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createCompanyCustomer())
                .usePayPageDirectBankOnly()  
                .setReturnUrl("http:myurl")
                .getPaymentForm();
        
        form
                .setMessageBase64(base64Payment)
                .setMerchantId(MerchantId)
                .setSecretWord(SecretWord)
                .setForm();
        
        final String EXPECTED = "<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\""
                + form.getUrl()
                + "\">"
                + "<input type=\"hidden\" name=\"merchantid\" value=\"" + MerchantId + "\" />"
                + "<input type=\"hidden\" name=\"message\" value=\"" + base64Payment + "\" />"
                + "<input type=\"hidden\" name=\"mac\" value=\"" + mac + "\" />"
                + "<noscript><p>Javascript är inaktiverat i er webbläsare, ni får dirigera om till paypage manuellt</p></noscript>"
                + "<input type=\"submit\" name=\"submit\" value=\"Betala\" />"
                + "</form>";
        
        assertEquals(EXPECTED, form.getCompleteForm());
    }
    
    @Test
    public void testSetHtmlFields() {
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
        
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createCompanyCustomer())
                .usePayPageDirectBankOnly()
                .setReturnUrl("http:myurl.se")
                .getPaymentForm();
        
        form.setMessageBase64(base64Payment)
            .setMerchantId(MerchantId)
            .setSecretWord(SecretWord)
            .setHtmlFields();
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();
        String url = form.getUrl();
        
        assertEquals("<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\"" + url + "\">", formHtmlFields.get("form_start_tag"));
        assertEquals("<input type=\"hidden\" name=\"merchantid\" value=\"" + MerchantId + "\" />", formHtmlFields.get("input_merchantId"));
        assertEquals("<input type=\"hidden\" name=\"message\" value=\"" + base64Payment + "\" />", formHtmlFields.get("input_message"));
        assertEquals("<input type=\"hidden\" name=\"mac\" value=\"" + mac + "\" />", formHtmlFields.get("input_mac"));
        assertEquals("<noscript><p>Javascript är inaktiverat i er webbläsare, ni får dirigera om till paypage manuellt</p></noscript>", formHtmlFields.get("noscript_p_tag"));
        assertEquals("<input type=\"submit\" name=\"submit\" value=\"Betala\" />", formHtmlFields.get("input_submit"));
        assertEquals("</form>", formHtmlFields.get("form_end_tag"));
    }
}
