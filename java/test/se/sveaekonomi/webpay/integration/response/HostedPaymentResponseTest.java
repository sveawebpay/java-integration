package se.sveaekonomi.webpay.integration.response;

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
    public void testDirectBankResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTg5Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+MzczNzgyMzk4N19pZF8wMDE8L2N1c3RvbWVycmVmbm8+DQogICAgPGFtb3VudD41MDA8L2Ftb3VudD4NCiAgICA8Y3VycmVuY3k+U0VLPC9jdXJyZW5jeT4NCiAgPC90cmFuc2FjdGlvbj4NCiAgPHN0YXR1c2NvZGU+MDwvc3RhdHVzY29kZT4NCjwvcmVzcG9uc2U+";
        String mac = "64dcd245ffcdcc8e9fe101411de581b2c612d29ba97a32e14203e0973a3ff7e23ed2ba22901db2a3454e04846bd65a7f257272f5921bba58a1fb25694456c675";
        SveaResponse response = new SveaResponse(testXMLResponseBase64, mac, null);
        
        assertEquals(response.getTransactionId(), "566989");
        assertEquals(response.getPaymentMethod(), "DBNORDEASE");
        assertEquals(response.getMerchantId(), "1175");
        assertEquals(response.getClientOrderNumber(), "3737823987_id_001");
        assertEquals(response.getAmount(), 5, 0);
        assertEquals(response.getCurrency(), "SEK");
        assertEquals(response.getResultCode(), 0);        
    }
    
    @Test
    public void testCreatePaymentResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTIzIj4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTQ2MDU3NjQ5MTwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT4xNTI1ODc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4=";
        String mac = "2e118d9be21dc12528d700fab54917ce4effd152be8a4b8977c75915843d31fc04204b054ee784a7fcf59262c9396861562dc1ca09cac57316bfff6da9785839";
        SveaResponse response = new SveaResponse(testXMLResponseBase64, mac, null);
        assertEquals(response.isOrderAccepted(), true);         
        assertEquals(response.getTransactionId(), "566923");
        assertEquals(response.getClientOrderNumber(), "test_1359460576491");
        assertEquals(response.getAmount(), 5.00, 0);
        assertEquals(response.getCurrency(), "SEK");
        assertEquals(response.getCardType(), "VISA");
        assertEquals(response.getExpiryMonth(), "03");
        assertEquals(response.getExpiryYear(), "20");
        assertEquals(response.getAuthCode(), "152587");
    }
    
    @Test
    public void testPayPageDirectBankResponse() throws SAXException, IOException, ParserConfigurationException {        
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU2Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+dGVzdF8xMzU5NjIwMzExNTg0PC9jdXN0b21lcnJlZm5vPg0KICAgIDxhbW91bnQ+NTAwPC9hbW91bnQ+DQogICAgPGN1cnJlbmN5PlNFSzwvY3VycmVuY3k+DQogIDwvdHJhbnNhY3Rpb24+DQogIDxzdGF0dXNjb2RlPjA8L3N0YXR1c2NvZGU+DQo8L3Jlc3BvbnNlPg==";
        String mac = "007381c8a309bae18fdfc643313fc13cfb9a197855233732521f068400f2dfb72e89298a67892be6be65fa890a1f4193dbbbee4b8b4f4bda7a04454d0c61b541";
        SveaResponse response = new SveaResponse(testXMLResponseBase64, mac, null);
        
        assertEquals(response.isOrderAccepted(), true);
        assertEquals(response.getResultCode(), 0);
        assertEquals(response.getTransactionId(), "567056");
        assertEquals(response.getMerchantId(), "1175");
        assertEquals(response.getAmount(), 5, 0);
        assertEquals(response.getClientOrderNumber(), "test_1359620311584");
        assertEquals(response.getCurrency(), "SEK");
        assertEquals(response.getPaymentMethod(), "DBNORDEASE");
    }
    
    @Test
    public void testPayPageDirectBankInterruptedResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDYyIj4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+dGVzdF8xMzU5NjIzMDIyMTQzPC9jdXN0b21lcnJlZm5vPg0KICAgIDxhbW91bnQ+NTAwPC9hbW91bnQ+DQogICAgPGN1cnJlbmN5PlNFSzwvY3VycmVuY3k+DQogIDwvdHJhbnNhY3Rpb24+DQogIDxzdGF0dXNjb2RlPjEwNzwvc3RhdHVzY29kZT4NCjwvcmVzcG9uc2U+DQo=";
        String mac = "17960e06bb6eea06ec6066116f41683c814c391fdce4685fdc4e218c506c855b3f59da75c84c1ca99c8bd319c6e87960770e0c7f6e10c0f52b5276835183eb9b";
        SveaResponse response = new SveaResponse(testXMLResponseBase64, mac, null);
        assertEquals(response.getResultCode(), 107);
    }
    
    @Test
    public void testPayPageCardPaymentResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU4Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTYyMTQ2NTk5MDwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT43NjQ4Nzc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4NCg==";
        String mac = "f99cff441cfc80c6632602d4e2a08fff63483398274c9f791b010bad18f6f477a2617d6bd2512eff4c9fe4d9fd1358926c11fe34577fb498805caac80cffb865";
        SveaResponse response = new SveaResponse(testXMLResponseBase64, mac, null);
        
        assertEquals(response.getTransactionId(), "567058");
        assertEquals(response.getPaymentMethod(), "KORTCERT");
        assertEquals(response.getMerchantId(), "1175");
        assertEquals(response.getClientOrderNumber(), "test_1359621465990");
        assertEquals(response.getAmount(), 5, 0);
        assertEquals(response.getCurrency(), "SEK");
        assertEquals(response.getCardType(), "VISA");
        assertEquals(response.getMaskedCardNumber(), "444433xxxxxx3300");
        assertEquals(response.getExpiryMonth(), "03");
        assertEquals(response.getExpiryYear(), "20");
        assertEquals(response.getAuthCode(), "764877");
        assertEquals(response.getResultCode(), 0);
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
        form.setMacSha512(HashUtil.createHash(form.getMessageBase64() + order.config.getSecretWord(), HASHALGORITHM.SHA_512));
        request.setParameter("mac", form.getMacSha512());
        request.setParameter("message", form.getMessageBase64());
        request.setParameter("merchantid", form.getMerchantId());        
        return conversation.getResponse(request);               
    }         
}
  
