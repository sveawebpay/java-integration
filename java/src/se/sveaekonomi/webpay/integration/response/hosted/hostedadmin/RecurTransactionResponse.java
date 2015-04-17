package se.sveaekonomi.webpay.integration.response.hosted.hostedadmin;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

/**
 * RecurTransactionResponse handles the recur transaction response
 * 
 * @author Kristian Grossman-Madsen
 */
public class RecurTransactionResponse extends HostedAdminResponse implements Respondable {

	private String rawResponse;

    /** the order id at Svea */
    private Long transactionId;
    /** the customer reference number, i.e. order number */
    private String clientOrderNumber;
    /** paymentMethod */
    private String paymentMethod;
    /** the merchant id */
    private String merchantId;    
    /** the total amount */
    private Double amount;
    /** currency -- ISO 4217 alphabetic, e.g. SEK */
    private String currency;
    /** cardType */
    private String cardType;
    /** maskedCardNumber */
    private String maskedCardNumber;
    /**  expiryMonth -- Expire month of the month */
    private String expiryMonth;
    /** expiryYear -- Expire year of the card */
    private String expiryYear;
    /** EDB authorization code */
    private String authCode; 
    /** subscriptionId */
    private Long subscriptionId;	

    
    public String getRawResponse() {
 		return rawResponse;
 	}

 	public Long getTransactionId() {
 		return transactionId;
 	}

 	public void setTransactionId(Long transactionId) {
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

 	public Double getAmount() {
 		return amount;
 	}

 	public void setAmount(Double amount) {
 		this.amount = amount;
 	}

 	public String getCurrency() {
 		return currency;
 	}

 	public void setCurrency(String currency) {
 		this.currency = currency;
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

 	public Long getSubscriptionId() {
 		return subscriptionId;
 	}

 	public void setSubscriptionId(Long subscriptionId) {
 		this.subscriptionId = subscriptionId;
 	} 

	public RecurTransactionResponse(String message, String mac, String secret ) {
		super(message, mac, secret);
		this.rawResponse = this.xml;		
		this.setValues();
	} 	
 	
	@Deprecated
	public RecurTransactionResponse(String responseXmlBase64, String secretWord) {
		super(responseXmlBase64, secretWord);
		this.rawResponse = this.xml;		
		this.setValues();
	}

	void setValues() {		
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//<?xml version='1.0' encoding='UTF-8'?>
		//<response>
		//	<transaction id="593951">
		//		<paymentmethod>CARD</paymentmethod>
		//		<merchantid>1130</merchantid>
		//		<customerrefno>recur1423569086456</customerrefno>
		//		<amount>25000</amount>
		//		<currency>SEK</currency>
		//		<subscriptionid>3188</subscriptionid>
		//		<cardtype>VISA</cardtype>
		//		<maskedcardno>444433xxxxxx1100</maskedcardno>
		//		<expirymonth>01</expirymonth>
		//		<expiryyear>17</expiryyear>
		//		<authcode>491419</authcode>
		//	</transaction>
		//	<statuscode>0</statuscode>
		//</response>
				
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document d1 = builder.parse(new InputSource(new StringReader(xml)));
			NodeList nodeList = d1.getElementsByTagName("response");
			int size = nodeList.getLength();

			for (int i = 0; i < size; i++) {
				Element element = (Element) nodeList.item(i);

				int status = Integer.parseInt(getTagValue(element, "statuscode"));
				if (status == 0) {
					this.setOrderAccepted(true);
					this.setResultCode("0 (ORDER_ACCEPTED)");
				} else {
					this.setOrderAccepted(false);
					setErrorParams(status);
				}
				
				if( this.isOrderAccepted() ) {	// don't attempt to parse a bad response
					this.transactionId = Long.valueOf(getTagAttribute(element, "transaction", "id"));
					this.clientOrderNumber = getTagValue(element, "customerrefno");
					this.paymentMethod = getTagValue(element, "paymentmethod");
					this.merchantId = getTagValue(element, "merchantid");
					this.amount = Double.valueOf(getTagValue(element, "amount"))/100.00;
					this.currency = getTagValue(element, "currency");
					this.cardType = getTagValue(element, "cardtype");
					this.maskedCardNumber = getTagValue(element, "maskedcardno");
					this.expiryMonth = getTagValue(element, "expirymonth");
					this.expiryYear = getTagValue(element, "expiryyear");
					this.authCode = getTagValue(element, "authcode");
					this.subscriptionId = Long.valueOf(getTagValue(element, "subscriptionid"));
				}
			}
		} catch (ParserConfigurationException e) {
			throw new SveaWebPayException("ParserConfigurationException", e);
		} catch (SAXException e) {
			throw new SveaWebPayException("SAXException", e);
		} catch (IOException e) {
			throw new SveaWebPayException("IOException", e);
		}
	}
}

