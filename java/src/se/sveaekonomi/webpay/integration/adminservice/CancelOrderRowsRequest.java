package se.sveaekonomi.webpay.integration.adminservice;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.ValidationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class CancelOrderRowsRequest {

	private String action;
	private CancelOrderRowsBuilder builder;
		
	public CancelOrderRowsRequest( CancelOrderRowsBuilder builder) {
		this.action = "CancelOrderRows";
		this.builder = builder;
	}	
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @throws ValidationException
	 */
	public void validateOrder() throws ValidationException {
    	String errors = "";
        if (builder.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}     
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }           
        if( builder.getRowsToCancel().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToCancel is required for cancelInvoiceOrderRows(). Use methods setRowToCancel() or setRowsToCancel().\n";
    	}
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}	
	
	public SOAPMessage prepareRequest() throws SOAPException {	

		// validate builder, throw runtime exception on error
		try {
			validateOrder(); 
		}
        catch (ValidationException e) {
            throw new SveaWebPayException( "CancelOrderRowsRequest: validateRequest failed.", e );
        }
				
		// build and return inspectable request object
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		
		//<soapenv:Envelope 
		//	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
		//	xmlns:tem="http://tempuri.org/" 
		//	xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" 
		//	xmlns:arr="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <tem:CancelOrderRows>
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:ClientId>79021</dat:ClientId>
		//            <dat:OrderRowNumbers>
		//	 		<arr:long>1</arr:long>
		//            </dat:OrderRowNumbers>
		//            <dat:OrderType>Invoice</dat:OrderType>
		//            <dat:SveaOrderId>508450</dat:SveaOrderId>
		//         </tem:request>
		//      </tem:CancelOrderRows>
		//   </soapenv:Body>
		//</soapenv:Envelope>
		
		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
	    envelope.addNamespaceDeclaration("arr", "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");	    

	    // SOAP Headers
		String soapActionPrefix = "http://tempuri.org/IAdminService/";		    	
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapActionPrefix + this.action);
			    
	    // SOAP Body
	    SOAPBody body = envelope.getBody();
	    SOAPElement deliverPartial = body.addChildElement("CancelOrderRows", "tem");
	    SOAPElement request = deliverPartial.addChildElement("request", "tem");
	    	SOAPElement authentication = request.addChildElement("Authentication", "dat");
	    		SOAPElement password = authentication.addChildElement("Password", "dat");
	    			password.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
	    		SOAPElement username = authentication.addChildElement("Username", "dat");
	    			username.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
			SOAPElement clientId = request.addChildElement("ClientId", "dat");
				clientId.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(this.builder.getOrderType(), this.builder.getCountryCode())));
	    	SOAPElement orderRowNumbers = request.addChildElement("OrderRowNumbers", "dat");
    		for( Integer rowIndex : this.builder.getRowsToCancel() ) {
    			orderRowNumbers.addChildElement("long","arr").addTextNode( Integer.toString( rowIndex ) );
    		}
		    SOAPElement orderType = request.addChildElement("OrderType", "dat");
		    	if( this.builder.getOrderType() == PAYMENTTYPE.INVOICE ) {
		    		orderType.addTextNode( ORDERTYPE.Invoice.toString() );
		    	}
	    		if( this.builder.getOrderType() == PAYMENTTYPE.PAYMENTPLAN ) {
	    			orderType.addTextNode( ORDERTYPE.PaymentPlan.toString() );
	    		}
		    SOAPElement sveaOrderId = request.addChildElement("SveaOrderId", "dat");
		    	sveaOrderId.addTextNode(String.valueOf(this.builder.getOrderId()));
	    	
    	soapMessage.saveChanges();
    	
        // DEBUG: Print SOAP request 
//		System.out.print("Request SOAP Message:");
//		try {
//			soapMessage.writeTo(System.out);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println();
		    	
		return soapMessage;
	}
	
	public CancelOrderRowsResponse doRequest() {	
		
        // validate and prepare request, throw runtime exception on error
		SOAPMessage soapRequest;
		try {
        	soapRequest = prepareRequest();		
		} catch (SOAPException e) {
			throw new SveaWebPayException( "CancelOrderRowsRequest: prepareRequest failed.", e );
		}
		
		// send request and receive response
		SOAPMessage soapResponse;
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			// Send SOAP Message to SOAP Server
	        URL url = builder.getConfig().getEndPoint(PAYMENTTYPE.ADMIN_TYPE);		
			soapResponse = soapConnection.call( soapRequest, url.toString() );
			
			// DEBUG: print SOAP Response
//			System.out.print("Response SOAP Message:");
//			try {
//				soapResponse.writeTo(System.out);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println();
			
			soapConnection.close();			
		}
		catch( SOAPException e) {
			throw new SveaWebPayException( "CancelOrderRowsRequest: doRequest send request failed.", e );
		}

		// parse response
		CancelOrderRowsResponse response;
		try {
			response = new CancelOrderRowsResponse(soapResponse);
		} catch (SOAPException e) {
			throw new SveaWebPayException( "CancelOrderRowsRequest: doRequest parse response failed.", e );

		}
		return response;
	};	
	
	
}
