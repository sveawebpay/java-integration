package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.hosted.SveaResponse;


public class HostedPaymentResponseTest {
	public final SveaConfig config = new SveaConfig();
    
    @Test
    public void testDirectBankResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTg5Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+MzczNzgyMzk4N19pZF8wMDE8L2N1c3RvbWVycmVmbm8+DQogICAgPGFtb3VudD41MDA8L2Ftb3VudD4NCiAgICA8Y3VycmVuY3k+U0VLPC9jdXJyZW5jeT4NCiAgPC90cmFuc2FjdGlvbj4NCiAgPHN0YXR1c2NvZGU+MDwvc3RhdHVzY29kZT4NCjwvcmVzcG9uc2U+";
 
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
        assertEquals(response.getTransactionId(), "566989");
        assertEquals(response.getPaymentMethod(), "DBNORDEASE");
        assertEquals(response.getMerchantId(), "1175");
        assertEquals(response.getClientOrderNumber(), "3737823987_id_001");
        assertEquals(response.getAmount(), 5, 0);
        assertEquals(response.getCurrency(), "SEK");
        assertEquals(response.getResultCode(),"0 (ORDER_ACCEPTED)");
    }
    
    @Test
    public void testCreatePaymentResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTIzIj4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTQ2MDU3NjQ5MTwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT4xNTI1ODc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4=";
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
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
 
        SveaResponse response = new SveaResponse(testXMLResponseBase64, null);
        
        assertEquals(response.isOrderAccepted(), true);
        assertEquals(response.getResultCode(), "0 (ORDER_ACCEPTED)");
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
 
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        assertEquals(response.getResultCode(), "107 (DENIED_BY_BANK)");
    }
    
    @Test
    public void testPayPageCardPaymentResponse() throws SAXException, IOException, ParserConfigurationException {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU4Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTYyMTQ2NTk5MDwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT43NjQ4Nzc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4NCg==";
 
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
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
        assertEquals(response.getResultCode(), "0 (ORDER_ACCEPTED)");
    }
         
    @Test
    public void testSetErrorParamsCode101() throws SAXException, IOException, ParserConfigurationException {
    	String responseXmlBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU4Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTYyMTQ2NTk5MDwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT43NjQ4Nzc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4xMDE8L3N0YXR1c2NvZGU+DQo8L3Jlc3BvbnNlPg==";
    	SveaResponse response = new SveaResponse(responseXmlBase64,  null);
    	assertEquals(response.getErrorMessage(), "Invalid XML.");
    }
    //Not used
  /*  private WebResponse postRequest(String sveaUrl, PaymentForm form) throws IOException, SAXException {
        WebConversation conversation = new WebConversation();
        WebRequest request = new PostMethodWebRequest(sveaUrl);
        form.setMacSha512(HashUtil.createHash(form.getXmlMessageBase64() + order.config.getSecretWord(), HASHALGORITHM.SHA_512));
        request.setParameter("mac", form.getMacSha512());
        request.setParameter("message", form.getXmlMessageBase64());
        request.setParameter("merchantid", form.getMerchantId());
        return conversation.getResponse(request);
    }*/
}
