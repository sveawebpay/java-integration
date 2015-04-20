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
 * Handles lowertransaction request response from hosted admin webservice
 * 
 * @author Kristian Grossman-Madsen
 */
public class LowerTransactionResponse extends HostedAdminResponse implements Respondable {

	private String rawResponse;
	private Long transactionid;
	private String clientOrderNumber;

	
    public String getRawResponse() {
		return rawResponse;
	}

    public Long getTransactionId() {
		return transactionid;
	}

	public String getClientOrderNumber() {
		return clientOrderNumber;
	}
    
	
	public LowerTransactionResponse(String message, String mac, String secret ) {
		super(message, mac, secret);
		this.rawResponse = this.xml;		
		this.setValues();
	}
	
	@Deprecated
	public LowerTransactionResponse(String responseXmlBase64, String secretWord) {
		super(responseXmlBase64, secretWord);
		this.rawResponse = this.xml;		
		this.setValues();
	}

	void setValues() {		
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

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

					this.transactionid = Long.valueOf(getTagAttribute(element, "transaction", "id"));
					this.clientOrderNumber = getTagValue(element, "customerrefno");
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

