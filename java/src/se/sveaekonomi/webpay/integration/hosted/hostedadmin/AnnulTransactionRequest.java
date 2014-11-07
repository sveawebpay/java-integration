package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * AnnulTransaction is used to cancel (annul) a card transaction. The
 * transaction must have status AUTHORIZED or CONFIRMED at Svea. After a
 * successful request the transaction will get the status ANNULLED.
 * 
 * @author Kristian Grossman-Madsen
 */
public class AnnulTransactionRequest extends HostedAdminRequest<CancelOrderBuilder> {

	String transactionId;

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public AnnulTransactionRequest(CancelOrderBuilder order) {
		super(order, "annul");
	}

	/**
	 * returns xml for hosted webservice "annul" request
	 */
	public String getRequestMessageXml() {

		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");

			xmlw.writeStartDocument("UTF-8", "1.0");
				xmlw.writeComment("Message generated by Integration package Java");
				xmlw.writeStartElement("annul");
					xmlw.writeStartElement("transactionid");
						xmlw.writeCharacters(((CancelOrderBuilder)this.order).getOrderId().toString());
					xmlw.writeEndElement();
				xmlw.writeEndElement();
			xmlw.writeEndDocument();
			xmlw.close();
		} catch (XMLStreamException e) {
			throw new SveaWebPayException("Error when building XML", e);
		}

		try {
			return new String(os.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SveaWebPayException("Unsupported encoding UTF-8", e);
		}
	}

	// parse response message into AnnulTransactionResponse
	@SuppressWarnings("unchecked")
	public AnnulTransactionResponse parseResponse(String message) {
		return new AnnulTransactionResponse(message, this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.order.getCountryCode()));
	}

    public String validateOrder() {
        String errors = "";
        if (this.order.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (((CancelOrderBuilder)this.order).getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        return errors;    
    }	
	
}
