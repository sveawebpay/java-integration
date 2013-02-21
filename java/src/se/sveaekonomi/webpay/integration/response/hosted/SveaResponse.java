package se.sveaekonomi.webpay.integration.response.hosted;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.Response;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

/**
 * Handles the asynchronous response from the hosted payment solution 
 * @author klar-sar
 *
 */
public class SveaResponse extends Response {
    public final SveaConfig config = new SveaConfig();
    
    private String xml;
    private String transactionId;
    private String clientOrderNumber;
    private String paymentMethod;
    private String merchantId;
    private double amount;
    private String currency;
    private String subscriptionId;
    private String subscriptionType;
    private String cardType;
    private String maskedCardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String authCode;
    
    
    public SveaResponse(String responseXmlBase64, String mac, String secretWord) throws SAXException, IOException, ParserConfigurationException {
        super();
        
        if(validateMac(responseXmlBase64, mac, secretWord))
                this.setValues(responseXmlBase64);
        else {
            this.setOrderAccepted(false);
            this.setErrorMessage("Response failed authorization. MAC not valid.");
        }          
    }         
    
    private boolean validateMac(String responseXmlBase64, String mac, String secret) {
        if(secret == null) {
            secret = config.getSecretWord();
        }
        String macKey = HashUtil.createHash(responseXmlBase64 + secret, HASHALGORITHM.SHA_512);
        
        if(mac.equals(macKey)) {
            return true;
        }
        return false;   
        
        
    }

    private void setValues(String xmlBase64) throws SAXException, IOException, ParserConfigurationException {
        String xml = Base64Util.decodeBase64String(xmlBase64);
        this.setXml(xml);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document d1 = builder.parse(new InputSource(new StringReader(xml)));
        //
        NodeList nodeList = d1.getElementsByTagName("response");
        try {
            int size = nodeList.getLength();
            
            for (int i = 0; i < size; i++) {
                Element element = (Element)nodeList.item(i);
                int status = Integer.parseInt(getTagValue(element, "statuscode"));
                if(status==0)
                    this.setOrderAccepted(true);
                else {
                    this.setOrderAccepted(false);
                    this.setResultCode(status);                    
                }
                                
                this.transactionId = getTagAttribute(element, "transaction", "id");
                this.paymentMethod = getTagValue(element, "paymentmethod");
                this.merchantId = getTagValue(element, "merchantid");
                this.clientOrderNumber = getTagValue(element, "customerrefno");
                int minorAmount = Integer.parseInt(getTagValue(element, "amount"));
                this.amount = minorAmount * 0.01;
                this.currency = getTagValue(element, "currency");
                this.setSubscriptionId(getTagValue(element, "subscriptionid"));
                this.setSubscriptionType(getTagValue(element, "subscriptiontype"));
                this.setCardType(getTagValue(element, "cardtype"));
                this.setMaskedCardNumber(getTagValue(element, "maskedcardno"));
                this.setExpiryMonth(getTagValue(element, "expirymonth"));
                this.setExpiryYear(getTagValue(element, "expiryyear"));
                this.setAuthCode(getTagValue(element, "authcode"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
    
    private String getTagAttribute(Element elementNode, String tagName, String attributeName) {
        String[] temp;      
        Node trans =  elementNode.getElementsByTagName(tagName).item(0);   
        NamedNodeMap attr = trans.getAttributes();
        Node nodeAttr = attr.getNamedItem("id");
        String node = nodeAttr.toString();
        temp = node.split("=");
        return temp[1].substring(1, temp[1].length()-1);                                   
    }
    
    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return ((Node) textList.item(0)).getNodeValue().trim();
        }
        return null;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getClientOrderNumber() {
        return clientOrderNumber;
    }
    public void setClientOrderNumber(String clientOrderNumber) {
        this.clientOrderNumber = clientOrderNumber;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getMerchantId() {
        return merchantId;
    }
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }       
}
