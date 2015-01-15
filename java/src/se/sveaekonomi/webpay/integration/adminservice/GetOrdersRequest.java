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
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * Handles Admin Webservice GetOrdersRequest
 * @author Kristian Grossman-Madsen
 */
public class GetOrdersRequest {

	// NOTE: validates on order level, don't validate request attributes in itself (rationale: bad request will return error from webservice)
	
	private String action;
	public QueryOrderBuilder builder;
		
	public GetOrdersRequest( QueryOrderBuilder builder) {
		this.action = "GetOrders";
		this.builder = builder;
	}
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @return error string indicating which methods are missing, or empty string if no problems found
	 */
	public String validateOrder() {
		String errors = "";		
		errors += validateOrderId();
		errors += validateCountryCode();
		return errors;
	}
	
    private String validateOrderId() {
    	return (builder.getOrderId() == null) ? "MISSING VALUE - OrderId is required, use setOrderId().\n" : "";
    }

    private String validateCountryCode() {
        return (builder.getCountryCode() == null) ? "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" : "";
    }	
	
	/**
	 * prepares the soap request to send to admin webservice
	 * @throws SOAPException 
	 * @throws IOException 
	 */
	public SOAPMessage prepareRequest() throws Exception {

    	// validate request and throw exception if validation fails
        String errors = validateOrder();        
        if (!errors.equals("")) {
        	//System.out.println(errors);
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
        
        // build inspectable request object and return
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
    
		//<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/" xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <tem:GetOrders>
		//         <!--Optional:-->
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:OrdersToRetrieve>
		//               <dat:GetOrderInformation>
		//                  <dat:ClientId>79021</dat:ClientId>
		//                  <dat:OrderType>Invoice</dat:OrderType>
		//                  <dat:SveaOrderId>348629</dat:SveaOrderId>
		//               </dat:GetOrderInformation>
		//            </dat:OrdersToRetrieve>
		//         </tem:request>
		//      </tem:GetOrders>
		//   </soapenv:Body>
		//</soapenv:Envelope>    	    
	                	
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");	    
        
	    // SOAP Body
	    SOAPBody start = envelope.getBody();
	    SOAPElement soapBody = start.addChildElement("GetOrders", "tem");
		    SOAPElement soapBodyElem = soapBody.addChildElement("request", "tem");
		    	SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("Authentication", "dat");
		    		SOAPElement soapBodyElem2 = soapBodyElem1.addChildElement("Password", "dat");
		    			soapBodyElem2.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
		    		SOAPElement soapBodyElem3 = soapBodyElem1.addChildElement("Username", "dat");
		    			soapBodyElem3.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
	    		
		    		SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("OrdersToRetrieve", "dat");
		    			SOAPElement soapBodyElem5 = soapBodyElem4.addChildElement("GetOrderInformation", "dat");
		    				SOAPElement soapBodyElem6 = soapBodyElem5.addChildElement("ClientId", "dat");
						    	soapBodyElem6.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber(this.builder.getOrderType(), this.builder.getCountryCode())));
						    SOAPElement soapBodyElem7 = soapBodyElem5.addChildElement("OrderType", "dat");
						    	if( this.builder.getOrderType() == PAYMENTTYPE.INVOICE ) {
						    		soapBodyElem7.addTextNode("Invoice");
						    	}
						    	if( this.builder.getOrderType() == PAYMENTTYPE.PAYMENTPLAN ) {
						    		soapBodyElem7.addTextNode("PaymentPlan");
						    	}						    	
						    SOAPElement soapBodyElem8 = soapBodyElem5.addChildElement("SveaOrderId", "dat");
						    	soapBodyElem8.addTextNode(String.valueOf(this.builder.getOrderId()));
						    	
        String soapActionPrefix = "http://tempuri.org/IAdminService/";
						    	
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapActionPrefix + this.action);
					   
        soapMessage.saveChanges();

        /* Print the request message */
		//System.out.print("Request SOAP Message:");
		//soapMessage.writeTo(System.out);
		//System.out.println();

        return soapMessage;
	}

	/**
	 * 
	 * @return GetOrdersResponse
	 * @throws Exception 
	 */
	public GetOrdersResponse doRequest() throws Exception {
			
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "https://partnerweb.sveaekonomi.se/WebPayAdminService_test/AdminService.svc/backward";
    	SOAPMessage soapResponse = soapConnection.call( prepareRequest(), url );
        
        // DEBUG: print SOAP Response
//		System.out.print("Response SOAP Message:");
//		soapResponse.writeTo(System.out);
//      System.out.println();
        
        soapConnection.close();
        
        // parse response
        GetOrdersResponse response = new GetOrdersResponse(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));
        
        return response;
	}	
	
}
