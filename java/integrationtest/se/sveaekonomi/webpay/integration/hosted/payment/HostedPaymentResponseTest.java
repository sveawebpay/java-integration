package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class HostedPaymentResponseTest {
    
    @Test
    public void testDoCardPaymentRequest() {
        HttpUnitOptions.setScriptingEnabled( false );
        
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(Long.toString((new Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPageCardOnly()
            .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtml")
            .getPaymentForm();
        
        WebResponse result = postRequest(form);
        
        assertEquals("OK", result.getResponseMessage());
    }
    
    @Test
    public void testNordeaSePaymentRequest() {
    	HttpUnitOptions.setScriptingEnabled( false );
        
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(Long.toString((new Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePaymentMethod(PAYMENTMETHOD.NORDEA_SE)
            .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtml")
            .getPaymentForm();
        
        WebResponse result = postRequest(form);
        
        assertEquals("OK", result.getResponseMessage());
    }
    
    private WebResponse postRequest(PaymentForm form) {
        WebConversation conversation = new WebConversation();
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig());
        WebRequest request = new PostMethodWebRequest(order.getConfig().getEndPoint(PAYMENTTYPE.HOSTED).toString());
        
        form.setMacSha512(HashUtil.createHash(form.getXmlMessageBase64() + order.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, order.getCountryCode()), HASHALGORITHM.SHA_512));
        request.setParameter("mac", form.getMacSha512());
        request.setParameter("message", form.getXmlMessageBase64());
        request.setParameter("merchantid", form.getMerchantId());
        
        WebResponse response = null;
        
        try {
            response = conversation.getResponse(request);
        } catch (IOException e) {
            throw new SveaWebPayException("Faild to post request", e);
        } catch (SAXException e) {
            throw new SveaWebPayException("Faild to post request", e);
        }
        
        return response;
    }
}
