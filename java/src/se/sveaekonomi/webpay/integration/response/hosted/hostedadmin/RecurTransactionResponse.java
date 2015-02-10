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
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * RecurTransactionResponse handles the recur transaction response
 * 
 * @author Kristian Grossman-Madsen
 */
public class RecurTransactionResponse extends HostedAdminResponse implements Respondable {

	private String rawResponse;

    /** the order id at Svea */
    public String transactionId;
    /** the customer reference number, i.e. order number */
    public String clientOrderNumber;
    /** paymentMethod */
    public String paymentMethod;	// TODO use PAYMENTMETHOD
    /** the merchant id */
    public String merchantId;    
    /** the total amount in minor currency (e.g. SEK 10.50 => 1050) */
    public String amount;
    /** currency -- ISO 4217 alphabetic, e.g. SEK */
    public String currency;
    /** cardType */
    public String cardType;
    /** maskedCardNumber */
    public String maskedCardNumber;
    /**  expiryMonth -- Expire month of the month */
    public String expiryMonth;
    /** expiryYear -- Expire year of the card */
    public String expiryYear;
    /** EDB authorization code */
    public String authCode; 
    /** subscriptionId */
    public String subscriptionId;
    /** decimalamount The total amount including VAT, presented as a decimal number. */
    public Double decimalamount;	
	

	public RecurTransactionResponse(String responseXmlBase64, String secretWord) {
		super(responseXmlBase64, secretWord);
		this.rawResponse = this.xml;		
		this.setValues();
	}

	/** 
	 * implement this to parse xml and set attributes according to response attributes 
	 */
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
					this.transactionId = getTagAttribute(element, "transaction", "id");
					this.clientOrderNumber = getTagValue(element, "customerrefno");
					this.paymentMethod = getTagValue(element, "paymentmethod");
					this.merchantId = getTagValue(element, "merchantid");
					this.amount = getTagValue(element, "amount");
					this.currency = getTagValue(element, "currency");
					this.cardType = getTagValue(element, "cardtype");
					this.maskedCardNumber = getTagValue(element, "maskedcardno");
					this.expiryMonth = getTagValue(element, "expirymonth");
					this.expiryYear = getTagValue(element, "expiryyear");
					this.authCode = getTagValue(element, "authcode");
					this.subscriptionId = getTagValue(element, "subscriptionid");
					this.decimalamount = Double.valueOf(getTagValue(element, "amount"))/100.00;
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
	
    public String getRawResponse() {
		return rawResponse;
	} 

}

