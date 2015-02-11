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

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * DeliverOrdersRequest handles requests to Svea Admin WebService DeliverOrders method
 * 
 * @author Kristian Grossman-Madsen
 */
public class DeliverOrdersRequest implements Requestable {
	
	private String action;
	public DeliverOrderBuilder builder;

    public DeliverOrdersRequest(DeliverOrderBuilder orderBuilder) {
		this.action = "DeliverOrders";
		this.builder = orderBuilder;
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
        
        if (builder.getInvoiceDistributionType() == null) {
            errors += "MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n";
    	}   
        
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }     		
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}

	/**
	 * prepares the soap request to send to admin webservice
	 * @throws SOAPException 
	 * @throws IOException 
	 */
	public SOAPMessage prepareRequest() throws SOAPException, IOException { 
		// validate builder, throw runtime exception on error
		try {
			validateOrder(); 
		}
        catch (ValidationException e) {
            throw new SveaWebPayException( "GetOrdersRequest: validateRequest failed.", e );
        }
		
        // build inspectable request object and return
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
    
		//<soapenv:Envelope 
		//	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
		//	xmlns:tem="http://tempuri.org/" 
		//	xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <tem:DeliverOrders>
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:InvoiceDistributionType>Post</dat:InvoiceDistributionType>
		//            <dat:OrdersToDeliver>
		//               <dat:DeliverOrderInformation>
		//                  <dat:ClientId>79021</dat:ClientId>
		//                  <dat:OrderType>Invoice</dat:OrderType>
		//                  <dat:SveaOrderId>478232</dat:SveaOrderId>
		//               </dat:DeliverOrderInformation>
		//            </dat:OrdersToDeliver>
		//         </tem:request>
		//      </tem:DeliverOrders>
		//   </soapenv:Body>
		//</soapenv:Envelope>    	    
          	
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");	    

	    // SOAP Body
	    SOAPBody start = envelope.getBody();
	    SOAPElement soapBody = start.addChildElement("DeliverOrders", "tem");
		    SOAPElement request = soapBody.addChildElement("request", "tem");
		    	SOAPElement soapBodyElem1 = request.addChildElement("Authentication", "dat");
		    		SOAPElement soapBodyElem2 = soapBodyElem1.addChildElement("Password", "dat");
		    			soapBodyElem2.addTextNode(this.builder.getConfig().getPassword( PAYMENTTYPE.fromOrderType(this.builder.getOrderType()), this.builder.getCountryCode()));
		    		SOAPElement soapBodyElem3 = soapBodyElem1.addChildElement("Username", "dat");
		    			soapBodyElem3.addTextNode(this.builder.getConfig().getUsername( PAYMENTTYPE.fromOrderType(this.builder.getOrderType()), this.builder.getCountryCode()));
	    	    	SOAPElement invoiceDistributionType = request.addChildElement("InvoiceDistributionType", "dat");
			    		invoiceDistributionType.addTextNode(this.builder.getInvoiceDistributionType().toString());   
		    		SOAPElement soapBodyElem4 = request.addChildElement("OrdersToDeliver", "dat");
		    			SOAPElement soapBodyElem5 = soapBodyElem4.addChildElement("DeliverOrderInformation", "dat");
		    				SOAPElement soapBodyElem6 = soapBodyElem5.addChildElement("ClientId", "dat");
						    	soapBodyElem6.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(PAYMENTTYPE.fromOrderType(this.builder.getOrderType()), this.builder.getCountryCode())));
						    SOAPElement soapBodyElem7 = soapBodyElem5.addChildElement("OrderType", "dat");
						    	if( this.builder.getOrderType().toString().equals("Invoice") ) {		// TODO PAYMENTTYPE.INVOICE
						    		soapBodyElem7.addTextNode("Invoice");
						    	}
						    	if( this.builder.getOrderType().toString().equals("PaymentPlan") ) {	// TODO PAYMENTTYPE.PAYMENTPLAN
						    		soapBodyElem7.addTextNode("PaymentPlan");
						    	}						    	
						    SOAPElement soapBodyElem8 = soapBodyElem5.addChildElement("SveaOrderId", "dat");
						    	soapBodyElem8.addTextNode(String.valueOf(this.builder.getOrderId()));
						    	
        String soapActionPrefix = "http://tempuri.org/IAdminService/";
						    	
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapActionPrefix + this.action);
					   
        soapMessage.saveChanges();

        /* Print the request message */
		System.out.print("Request SOAP Message:");
		soapMessage.writeTo(System.out);
		System.out.println();

        return soapMessage; 
	};    
    
	// TODO
	public DeliverOrdersResponse doRequest() {
		return new DeliverOrdersResponse();
	};
}
