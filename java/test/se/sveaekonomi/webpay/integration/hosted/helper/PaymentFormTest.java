package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

public class PaymentFormTest {
    
    private static final String SecretWord = "secret";
    private static final String MerchantId = "1234";

    @Test
    public void testSetForm() {
        PaymentForm form = new PaymentForm();
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
        
        form.setMessageBase64(base64Payment)
            .setMerchantId(MerchantId)
            .setSecretWord(SecretWord)
            .setTestmode("true")
            .setForm();
        
        final String EXPECTED = "<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\""
                + SveaConfig.SWP_TEST_URL
                + "\">"
                + "<input type=\"hidden\" name=\"merchantid\" value=\"" + MerchantId + "\" />"
                + "<input type=\"hidden\" name=\"message\" value=\"" + base64Payment + "\" />"
                + "<input type=\"hidden\" name=\"mac\" value=\"" + mac + "\" />"
                + "<noscript><p>Javascript is inactivated in your browser, you will manually have to redirect to the paypage</p></noscript>"
                + "<input type=\"submit\" name=\"submit\" value=\"Submit\" />"
                + "</form>";
        
        assertEquals(EXPECTED, form.getForm());
    }

    @Test
    public void testSetHtmlFields() {
        PaymentForm form = new PaymentForm();
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
        
        form.setMessageBase64(base64Payment)
            .setMerchantId(MerchantId)
            .setSecretWord(SecretWord)
            .setTestmode("true")
            .setHtmlFields();
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();
        
        assertTrue(formHtmlFields.get("form_start_tag").equals("<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\"" + SveaConfig.SWP_TEST_URL + "\">"));
        assertTrue(formHtmlFields.get("input_merchantId").equals("<input type=\"hidden\" name=\"merchantid\" value=\"" + MerchantId + "\" />"));
        assertTrue(formHtmlFields.get("input_message").equals("<input type=\"hidden\" name=\"message\" value=\"" + base64Payment + "\" />"));
        assertTrue(formHtmlFields.get("input_mac").equals("<input type=\"hidden\" name=\"mac\" value=\"" + mac + "\" />"));
        assertTrue(formHtmlFields.get("noscript_p_tag").equals("<noscript><p>Javascript is inactivated in your browser, you will manually have to redirect to the paypage</p></noscript>"));
        assertTrue(formHtmlFields.get("input_submit").equals("<input type=\"submit\" name=\"submit\" value=\"Submit\" />"));
        assertTrue(formHtmlFields.get("form_end_tag").equals("</form>"));
    }
}
