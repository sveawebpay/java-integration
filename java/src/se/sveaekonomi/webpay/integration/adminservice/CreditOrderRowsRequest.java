package se.sveaekonomi.webpay.integration.adminservice;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class CreditOrderRowsRequest {

	private String action;
	private CreditOrderRowsBuilder builder;
		
	public CreditOrderRowsRequest( CreditOrderRowsBuilder builder) {
		this.action = "CreditInvoiceRows";
		this.builder = builder;
	}
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @throws ValidationException
	 */	
    public void validateOrder() throws ValidationException {
        String errors = "";
        if (builder.getInvoiceId() == null) {
            errors += "MISSING VALUE - InvoiceId is required, use setInvoiceId().\n";
    	}
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }
        if (builder.getInvoiceDistributionType() == null) {
        	errors += "MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n";
        }
        // need either row indexes or new credit rows to calculate amount to credit
        if( builder.getRowsToCredit().size() == 0 && builder.getNewCreditOrderRows().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditDirectBankOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n";
    	}
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
    }

    /** @returns false iff any order row is specified using amountExVat and vatPercent, and the flipPriceIncludingVat flag is false */
    public boolean determinePriceIncludingVat( ArrayList<OrderRowBuilder> orderRows, boolean flipPriceIncludingVat) {
    	boolean exVatRowSeen = false;
    	for( OrderRowBuilder row : orderRows ) {
    		if( row.getAmountExVat() != null && row.getVatPercent() != null ) { // row specified without incvat, should send as exvat
    			exVatRowSeen = true;
    			break;
    		}
    	}
    	boolean usePriceIncludingVat = exVatRowSeen ? false : true;
    	
    	return flipPriceIncludingVat ? !usePriceIncludingVat : usePriceIncludingVat;
    }    
    
	public SOAPMessage prepareRequest() throws SOAPException {	

		// validate builder, throw runtime exception on error
		try {
			validateOrder(); 
		}
        catch (ValidationException e) {
            throw new SveaWebPayException( "CreditOrderRowsRequest: validateRequest failed.", e );
        }

		// determine if we can send the order as incvat, by using the priceIncludingVat = true flag in request
		boolean usePriceIncludingVat = determinePriceIncludingVat(this.builder.getNewCreditOrderRows(), false);
		
		// build and return inspectable request object
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		
		//<soapenv:Envelope 
		//	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
		//	xmlns:tem="http://tempuri.org/" 
		//	xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" 
		//	xmlns:dat1="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" 
		//	xmlns:arr="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <tem:CreditInvoiceRows>
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:ClientId>79021</dat:ClientId>
		//            <dat:InvoiceDistributionType>Post</dat:InvoiceDistributionType>
		//            <dat:InvoiceId>1043819</dat:InvoiceId>
		//            <dat:RowNumbers>
		//               <arr:long>1</arr:long>
		//            </dat:RowNumbers>
		//         </tem:request>
		//      </tem:CreditInvoiceRows>
		//   </soapenv:Body>
		//</soapenv:Envelope>
		
		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
		envelope.addNamespaceDeclaration("dat1", "http://schemas.datacontract.org/2004/07/DataObjects.Webservice");	    
	    envelope.addNamespaceDeclaration("arr", "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");	    

	    // SOAP Headers
		String soapActionPrefix = "http://tempuri.org/IAdminService/";		    	
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapActionPrefix + this.action);
			    
	    // SOAP Body
	    SOAPBody body = envelope.getBody();
	    SOAPElement deliverPartial = body.addChildElement("CreditInvoiceRows", "tem");
	    SOAPElement request = deliverPartial.addChildElement("request", "tem");
	    	SOAPElement authentication = request.addChildElement("Authentication", "dat");
	    		SOAPElement password = authentication.addChildElement("Password", "dat");
	    			password.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
	    		SOAPElement username = authentication.addChildElement("Username", "dat");
	    			username.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
			SOAPElement clientId = request.addChildElement("ClientId", "dat");
				clientId.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(this.builder.getOrderType(), this.builder.getCountryCode())));
	    	SOAPElement invoiceDistributionType = request.addChildElement("InvoiceDistributionType", "dat");
    			invoiceDistributionType.addTextNode(this.builder.getInvoiceDistributionType().toString());
		    SOAPElement invoiceId = request.addChildElement("InvoiceId", "dat");
		    	invoiceId.addTextNode(String.valueOf(this.builder.getInvoiceId()));
	    	SOAPElement rowNumbers = request.addChildElement("RowNumbers", "dat");
    		for( Integer rowIndex : this.builder.getRowsToCredit() ) {
    			rowNumbers.addChildElement("long","arr").addTextNode( Integer.toString( rowIndex ) );
    		}
	    	
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
	
	public CreditOrderRowsResponse doRequest() {	
		
        // validate and prepare request, throw runtime exception on error
		SOAPMessage soapRequest;
		try {
        	soapRequest = prepareRequest();		
		} catch (SOAPException e) {
			throw new SveaWebPayException( "CreditOrderRowsRequest: prepareRequest failed.", e );
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
			throw new SveaWebPayException( "CreditOrderRowsRequest: doRequest send request failed.", e );
		}

		// parse response
		CreditOrderRowsResponse response;
		try {
			response = new CreditOrderRowsResponse(soapResponse);
		} catch (SOAPException e) {
			throw new SveaWebPayException( "CreditOrderRowsRequest: doRequest parse response failed.", e );

		}
		return response;
	};	
}
