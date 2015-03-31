package se.sveaekonomi.webpay.integration.adminservice;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
import se.sveaekonomi.webpay.integration.order.handle.AddOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.AddOrderRowsResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;

public class AddOrderRowsRequest extends AdminServiceRequest {

	private String action;
	private AddOrderRowsBuilder builder;
		
	public AddOrderRowsRequest( AddOrderRowsBuilder addOrderRowsBuilder) {
		this.action = "AddOrderRows";
		this.builder = addOrderRowsBuilder;
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
        // need either row indexes or new credit rows to calculate amount to credit
        if( builder.getOrderRows().size() == 0 ) {
        	errors += "MISSING VALUE - orderRows is required, use method addOrderRow()/addOrderRows().\n";
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
    
	public SOAPMessage prepareRequest( boolean resendOrderWithFlippedPriceIncludingVat ) throws SOAPException {	

		// validate builder, throw runtime exception on error
		try {
			validateOrder(); 
		}
        catch (ValidationException e) {
            throw new SveaWebPayException( "AddOrderRowsRequest: validateRequest failed.", e );
        }

		// determine if we can send the order as incvat, by using the priceIncludingVat = true flag in request
		boolean usePriceIncludingVatFlag = determinePriceIncludingVat(this.builder.getOrderRows(), resendOrderWithFlippedPriceIncludingVat);		
		
		// build and return inspectable request object
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		
		//<soapenv:Envelope 
		//xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
		//xmlns:tem="http://tempuri.org/" 
		//xmlns:dat="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" 
		//xmlns:dat1="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <tem:AddOrderRows>
		//         <tem:request>
		//            <dat:Authentication>
		//               <dat:Password>sverigetest</dat:Password>
		//               <dat:Username>sverigetest</dat:Username>
		//            </dat:Authentication>
		//            <dat:ClientId>79021</dat:ClientId>
		//            <dat:OrderRows>
		//                <dat1:OrderRow>
		//                  <dat1:ArticleNumber>D</dat1:ArticleNumber>
		//                  <dat1:Description>added</dat1:Description>
		//                  <dat1:DiscountPercent>0</dat1:DiscountPercent>
		//                  <dat1:NumberOfUnits>1</dat1:NumberOfUnits>                  
		//                  <dat1:PricePerUnit>125</dat1:PricePerUnit>
		//			   		<dat1:PriceIncludingVat>true</dat1:PriceIncludingVat>
		//			   		<dat1:Unit>st</dat1:Unit>
		//                  <dat1:VatPercent>25</dat1:VatPercent>
		//               </dat1:OrderRow>             
		//              </dat:OrderRows>
		//            <dat:OrderType>Invoice</dat:OrderType>
		//            <dat:SveaOrderId>9999999</dat:SveaOrderId>
		//         </tem:request>
		//      </tem:AddOrderRows>
		//   </soapenv:Body>
		//</soapenv:Envelope>

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope(); // adds namespace SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/
	    envelope.addNamespaceDeclaration("dat", "http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service");
		envelope.addNamespaceDeclaration("dat1", "http://schemas.datacontract.org/2004/07/DataObjects.Webservice");	    
	    envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");

	    // SOAP Headers
		String soapActionPrefix = "http://tempuri.org/IAdminService/";		    	
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapActionPrefix + this.action);
		
    	setHeaderRequestProperties(headers, this.builder.getConfig());		
			    
	    // SOAP Body
	    SOAPBody body = envelope.getBody();
	    SOAPElement updateOrderRows = body.addChildElement("AddOrderRows", "tem");
	    SOAPElement request = updateOrderRows.addChildElement("request", "tem");
	    	SOAPElement authentication = request.addChildElement("Authentication", "dat");
	    		SOAPElement password = authentication.addChildElement("Password", "dat");	    			
	    			password.addTextNode(this.builder.getConfig().getPassword( this.builder.getOrderType(), this.builder.getCountryCode()));
	    		SOAPElement username = authentication.addChildElement("Username", "dat");
	    			username.addTextNode(this.builder.getConfig().getUsername( this.builder.getOrderType(), this.builder.getCountryCode()));
			SOAPElement clientId = request.addChildElement("ClientId", "dat");
				clientId.addTextNode(String.valueOf(this.builder.getConfig().getClientNumber( this.builder.getOrderType(), this.builder.getCountryCode() )));
		    	
		    SOAPElement orderRows = request.addChildElement("OrderRows", "dat");
		    for( OrderRowBuilder row : this.builder.getOrderRows() ) {
		    	SOAPElement orderRow = orderRows.addChildElement("OrderRow", "dat1");
		    		SOAPElement articleNumber = orderRow.addChildElement("ArticleNumber", "dat1");
		    			articleNumber.addTextNode( row.getArticleNumber() );
	    			SOAPElement description = orderRow.addChildElement("Description", "dat1");
	    				description.addTextNode( row.getName()+": "+row.getDescription() );
    				SOAPElement discountPercent = orderRow.addChildElement("DiscountPercent", "dat1");
						discountPercent.addTextNode( String.valueOf(row.getDiscountPercent()) );
    				SOAPElement numberOfUnits = orderRow.addChildElement("NumberOfUnits", "dat1");
    					numberOfUnits.addTextNode( String.valueOf(row.getQuantity()) );
					SOAPElement priceIncludingVat = orderRow.addChildElement("PriceIncludingVat", "dat1");
						priceIncludingVat.addTextNode( usePriceIncludingVatFlag ? "true" : "false" );
					SOAPElement pricePerUnit = orderRow.addChildElement("PricePerUnit", "dat1");
						pricePerUnit.addTextNode( String.valueOf( 
	    					// calculate the correct amount to send based on the builder order row and usePriceIncludingVat flag
							getPricePerUnitFromBuilderOrderRowAndPriceIncludingVatFlag( row, usePriceIncludingVatFlag) ) 
						);
    				SOAPElement unit = orderRow.addChildElement("Unit", "dat1");
    					unit.addTextNode( String.valueOf(row.getUnit()) ); 
    				SOAPElement vatPercent = orderRow.addChildElement("VatPercent", "dat1");
    					vatPercent.addTextNode( String.valueOf( 
	    					// get vat percent to send based on the builder order row (i.e. if specified exvat + incvat)
							getVatPercentFromBuilderOrderRow(row) ) 
						); 
			}		    
		    SOAPElement orderType = request.addChildElement("OrderType", "dat");
		    	orderType.addTextNode( this.builder.getOrderType().toString() );				
		    SOAPElement sveaOrderId = request.addChildElement("SveaOrderId", "dat");
		    	sveaOrderId.addTextNode(String.valueOf(this.builder.getOrderId()));	
	    	
    	soapMessage.saveChanges();
    	
        // DEBUG: Print SOAP request 
//		System.out.print("Request SOAP Message:");
//		try {
//			soapMessage.writeTo(System.out);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println();
		    	
		return soapMessage;
	}



	protected Double getPricePerUnitFromBuilderOrderRowAndPriceIncludingVatFlag( OrderRowBuilder row, boolean usePriceIncludingVatFlag) {
		Double amount = 0.0;
		
		// row: exvat + vatpercent 
		if( row.getAmountExVat() != null && row.getVatPercent() != null ) {
			if( usePriceIncludingVatFlag  ) {
				amount = OrderRowBuilder.convertExVatToIncVat( row.getAmountExVat(), row.getVatPercent() );
			}
			else {
				amount = row.getAmountExVat();
			}
		}
		// row: incvat + vatpercent 
		if( row.getAmountIncVat() != null && row.getVatPercent() != null ) {
			if( usePriceIncludingVatFlag  ) {
				amount = row.getAmountIncVat();
			}
			else {
				amount = OrderRowBuilder.convertIncVatToExVat( row.getAmountIncVat(), row.getVatPercent() );
			}
		}		
		// row: incvat + exvat 
		if( row.getAmountIncVat() != null && row.getAmountExVat() != null ) {
			if( usePriceIncludingVatFlag  ) {
				amount = row.getAmountIncVat();
			}
			else {
				amount = row.getAmountExVat();
			}
		}        
		return amount;
	}
	
	protected Double getVatPercentFromBuilderOrderRow( OrderRowBuilder row ) {

		Double vatPercent = 0.0;
		// row: exvat + vatpercent 
		if( row.getAmountExVat() != null && row.getVatPercent() != null ) {
			vatPercent = row.getVatPercent();
		}
		// row: incvat + vatpercent 
		if( row.getAmountIncVat() != null && row.getVatPercent() != null ) {
			vatPercent = row.getVatPercent();
		}		
		// row: incvat + exvat 
		if( row.getAmountIncVat() != null && row.getAmountExVat() != null ) {
			vatPercent = OrderRowBuilder.calculateVatPercentFromAmountExVatAndAmountIncVat( row.getAmountExVat(), row.getAmountIncVat() );
		}        
		return vatPercent;
	}	

	public AddOrderRowsResponse doRequest() {
		return doRequest( false );
	}
	private AddOrderRowsResponse doRequest( boolean resendOrderWithFlippedPriceIncludingVat ) {	
		
        // validate and prepare request, throw runtime exception on error
		SOAPMessage soapRequest;
		try {
        	soapRequest = prepareRequest( resendOrderWithFlippedPriceIncludingVat );		
		} catch (SOAPException e) {
			throw new SveaWebPayException( "AddOrderRowsRequest: prepareRequest failed.", e );
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
//			
//			soapConnection.close();			
		}
		catch( SOAPException e) {
			throw new SveaWebPayException( "AddOrderRowsRequest: doRequest send request failed.", e );
		}

		// parse response
		AddOrderRowsResponse response;
		try {
			response = new AddOrderRowsResponse(soapResponse);
		} catch (SOAPException e) {
			throw new SveaWebPayException( "AddOrderRowsRequest: doRequest parse response failed.", e );

		}
		
        // if we received error 50036 from webservice , resend request with flipPriceIncludingVat set to true
		if( response.getResultCode().equals("50036") ) {         				
			response = this.doRequest( true ); 
        }

		return response;
	};	
}
