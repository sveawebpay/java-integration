package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.hosted.SveaResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class HostedPaymentResponseTest {
  private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = new CreateOrderBuilder();
    }
    
    
        @Test
    public void testDoCardPaymentRequest() throws Exception {
        HttpUnitOptions.setScriptingEnabled( false );
        
        order.setTestmode();
        order.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
        order.addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"));
            PaymentForm form = order.setCountryCode(COUNTRYCODE.SE)
        .setClientOrderNumber(String.valueOf(Calendar.DATE) + String.valueOf(Calendar.MILLISECOND))
        .setCurrency("SEK")
        .usePayPageCardOnly()
            .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtml")
            .getPaymentForm();
                        
        WebResponse result = postRequest(SveaConfig.SWP_TEST_URL, form);        
        assertEquals("OK", result.getResponseMessage());        
    }
    
    private WebResponse postRequest(String sveaUrl, PaymentForm form) throws IOException, SAXException {
        WebConversation conversation = new WebConversation();
        WebRequest request = new PostMethodWebRequest(sveaUrl);       
        form.setMacSha512(HashUtil.createHash(form.getXmlMessageBase64() + order.config.getSecretWord(), HASHALGORITHM.SHA_512));
        request.setParameter("mac", form.getMacSha512());
        request.setParameter("message", form.getXmlMessageBase64());
        request.setParameter("merchantid", form.getMerchantId());        
        return conversation.getResponse(request);               
    }         
}
  
