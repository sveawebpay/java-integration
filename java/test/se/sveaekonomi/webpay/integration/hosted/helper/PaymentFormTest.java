package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
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
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber("666666")
                    .setEmail("test@svea.com")
                    .setPhoneNumber("999999")
                    .setIpAddress("123.123.123.123")
                    .setStreetAddress("Gatan", "23")
                    .setCoAddress("c/o Eriksson")
                    .setZipCode("9999")
                    .setLocality("Stan"))
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
    public void testSetHtmlFields() throws ValidationException, Exception {
        String base64Payment = Base64Util.encodeBase64String("0");
        String mac = HashUtil.createHash(base64Payment + SecretWord, HASHALGORITHM.SHA_512);
       
        PaymentForm form = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("nr26")
                .setCurrency(CURRENCY.SEK)
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber("666666")
                    .setEmail("test@svea.com")
                    .setPhoneNumber("999999")
                    .setIpAddress("123.123.123.123")
                    .setStreetAddress("Gatan", "23")
                    .setCoAddress("c/o Eriksson")
                    .setZipCode("9999")
                    .setLocality("Stan"))
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
