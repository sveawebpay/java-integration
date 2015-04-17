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
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.RecurTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

/**
 * RecurTransaction is used to send a recurring payment request to Svea, using a previously set up subscription id.
 * 
 * Note: If recur is to an international acquirer, the currency for the recurring transaction must be the same as for the registration transaction.
 * Note: If subscriptiontype is either RECURRING or RECURRINGCAPTURE, the amount must be given in the same currency as the initial transaction. 
 *
 * @author Kristian Grossman-Madsen
 */
public class RecurTransactionRequest extends HostedAdminRequest<RecurTransactionRequest> {

	String amount;
	String customerRefNo;
	String subscriptionId;
	String currency;
	
	public RecurTransactionRequest setAmount(String amount) {
		this.amount = amount;
		return this;
	}
	public String getAmount() {
		return this.amount;
	}
	
	public RecurTransactionRequest setCustomerRefNo(String customerRefNo) {
		this.customerRefNo = customerRefNo;
		return this;
	}
	public String getCustomerRefNo() {
		return this.customerRefNo;
	}
	
	public RecurTransactionRequest setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		return this;
	}
	public String getSubscriptionId() {
		return this.subscriptionId;
	}
	
	public RecurTransactionRequest setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	public String getCurrency() {
		return this.currency;
	}
	
	public RecurTransactionRequest( ConfigurationProvider config ) {
		super(config, "recur");
	}
	
	/**
	 * should return the request message xml for the method in question
	 */
	public String getRequestMessageXml( ConfigurationProvider config ) {

		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");

			xmlw.writeStartDocument("UTF-8", "1.0");
			xmlw.writeComment( GetRequestProperties.getLibraryAndPlatformPropertiesAsJson(config) );
				xmlw.writeStartElement("recur");
					xmlw.writeStartElement("amount");
						xmlw.writeCharacters(this.getAmount());
					xmlw.writeEndElement();
					xmlw.writeStartElement("customerrefno");
						xmlw.writeCharacters(this.getCustomerRefNo());
					xmlw.writeEndElement();			
					xmlw.writeStartElement("subscriptionid");
						xmlw.writeCharacters(this.getSubscriptionId());
					xmlw.writeEndElement();
					if( this.getCurrency() != null ) {
						xmlw.writeStartElement("currency");
							xmlw.writeCharacters(this.getCurrency());
						xmlw.writeEndElement();
					}
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
	 * should return string indicating any missing order builder setter methods on validation failure, or empty string
	 */
	public String validateRequest() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }        
        if (this.getAmount() == null) {
            errors += "MISSING VALUE - amount is required. Use function setAmount().\n";
    	}           
        if (this.getCustomerRefNo() == null) {
            errors += "MISSING VALUE - customerRefNo is required. Use function setCustomerRefNo (also check setClientOrderNumber in order builder).\n";
    	}        
        if (this.getSubscriptionId() == null) {
            errors += "MISSING VALUE - subscriptionId is required. Use function setSubscriptionId() with the subscriptionId from the createOrder response.\n";
    	}
        return errors;    
    }
	
	/**
	 * returns the request fields to post to service
	 */
	public Hashtable<String,String> prepareRequest() {

    	// validate request and throw exception if validation fails
        String errors = validateRequest();
        
        if (!errors.equals("")) {
        	//System.out.println(errors);
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
	 * @return RecurTransactionResponse
	 * @throws SveaWebPayException
	 */
	public RecurTransactionResponse doRequest() throws SveaWebPayException {

		try {
			// prepare request fields
	    	Hashtable<String, String> requestFields = this.prepareRequest();

	    	// send request 
	    	String xmlResponse = sendHostedAdminRequest(requestFields);
	
	    	// parse response	
			return new RecurTransactionResponse( getResponseMessageFromXml(xmlResponse), getResponseMacFromXml(xmlResponse), this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode()));
			
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
