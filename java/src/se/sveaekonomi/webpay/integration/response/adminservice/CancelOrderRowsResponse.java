package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class CancelOrderRowsResponse extends AdminServiceResponse {

	public CancelOrderRowsResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		//nothing to be done for CancelOrderRows
    	}
	}
}
