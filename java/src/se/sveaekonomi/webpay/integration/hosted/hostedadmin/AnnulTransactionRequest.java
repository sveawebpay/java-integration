package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

/**
 * AnnulTransaction is used to cancel (annul) a card transaction. 
 * The transaction must have status AUTHORIZED or CONFIRMED at Svea.
 * After a successful request the transaction will get the status ANNULLED.
 *
 * @author Kristian Grossman-Madsen
 */
public class AnnulTransactionRequest extends HostedAdminRequest {

	public String transactionId;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}	

	public AnnulTransactionRequest( ConfigurationProvider config ) {
		super( config, "annul" );
	}

//	public prepareRequest() {
//		
//		// check that all required setX() methods have been used
//		this.validateRequest();
//	
//		// create message xml
//		
//		// create message mac for the configured merchantid 
//		
//		// return request post fields
//	}
	
	
    /** 
     * writes xml for hosted webservice "annul" request message
     */
	public String getAnnulTransactionXml(AnnulTransactionRequest request) {
		
	    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");

			xmlw.writeStartDocument("UTF-8", "1.0");
			xmlw.writeComment("Message generated by Integration package Java");
			xmlw.writeStartElement("annul");			
				xmlw.writeStartElement("transactionid");
					xmlw.writeCharacters( request.getTransactionId() );
				xmlw.writeEndElement();
			xmlw.writeEndElement();			
			xmlw.writeEndDocument();			
			xmlw.close();				
		}
		catch (XMLStreamException e) {
			throw new SveaWebPayException("Error when building XML", e);
		}	

		try {
			return new String(os.toByteArray(), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
				throw new SveaWebPayException("Unsupported encoding UTF-8", e);
		}
	}

	
}
