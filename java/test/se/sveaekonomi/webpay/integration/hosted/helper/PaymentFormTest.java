package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

public class PaymentFormTest {
    
    private static final String SecretWord = "secret";
    private static final String MerchantId = "1234";

    @Test
    public void testSetForm() throws ValidationException, Exception {

        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
        PaymentForm form = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .usePayPageDirectBankOnly()                
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
    public void testSetHtmlFields() throws ValidationException, Exception {
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
       
        PaymentForm form = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .usePayPageDirectBankOnly()                
                .getPaymentForm();
        
        form.setMessageBase64(base64Payment)
            .setMerchantId(MerchantId)
            .setSecretWord(SecretWord)         
            .setHtmlFields();
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();
        String url = form.getUrl();
        
        assertTrue(formHtmlFields.get("form_start_tag").equals("<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\"" + url + "\">"));
        assertTrue(formHtmlFields.get("input_merchantId").equals("<input type=\"hidden\" name=\"merchantid\" value=\"" + MerchantId + "\" />"));
        assertTrue(formHtmlFields.get("input_message").equals("<input type=\"hidden\" name=\"message\" value=\"" + base64Payment + "\" />"));
        assertTrue(formHtmlFields.get("input_mac").equals("<input type=\"hidden\" name=\"mac\" value=\"" + mac + "\" />"));
        assertTrue(formHtmlFields.get("noscript_p_tag").equals("<noscript><p>Javascript är inaktiverat i er webbläsare, ni får dirigera om till paypage manuellt</p></noscript>"));
        assertTrue(formHtmlFields.get("input_submit").equals("<input type=\"submit\" name=\"submit\" value=\"Betala\" />"));
        assertTrue(formHtmlFields.get("form_end_tag").equals("</form>"));
    }
}
