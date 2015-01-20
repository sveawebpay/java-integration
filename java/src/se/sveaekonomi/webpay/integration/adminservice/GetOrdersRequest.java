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
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * Handles Admin Webservice GetOrders request
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
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}
	
	/**
	 * prepares the soap request to send to admin webservice
	 * @throws SOAPException 
	 * @throws IOException 
	 */
	public SOAPMessage prepareRequest() throws SOAPException {
		
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
	public GetOrdersResponse doRequest() {
		
		// validate and prepare request, throw runtime exception on error
		SOAPMessage soapRequest;
    	try {
			soapRequest = prepareRequest();
		} catch (SOAPException e) {
			throw new SveaWebPayException( "GetOrdersRequest: prepareRequest failed.", e );
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
			throw new SveaWebPayException( "GetOrdersRequest: doRequest send request failed.", e );
		}
        
		// parse response
		GetOrdersResponse response;
		try {
			response = new GetOrdersResponse(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));
		} catch (SOAPException e) {
			throw new SveaWebPayException( "GetOrdersRequest: doRequest parse response failed.", e );
		}
        return response;
	}	
	
}
