package se.sveaekonomi.webpay.integration.adminservice;

import java.io.IOException;

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
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * Handles Admin Webservice DeliverOrderRows method
 * 
 * @author Kristian Grossman-Madsen
 */
public class DeliverOrderRowsRequest  {

	private String action;
	private DeliverOrderRowsBuilder builder;
		
	public DeliverOrderRowsRequest( DeliverOrderRowsBuilder builder) {
		this.action = "DeliverPartial";
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
        if( builder.getRowIndexesToDeliver().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToDeliver is required for deliverInvoiceOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n";
    	}
        if (builder.getInvoiceDistributionType() == null) {
        	errors += "MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n";
        }

        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}

	public SOAPMessage prepareRequest() throws SOAPException {		
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
		//      <tem:DeliverPartial>
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:InvoiceDistributionType>Post</dat:InvoiceDistributionType>
		//            <dat:OrderToDeliver>
		//               <dat:ClientId>79021</dat:ClientId>
		//               <dat:OrderType>Invoice</dat:OrderType>
		//               <dat:SveaOrderId>506791</dat:SveaOrderId>
		//            </dat:OrderToDeliver>
		//            <dat:RowNumbers>
		//               <arr:long>1</arr:long>
		//            </dat:RowNumbers>
		//         </tem:request>
		//      </tem:DeliverPartial>
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
	    SOAPElement deliverPartial = body.addChildElement("DeliverPartial", "tem");
	    SOAPElement request = deliverPartial.addChildElement("request", "tem");
	    	SOAPElement authentication = request.addChildElement("Authentication", "dat");
	    		SOAPElement password = authentication.addChildElement("Password", "dat");
	    			password.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
	    		SOAPElement username = authentication.addChildElement("Username", "dat");
	    			username.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
	    	SOAPElement invoiceDistributionType = request.addChildElement("InvoiceDistributionType", "dat");
	    		invoiceDistributionType.addTextNode(this.builder.getInvoiceDistributionType());
			SOAPElement orderToDeliver = request.addChildElement("OrderToDeliver", "dat");
				SOAPElement clientId = orderToDeliver.addChildElement("ClientId", "dat");
					clientId.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(this.builder.getOrderType(), this.builder.getCountryCode())));
			    SOAPElement orderType = orderToDeliver.addChildElement("OrderType", "dat");
			    	orderType.addTextNode("Invoice");
			    SOAPElement sveaOrderId = orderToDeliver.addChildElement("SveaOrderId", "dat");
			    	sveaOrderId.addTextNode(String.valueOf(this.builder.getOrderId()));
	    	SOAPElement rowNumbers = request.addChildElement("RowNumbers", "dat");
	    		for( Integer rowIndex : this.builder.getRowIndexesToDeliver() ) {
	    			rowNumbers.addChildElement("long","arr").addTextNode( Integer.toString( rowIndex ) );
	    		}
	    	
    	soapMessage.saveChanges();
    	
        /* Print the request message */
			System.out.print("Request SOAP Message:");
			try {
				soapMessage.writeTo(System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
		    	
		return soapMessage;

	};    	
	
	public DeliverOrderRowsResponse doRequest() {	
		
		// validate builder, throw runtime exception on error
		try {
			validateOrder(); 
		}
        catch (ValidationException e) {
            throw new SveaWebPayException( "DeliverOrderRowsRequest: validateRequest failed.", e );
        }
		
        // prepare request, throw runtime exception on error
		SOAPMessage soapRequest;
		try {
        	soapRequest = prepareRequest();		
		} catch (SOAPException e) {
			throw new SveaWebPayException( "DeliverOrderRowsRequest: prepareRequest failed.", e );
		}
		
		// send request and receive response
		SOAPMessage soapResponse;
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			// Send SOAP Message to SOAP Server
			String url = "https://partnerweb.sveaekonomi.se/WebPayAdminService_test/AdminService.svc/backward";	// TODO get url from config
			soapResponse = soapConnection.call( soapRequest, url );
			
			// DEBUG: print SOAP Response
			System.out.print("Response SOAP Message:");
			try {
				soapResponse.writeTo(System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
			
			soapConnection.close();			
		}
		catch( SOAPException e) {
			throw new SveaWebPayException( "DeliverOrderRowsRequest: doRequest send request failed.", e );
		}
		// parse response
		DeliverOrderRowsResponse response;
		try {
			response = new DeliverOrderRowsResponse(soapResponse);
		} catch (SOAPException e) {
			throw new SveaWebPayException( "DeliverOrderRowsRequest: doRequest parse response failed.", e );
		}
		return response;

	};
}
