package se.sveaekonomi.webpay.integration.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class CreditOrderRowsResponse extends AdminServiceResponse {


	public CreditOrderRowsResponse(SOAPMessage soapResponse) throws SOAPException {
		// set common response attributes
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		// TODO
    	}
	}
}
