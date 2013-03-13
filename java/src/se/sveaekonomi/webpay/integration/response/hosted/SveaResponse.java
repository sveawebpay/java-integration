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
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;

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
    
    
    public SveaResponse(String responseXmlBase64,/*, String mac,*/ String secretWord) throws SAXException, IOException, ParserConfigurationException {
        super();
                this.setValues(responseXmlBase64);
              
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
                if(status==0) {                	
                    this.setOrderAccepted(true);
                	this.setResultCode("0 (ORDER_ACCEPTED)");
                }
                else {
                    this.setOrderAccepted(false);                   
                    setErrorParams(status);
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
    public void setCurrency(CURRENCY currency) {
        this.currency = currency.toString();
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
    
    private void setErrorParams(int resultCode) {
    	switch(resultCode)
    	{
    	case 1:
    		this.setResultCode(resultCode + " (REQUIRES_MANUAL_REVIEW)");
    		this.setErrorMessage("Request performed successfully but requires manual review from merchant. Applicable paymentmethods: PAYPAL.");    	
    		break;
    	case 100:
    		this.setResultCode(resultCode + " (INTERNAL_ERROR)");
    		this.setErrorMessage("Invalid – contact integrator.");
    		break;
    	case 101:
    		this.setResultCode(resultCode + " (XMLPARSEFAIL)");
    		this.setErrorMessage("Invalid XML.");
    		break;
    	case 102:
    		this.setResultCode(resultCode + " (ILLEGAL_ENCODING)");
    		this.setErrorMessage("Invalid encoding.");
    		break;
    	case 104:
    		this.setResultCode(resultCode + " (ILLEGAL_URL)");
    		this.setErrorMessage("Illegal Url.");
    		break;
    	case 105:
    		this.setResultCode(resultCode + " (ILLEGAL_TRANSACTIONSTATUS)");
    		this.setErrorMessage("Invalid transaction status.");
    		break;
    	case 106:
    		this.setResultCode(resultCode + " (EXTERNAL_ERROR)");
    		this.setErrorMessage("Failure at third party e.g. failure at the bank.");
    		break;
    	case 107:
    		this.setResultCode(resultCode + " (DENIED_BY_BANK)");
    		this.setErrorMessage("Transaction rejected by bank.");
    		break;
    	case 108:
    		this.setResultCode(resultCode + " (CANCELLED)");
    		this.setErrorMessage("Transaction cancelled.");
    		break;
    	case 109:
    		this.setResultCode(resultCode + " (NOT_FOUND_AT_BANK)");
    		this.setErrorMessage("Transaction not found at the bank.");
    		break;
    	case 110:
    		this.setResultCode(resultCode + " (ILLEGAL_TRANSACTIONID)");
    		this.setErrorMessage("Invalid transaction ID.");
    		break;
    	case 111:
    		this.setResultCode(resultCode + " (MERCHANT_NOT_CONFIGURED)");
    		this.setErrorMessage("Merchant not configured.");
    		break;
    	case 112:
    		this.setResultCode(resultCode + " (MERCHANT_NOT_CONFIGURED_AT_BANK)");
    		this.setErrorMessage("Merchant not configured at the bank.");
    		break;
    	case 113:
    		this.setResultCode(resultCode + " (PAYMENTMETHOD_NOT_CONFIGURED)");
    		this.setErrorMessage("Payment method not configured for merchant.");
    		break;
    	case 114:
    		this.setResultCode(resultCode + " (TIMEOUT_AT_BANK)");
    		this.setErrorMessage("Timeout at the bank.");
    		break;
    	case 115:
    		this.setResultCode(resultCode + " (MERCHANT_NOT_ACTIVE)");
    		this.setErrorMessage("The merchant is disabled.");
    		break;
    	case 116:
    		this.setResultCode(resultCode + " (PAYMENTMETHOD_NOT_ACTIVE)");
    		this.setErrorMessage("The payment method is disabled.");
    		break;
    	case 117:
    		this.setResultCode(resultCode + " (ILLEGAL_AUTHORIZED_AMOUNT)");
    		this.setErrorMessage("Invalid authorized amount.");
    		break;
    	case 118:
    		this.setResultCode(resultCode + " (ILLEGAL_CAPTURED_AMOUNT)");
    		this.setErrorMessage("Invalid captured amount.");
    		break;
    	case 119:
    		this.setResultCode(resultCode + " (ILLEGAL_CREDITED_AMOUNT)");
    		this.setErrorMessage("Invalid credited amount.");
    		break;
    	case 120:
    		this.setResultCode(resultCode + " (NOT_SUFFICIENT_FUNDS)");
    		this.setErrorMessage("Not enough founds.");
    		break;
    	case 121:
    		this.setResultCode(resultCode + " (EXPIRED_CARD)");
    		this.setErrorMessage("The card has expired.");
    		break;
    	case 122:
    		this.setResultCode(resultCode + " (STOLEN_CARD)");
    		this.setErrorMessage("Stolen card.");
    		break;
    	case 123:
    		this.setResultCode(resultCode + " (LOST_CARD)");
    		this.setErrorMessage("Lost card.");
    		break;
    	case 124:
    		this.setResultCode(resultCode + " (EXCEEDS_AMOUNT_LIMIT)");
    		this.setErrorMessage("Amount exceeds the limit.");
    		break;
    	case 125:
    		this.setResultCode(resultCode + " (EXCEEDS_FREQUENCY_LIMIT)");
    		this.setErrorMessage("Frequency limit exceeded.");
    		break;
    	case 126:
    		this.setResultCode(resultCode + " (TRANSACTION_NOT_BELONGING_TO_MERCHANT)");
    		this.setErrorMessage("Transaction does not belong to merchant.");
    		break;
    	case 127:
    		this.setResultCode(resultCode + " (CUSTOMERREFNO_ALREADY_USED)");
    		this.setErrorMessage("Customer reference number already used in another transaction.");
    		break;
    	case 128:
    		this.setResultCode(resultCode + " (NO_SUCH_TRANS)");
    		this.setErrorMessage("Transaction does not exist.");
    		break;
    	case 129:
    		this.setResultCode(resultCode + " (DUPLICATE_TRANSACTION)");
    		this.setErrorMessage("More than one transaction found for the given customer reference number.");
    		break;
    	case 130:
    		this.setResultCode(resultCode + " (ILLEGAL_OPERATION)");
    		this.setErrorMessage("Operation not allowed for the given payment method.");
    		break;    	
    	case 131:
    		this.setResultCode(resultCode + " (COMPANY_NOT_ACTIVE)");
    		this.setErrorMessage("Company inactive.");
    		break;
    	case 132:
    		this.setResultCode(resultCode + " (SUBSCRIPTION_NOT_FOUND)");
    		this.setErrorMessage("No subscription exist.");
    		break;
    	case 133:
    		this.setResultCode(resultCode + " (SUBSCRIPTION_NOT_ACTIVE)");
    		this.setErrorMessage("Subscription not active.");
    		break;
    	case 134:
    		this.setResultCode(resultCode + " (SUBSCRIPTION_NOT_SUPPORTED)");
    		this.setErrorMessage("Payment method doesn’t support subscriptions.");
    		break;
    	case 135:
    		this.setResultCode(resultCode + " (ILLEGAL_DATE_FORMAT)");
    		this.setErrorMessage("Illegal date format.");
    		break;
    	case 136:
    		this.setResultCode(resultCode + " (ILLEGAL_RESPONSE_DATA)");
    		this.setErrorMessage("Illegal response data.");
    		break;
    	case 137:
    		this.setResultCode(resultCode + " (IGNORE_CALLBACK)");
    		this.setErrorMessage("Ignore callback.");
    		break;
    	case 138:
    		this.setResultCode(resultCode + " (CURRENCY_NOT_CONFIGURED)");
    		this.setErrorMessage("Currency not configured.");
    		break;
    	case 139:
    		this.setResultCode(resultCode + " (CURRENCY_NOT_ACTIVE)");
    		this.setErrorMessage("Currency not active.");
    		break;
    	case 140:
    		this.setResultCode(resultCode + " (CURRENCY_ALREADY_CONFIGURED)");
    		this.setErrorMessage("Currency is already configured.");
    		break;
    	case 141:
    		this.setResultCode(resultCode + " (ILLEGAL_AMOUNT_OF_RECURS_TODAY)");
    		this.setErrorMessage("Ilegal amount of recurs per day.");
    		break;
    	case 142:
    		this.setResultCode(resultCode + " (NO_VALID_PAYMENT_METHODS)");
    		this.setErrorMessage("No valid paymentmethods.");
    		break;
    	case 143:
    		this.setResultCode(resultCode + " (CREDIT_DENIED_BY_BANK)");
    		this.setErrorMessage("Credit denied by bank.");
    		break;
    	case 144:
    		this.setResultCode(resultCode + " (ILLEGAL_CREDIT_USER)");
    		this.setErrorMessage("User is not allowed to perform credit operation.");
    		break;
    	case 300:
    		this.setResultCode(resultCode + " (BAD_CARDHOLDER_NAME)");
    		this.setErrorMessage("Invalid value for cardholder name.");
    		break;
    	case 301:
    		this.setResultCode(resultCode + " (BAD_TRANSACTION_ID)");
    		this.setErrorMessage("Invalid value for transaction id.");
    		break;
    	case 302:
    		this.setResultCode(resultCode + " (BAD_REV)");
    		this.setErrorMessage("Invalid value for rev.");
    		break;
    	case 303:
    		this.setResultCode(resultCode + " (BAD_MERCHANT_ID)");
    		this.setErrorMessage("nvalid value for merchant id.");
    		break;
    	case 304:
    		this.setResultCode(resultCode + " (BAD_LANG)");
    		this.setErrorMessage("Invalid value for lang.");
    		break;
    	case 305:
    		this.setResultCode(resultCode + " (BAD_AMOUNT)");
    		this.setErrorMessage("Invalid value for amount.");
    		break;
    	case 306:
    		this.setResultCode(resultCode + " (BAD_CUSTOMERREFNO)");
    		this.setErrorMessage("Invalid value for customer refno.");
    		break;
    	case 307:
    		this.setResultCode(resultCode + " (BAD_CURRENCY)");
    		this.setErrorMessage("Invalid value for currency.");
    		break;
    	case 308:
    		this.setResultCode(resultCode + " (BAD_PAYMENTMETHOD)");
    		this.setErrorMessage("Invalid value for payment method.");
    		break;
    	case 309:
    		this.setResultCode(resultCode + " (BAD_RETURNURL)");
    		this.setErrorMessage("Invalid value for return url.");
    		break;
    	case 310:
    		this.setResultCode(resultCode + " (BAD_LASTBOOKINGDAY)");
    		this.setErrorMessage("Invalid value for last booking day.");
    		break;
    	case 311:
    		this.setResultCode(resultCode + " (BAD_MAC)");
    		this.setErrorMessage("Invalid value for mac.");
    		break;
    	case 312:
    		this.setResultCode(resultCode + " (BAD_TRNUMBER)");
    		this.setErrorMessage("Invalid value for tr number.");
    		break;
    	case 313:
    		this.setResultCode(resultCode + " (BAD_AUTHCODE)");
    		this.setErrorMessage("Invalid value for authcode.");
    		break;
    	case 314:
    		this.setResultCode(resultCode + " (BAD_CC_DESCR)");
    		this.setErrorMessage("Invalid value for cc_descr.");
    		break;
    	case 315:
    		this.setResultCode(resultCode + " (BAD_ERROR_CODE)");
    		this.setErrorMessage("Invalid value for error_code.");
    		break;
    	case 316:
    		this.setResultCode(resultCode + " (BAD_CARDNUMBER_OR_CARDTYPE_NOT_CONFIGURED)");
    		this.setErrorMessage("Card type not configured for merchant.");
    		break;
    	case 317:
    		this.setResultCode(resultCode + " (BAD_SSN)");
    		this.setErrorMessage("Invalid value for ssn.");
    		break;
    	case 318:
    		this.setResultCode(resultCode + " (BAD_VAT)");
    		this.setErrorMessage("Invalid value for vat.");
    		break;
    	case 319:
    		this.setResultCode(resultCode + " (BAD_CAPTURE_DATE)");
    		this.setErrorMessage("Invalid value for capture date.");
    		break;
    	case 320:
    		this.setResultCode(resultCode + " (BAD_CAMPAIGN_CODE_INVALID)");
    		this.setErrorMessage("Invalid value for campaign code. There are no valid matching campaign codes.");
    		break;
    	case 321:
    		this.setResultCode(resultCode + " (BAD_SUBSCRIPTION_TYPE)");
    		this.setErrorMessage("Invalid subscription type.");
    		break;
    	case 322:
    		this.setResultCode(resultCode + " (BAD_SUBSCRIPTION_ID)");
    		this.setErrorMessage("Invalid subscription id.");
    		break;
    	case 323:
    		this.setResultCode(resultCode + " (BAD_BASE64)");
    		this.setErrorMessage("Invalid base64.");
    		break;
    	case 324:
    		this.setResultCode(resultCode + " (BAD_CAMPAIGN_CODE)");
    		this.setErrorMessage("Invalid campaign code. Missing value.");
    		break;
    	case 325:
    		this.setResultCode(resultCode + " (BAD_CALLBACKURL)");
    		this.setErrorMessage("Invalid callbackurl.");
    		break;
    	case 326:
    		this.setResultCode(resultCode + " (THREE_D_CHECK_FAILED)");
    		this.setErrorMessage("3D check failed.");
    		break;
    	case 327:
    		this.setResultCode(resultCode + " (CARD_NOT_ENROLLED)");
    		this.setErrorMessage("Card not enrolled in 3D secure.");
    		break;
    	case 328:
    		this.setResultCode(resultCode + " (BAD_IPADDRESS)");
    		this.setErrorMessage("Provided ip address is incorrect.");
    		break;
    	case 329:
    		this.setResultCode(resultCode + " (BAD_MOBILE)");
    		this.setErrorMessage("Bad mobile phone number.");
    		break;
    	case 330:
    		this.setResultCode(resultCode + " (BAD_COUNTRY)");
    		this.setErrorMessage("Bad country parameter.");
    		break;
    	case 331:
    		this.setResultCode(resultCode + " (THREE_D_CHECK_NOT_AVAILABLE)");
    		this.setErrorMessage("Merchants 3D configuration invalid.");
    		break;
    	case 332:
    		this.setResultCode(resultCode + " (TIMEOUT)");
    		this.setErrorMessage("Timeout at Svea.");
    		break;
    	case 500:
    		this.setResultCode(resultCode + " (ANTIFRAUD_CARDBIN_NOT_ALLOWED)");
    		this.setErrorMessage("Antifraud - cardbin not allowed.");
    		break;
    	case 501:
    		this.setResultCode(resultCode + " (ANTIFRAUD_IPLOCATION_NOT_ALLOWED)");
    		this.setErrorMessage("Antifraud – iplocation not allowed.");
    		break;
    	case 502:
    		this.setResultCode(resultCode + " (ANTIFRAUD_IPLOCATION_AND_BIN_DOESNT_MATCH)");
    		this.setErrorMessage("Antifraud – ip-location and bin does not match.");
    		break;
    	case 503:
    		this.setResultCode(resultCode + " (ANTIFRAUD_MAX_AMOUNT_PER_IP_EXCEEDED)");
    		this.setErrorMessage("Antofraud – max amount per ip exceeded.");
    		break;
    	case 504:
    		this.setResultCode(resultCode + " (ANTIFRAUD_MAX_TRANSACTIONS_PER_IP_EXCEEDED)");
    		this.setErrorMessage("Antifraud – max transactions per ip exceeded.");
    		break;
    	case 505:
    		this.setResultCode(resultCode + " (ANTIFRAUD_MAX_TRANSACTIONS_PER_CARDNO_EXCEEDED)");
    		this.setErrorMessage("Antifraud – max transactions per card number exceeded.");
    		break;
    	case 506:
    		this.setResultCode(resultCode + " (ANTIFRAUD_MAX_AMOUNT_PER_CARDNO_EXCEEDED)");
    		this.setErrorMessage("Antifraud – max amount per cardnumer exceeded.");
    		break;
    	case 507:
    		this.setResultCode(resultCode + " (ANTIFRAUD_IP_ADDRESS_BLOCKED)");
    		this.setErrorMessage("Antifraud – IP address blocked.");
    		break;
    	default:
    		this.setResultCode(resultCode + " (UNKNOWN_ERROR)");
    		this.setErrorMessage("Unknown error.");
    		break;
    	}
    	
    }
}
