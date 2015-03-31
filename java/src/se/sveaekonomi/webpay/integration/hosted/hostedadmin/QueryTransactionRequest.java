package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

public class QueryTransactionRequest extends HostedAdminRequest<QueryTransactionRequest> {

	/** Required. */
	public String transactionId;
	
	public String getTransactionId() {
		return transactionId;
	}

	public QueryTransactionRequest setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public QueryTransactionRequest( ConfigurationProvider config ) {		
		super(config, "querytransactionid");
	}

	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @return indicating which methods are missing, or empty String if no problems found
	 */
	public String validateRequest() {
		String errors = "";		
		errors += validateRequestId();
		errors += validateCountryCode();
		return errors;
	}
	
    private String validateRequestId() {
    	String tid = this.getTransactionId();
    	return (tid == null) ? "MISSING VALUE - setOrderId is required.\n" : "";
    }

    private String validateCountryCode() {
        return (this.getCountryCode() == null) ? "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" : "";
    }	
    

    /**
	 * returns xml for hosted webservice "query" request
	 */
	public String getRequestMessageXml( ConfigurationProvider config ) {

		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");

			xmlw.writeStartDocument("UTF-8", "1.0");
			xmlw.writeComment( GetRequestProperties.getLibraryAndPlatformPropertiesAsJson(config) );
				xmlw.writeStartElement("querytransactionid");
					xmlw.writeStartElement("transactionid");
						xmlw.writeCharacters( this.getTransactionId() );
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
	
	/**
	 * returns the request fields to post to service
	 */
	public Hashtable<String,String> prepareRequest() {

    	// validate request and throw exception if validation fails
        String errors = validateRequest();
        
        if (!errors.equals("")) {
        	System.out.println(errors);
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
        
        // build inspectable request object and return
		Hashtable<String,String> requestFields = new Hashtable<>();

		String merchantId = this.config.getMerchantId(PAYMENTTYPE.HOSTED, this.getCountryCode());
		String secretWord = this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode());		
		
    	String xmlMessage = getRequestMessageXml(this.config);
    	String xmlMessageBase64 = Base64Util.encodeBase64String(xmlMessage);
    	String macSha512 =  HashUtil.createHash(xmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512);			

    	requestFields.put("message", xmlMessageBase64);
    	requestFields.put("mac", macSha512);
    	requestFields.put("merchantid", merchantId);
    	
		return requestFields;
	}

	/**
	 * validate, prepare and do request
	 * @return ConfirmTransactionResponse
	 * @throws SveaWebPayException
	 */
	public QueryTransactionResponse doRequest() throws SveaWebPayException {

		try {
			// prepare request fields
	    	Hashtable<String, String> requestFields = this.prepareRequest();

	    	// send request 
	    	String xmlResponse = sendHostedAdminRequest(requestFields);
	
	    	// parse response	
			return new QueryTransactionResponse( getResponseMessageFromXml(xmlResponse), this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode()) );
			
	    } catch (IllegalStateException ex) {
	        throw new SveaWebPayException("IllegalStateException", ex);
	    } 
		catch (IOException ex) {
			//System.out.println(ex.toString());
			//System.out.println(((HttpResponseException)ex).getStatusCode());
	        throw new SveaWebPayException("IOException", ex);
	    }		
	}
}
