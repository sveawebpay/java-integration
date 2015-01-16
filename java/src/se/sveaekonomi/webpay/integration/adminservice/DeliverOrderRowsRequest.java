package se.sveaekonomi.webpay.integration.adminservice;

import java.io.IOException;

import javax.xml.bind.ValidationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
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
		this.action = "GetOrders";
		this.builder = builder;
	}

	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @return error string indicating which methods are missing, or empty string if no problems found
	 */
	public String validateOrder() {
    	String errors = "";
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }        
        if (builder.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}        
        if( builder.getRowIndexesToDeliver().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToDeliver is required for deliverInvoiceOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n";
    	}
        if (builder.getInvoiceDistributionType() == null) {
        	errors += "MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n";
        }
        return errors;
	}

	// TODO
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
//		String soapActionPrefix = "http://tempuri.org/IAdminService/";		    	
//		MimeHeaders headers = soapMessage.getMimeHeaders();
//		headers.addHeader("SOAPAction", soapActionPrefix + this.action);
			    
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
    
//	/**
//	 * prepares the soap request to send to admin webservice
//	 * @throws SOAPException 
//	 * @throws IOException 
//	 */
//	public SOAPMessage prepareRequest() throws Exception {
//

//        // build inspectable request object and return
//        MessageFactory messageFactory = MessageFactory.newInstance();
//        SOAPMessage soapMessage = messageFactory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//    
//		//<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/" xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service">
//		//   <soapenv:Header/>
//		//   <soapenv:Body>
//		//      <tem:GetOrders>
//		//         <!--Optional:-->
//		//         <tem:request>
//		//            <dat:Authentication>
//		//               <dat:Password>sverigetest</dat:Password>
//		//               <dat:Username>sverigetest</dat:Username>
//		//            </dat:Authentication>
//		//            <dat:OrdersToRetrieve>
//		//               <dat:GetOrderInformation>
//		//                  <dat:ClientId>79021</dat:ClientId>
//		//                  <dat:OrderType>Invoice</dat:OrderType>
//		//                  <dat:SveaOrderId>348629</dat:SveaOrderId>
//		//               </dat:GetOrderInformation>
//		//            </dat:OrdersToRetrieve>
//		//         </tem:request>
//		//      </tem:GetOrders>
//		//   </soapenv:Body>
//		//</soapenv:Envelope>    	    
//	                	
//        // SOAP Envelope
//        SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
//	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
//	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");	    
//        
//	    // SOAP Body
//	    SOAPBody start = envelope.getBody();
//	    SOAPElement soapBody = start.addChildElement("GetOrders", "tem");
//		    SOAPElement soapBodyElem = soapBody.addChildElement("request", "tem");
//		    	SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("Authentication", "dat");
//		    		SOAPElement soapBodyElem2 = soapBodyElem1.addChildElement("Password", "dat");
//		    			soapBodyElem2.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
//		    		SOAPElement soapBodyElem3 = soapBodyElem1.addChildElement("Username", "dat");
//		    			soapBodyElem3.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
//	    		
//		    		SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("OrdersToRetrieve", "dat");
//		    			SOAPElement soapBodyElem5 = soapBodyElem4.addChildElement("GetOrderInformation", "dat");
//		    				SOAPElement soapBodyElem6 = soapBodyElem5.addChildElement("ClientId", "dat");
//						    	soapBodyElem6.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(this.builder.getOrderType(), this.builder.getCountryCode())));
//						    SOAPElement soapBodyElem7 = soapBodyElem5.addChildElement("OrderType", "dat");
//						    	if( this.builder.getOrderType() == PAYMENTTYPE.INVOICE ) {
//						    		soapBodyElem7.addTextNode("Invoice");
//						    	}
//						    	if( this.builder.getOrderType() == PAYMENTTYPE.PAYMENTPLAN ) {
//						    		soapBodyElem7.addTextNode("PaymentPlan");
//						    	}						    	
//						    SOAPElement soapBodyElem8 = soapBodyElem5.addChildElement("SveaOrderId", "dat");
//						    	soapBodyElem8.addTextNode(String.valueOf(this.builder.getOrderId()));
//						    	
//        String soapActionPrefix = "http://tempuri.org/IAdminService/";
//						    	
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", soapActionPrefix + this.action);
//					   
//        soapMessage.saveChanges();
//
//        /* Print the request message */
//		//System.out.print("Request SOAP Message:");
//		//soapMessage.writeTo(System.out);
//		//System.out.println();
//
//        return soapMessage;
//	}
	
	
	
	public DeliverOrderRowsResponse doRequest() {	
		
		// validate builder, throw runtime exception on error
		String errors =  this.validateOrder(); 
        if ( !errors.equals("")) {
//      	//System.out.println("DeliverOrderRequest.doRequest: " + errors);
            throw new SveaWebPayException( "Validation failed", new ValidationException(errors) );
        }     	
		
        // prepare and send request
        try {
			prepareRequest();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO ...
		return new DeliverOrderRowsResponse(null);
	};
}
