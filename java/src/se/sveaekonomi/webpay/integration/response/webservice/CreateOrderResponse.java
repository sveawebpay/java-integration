package se.sveaekonomi.webpay.integration.response.webservice;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class CreateOrderResponse extends WebServiceResponse {
    
	/** long representation of String Svea OrderId */
    public long orderId;
    /** string representation of ORDERTYPE ORDERTYPE.Invoice or ORDERTYPE.PaymentPlan */
    public String orderType;
    /** boolean representation of Boolean sveaWillBuyOrder */
    public boolean sveaWillBuyOrder;
    /** double representation of Double amount */
    public double amount;
    /** @deprecated */
    public CustomerIdentityResponse customerIdentity;
    public String expirationDate;
	public String clientOrderNumber;
	/** derived from customerIdentity customerType */
    public boolean isIndividualIdentity;

	// 2.0 on
    private CustomerIdentity<?> customer;    
    private CustomerIdentity<?> getCustomer() {
    	return this.customer;
    }
    public IndividualCustomer getIndividualCustomer() {
    	return (IndividualCustomer) getCustomer();
    }
    public CompanyCustomer getCompanyCustomer() {
    	return (CompanyCustomer) getCustomer();
    }
    
    
    public Long getOrderId() {
    	return this.orderId;
    }
    public void setOrderId( Long orderId ) {
    	this.orderId = orderId;
    }
    
    public ORDERTYPE getOrderType() {
    	if( this.orderType.equals("Invoice") ) {
    		return ORDERTYPE.Invoice;
    	}
    	else if( this.orderType.equals("PaymentPlan") ) {
    		return ORDERTYPE.PaymentPlan;
    	}
    	else {
    		throw new IllegalArgumentException("Unknown OrderType");
    	}
    }    
    public void setOrderType( ORDERTYPE orderType ) {
    	if( orderType == ORDERTYPE.Invoice ) {
    		this.orderType = ORDERTYPE.Invoice.toString();
    	}
    	else if( orderType == ORDERTYPE.PaymentPlan ) {
    		this.orderType =  ORDERTYPE.PaymentPlan.toString();
    	}
    	else {
    		throw new IllegalArgumentException("Unknown OrderType");
    	}    	
    }   
    /** @deprecated */
    public void setOrderType( String orderTypeAsString ) {
    	if( orderTypeAsString.equals("Invoice") ) {
    		this.orderType = ORDERTYPE.Invoice.toString();
    	}
    	else if( this.orderType.equals("PaymentPlan") ) {
    		this.orderType =  ORDERTYPE.PaymentPlan.toString();
    	}
    	else {
    		throw new IllegalArgumentException("Unknown OrderType");
    	}    	
    }
    
    public Boolean getSveaWillBuyOrder() {
    	return Boolean.valueOf(this.sveaWillBuyOrder);
    }
    public void setSveaWillBuyOrder( Boolean sveaWillBuyOrder ) {
    	this.sveaWillBuyOrder = sveaWillBuyOrder;
    }

    public Double getAmount() {
    	return Double.valueOf(this.amount);
    }
    public void setAmount( Double amount ) {
    	this.amount = amount;
    }
    
    /** deprecated */
    public CustomerIdentityResponse getCustomerIdentity() {
		return customerIdentity;
	}
    /** deprecated */
    public void setCustomerIdentity(CustomerIdentityResponse customerIdentity) {
		this.customerIdentity = customerIdentity;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public String getClientOrderNumber() {
		return clientOrderNumber;
	}
	public void setClientOrderNumber(String clientOrderNumber) {
		this.clientOrderNumber = clientOrderNumber;
	}
	
	public Boolean isIndividualIdentity() {
		return Boolean.valueOf(isIndividualIdentity);
	}
	public void setIndividualIdentity(Boolean isIndividualIdentity) {
		this.isIndividualIdentity = isIndividualIdentity;
	}
    
    public CreateOrderResponse(NodeList soapMessage) {
        super(soapMessage);
        this.isIndividualIdentity = false;
        this.customerIdentity = new CustomerIdentityResponse();
        this.setValues(soapMessage);
    }
    
    public void setValues(NodeList soapMessage) {
        int size = soapMessage.getLength();
        
        for (int i = 0; i < size; i++) {
            Element node = (Element) soapMessage.item(i);

            this.sveaWillBuyOrder = Boolean.parseBoolean(getTagValue(node, "SveaWillBuyOrder"));
            
            this.amount = Double.parseDouble(getTagValue(node, "Amount"));
            this.orderId = Long.parseLong(getTagValue(node, "SveaOrderId"));
            this.expirationDate = getTagValue(node, "ExpirationDate");
            
            // Optional values
            String value = getTagValue(node, "OrderType");
            if (value != null) {
                this.orderType = value;
            }
            
            value = getTagValue(node, "ClientOrderNumber");
            if (value != null)
                this.clientOrderNumber = value;
            
            setCustomerIdentityType(node);
            setCustomerIdentityResponse( node );             
            setCustomer( node );
        }
    }

    private void setCustomerIdentityType(Node n) {
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int length = nl.getLength();
            
            for (int j = 0; j < length; j++) {
                Node childNode = nl.item(j);
                String nodeName = childNode.getNodeName();
                
                if (nodeName.equals("CustomerType")) {
                    this.isIndividualIdentity = getTagValue((Element) n, "CustomerType").equals("Individual") ? true : false;
                    return;
                }
                
                setCustomerIdentityType(childNode);
            }
        }
    }
    
    
	// TODO write tests for address data returned for all methods and all countries, set up matrix in documentation!
	// WS/createOrderEU * individual/company * countries
	// WS/getAddresses
	// HS/payment
	// AS/getOrders
	// HS/queryTransactionResponse
	// => all return CustomerIdentity<?> => IndividualCustomer/CompanyCustomer w/different attributes set -- annotate in matrix!
    
    private void setCustomer( Node node ) {
    	if( isIndividualIdentity() == true ) {
			
    		IndividualCustomer individualCustomer = new IndividualCustomer();
    		
    		//<CustomerIdentity>
			//    <NationalIdNumber>194605092222</NationalIdNumber>
			//    <FullName>Persson, Tess T</FullName>
			//    <Street>Testgatan 1</Street>
			//    <CoAddress>c/o Eriksson, Erik</CoAddress>
			//    <ZipCode>99999</ZipCode>
			//    <Locality>Stan</Locality>
			//    <CountryCode>SE</CountryCode>
			//    <CustomerType>Individual</CustomerType>
			//</CustomerIdentity>    	
				    	
			//  //ci
			//  private String phoneNumber;
			//  private String email;
			//  private String ipAddress;
			//  private String coAddress;
			//  private String streetAddress;
			//  private String housenumber;
			//  private String zipCode;
			//  private String locality;    	
			//  //ic
			//  private String ssn;
			//  private String birthDate;
			//  private String firstName;
			//  private String lastName;
			//  private String initials;
			//  private String name;
    		    		
    		// TODO remove null refs, if returned, set values!     		
    		
			individualCustomer.setPhoneNumber( null );
			individualCustomer.setEmail( null );
			individualCustomer.setIpAddress( null );	// not returned
			individualCustomer.setStreetAddress( returnChildNodeValue(node, "Street") );	// one argument version sets HouseNumber to null
			individualCustomer.setCoAddress( returnChildNodeValue(node, "CoAddress") );
			individualCustomer.setZipCode( returnChildNodeValue(node, "ZipCode") );
			individualCustomer.setLocality( returnChildNodeValue(node, "Locality") );
			individualCustomer.setName( returnChildNodeValue(node, "FullName") ); // one argument version sets name field
			individualCustomer.setNationalIdNumber( returnChildNodeValue(node, "NationalIdNumber") );
			individualCustomer.setBirthDate( null );
			individualCustomer.setName( null, null ); // two argument version sets firstName, lastName
			individualCustomer.setInitials( null );

			this.customer = individualCustomer;
    	}
    	
    	if( isIndividualIdentity() == false ) {
			
    		CompanyCustomer companyCustomer = new CompanyCustomer();
	    	
			//<CustomerIdentity>
			//    <NationalIdNumber>164608142222</NationalIdNumber>
			//    <FullName>Persson, Tess T</FullName>
			//    <Street>Testgatan 1</Street>
			//    <CoAddress>c/o Eriksson, Erik</CoAddress>
			//    <ZipCode>99999</ZipCode>
			//    <Locality>Stan</Locality>
			//    <CountryCode>SE</CountryCode>
			//    <CustomerType>Company</CustomerType>
			//</CustomerIdentity>
				
    		//  //ci
			//  private String phoneNumber;
			//  private String email;
			//  private String ipAddress;
			//  private String coAddress;
			//  private String streetAddress;
			//  private String housenumber;
			//  private String zipCode;
			//  private String locality;    		
			//  //cc
			//  private String companyName;
			//  private String orgNumber;
			//  private String companyVatNumber;
			//  private String addressSelector;    		
    		
    		// TODO remove null refs, if returned, set values! 
    		
    		companyCustomer.setPhoneNumber( null );
    		companyCustomer.setEmail( null );
    		companyCustomer.setIpAddress( null );
    		companyCustomer.setStreetAddress( returnChildNodeValue(node, "Street") );	// one argument version sets HouseNumber to null
    		companyCustomer.setCoAddress( returnChildNodeValue(node, "CoAddress") );
    		companyCustomer.setZipCode( returnChildNodeValue(node, "ZipCode") );
    		companyCustomer.setLocality( returnChildNodeValue(node, "Locality") );
	    	// this.customer.setCountryCode( returnChildNodeValue(node, "Street") ); // TODO not implemented...
    		companyCustomer.setCompanyName( returnChildNodeValue(node, "FullName") );
    		companyCustomer.setNationalIdNumber( returnChildNodeValue(node, "NationalIdNumber") );
    		companyCustomer.setVatNumber( null );
    		companyCustomer.setAddressSelector( null );

			this.customer = companyCustomer;
    	}
    	
    }
    
    private void setCustomerIdentityResponse( Node node ) {
        // Set child nodes from CustomerIdentityResponse
        setChildNodeValue(node, "NationalIdNumber");
        setChildNodeValue(node, "Email");
        setChildNodeValue(node, "PhoneNumber");
        setChildNodeValue(node, "FullName");
        setChildNodeValue(node, "Street");
        setChildNodeValue(node, "CoAddress");
        setChildNodeValue(node, "ZipCode");
        setChildNodeValue(node, "HouseNumber");
        setChildNodeValue(node, "Locality");
        setChildNodeValue(node, "CountryCode");
        setChildNodeValue(node, "CustomerType");
    }
    
    
    // sets customerIdentity attribue "tagName" to tagValue via triggered side effect
    private void setChildNodeValue(Node n, String tagName) {
        String tagValue = "";
        
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int length = nl.getLength();
            
            for (int j = 0; j < length; j++) {
                Node childNode = nl.item(j);
                String nodeName = childNode.getNodeName();
                
                if (nodeName.equals(tagName)) {
                    tagValue = getTagValue((Element) n, tagName);
                    
                    if (tagValue != null) {
                        this.customerIdentity.setValue(tagName, tagValue);
                    }
                }
                
                setChildNodeValue(childNode, tagName);
            }
        }
    }
    
    // hack to return tagName value by recursing over nodes, bubbling up found values
    private String returnChildNodeValue(Node n, String tagName) {
    	String tagValue = null;
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int length = nl.getLength();
            
            for (int j = 0; j < length; j++) {
                Node childNode = nl.item(j);
                String nodeName = childNode.getNodeName();
                
                if (nodeName.equals(tagName)) {
                    tagValue = getTagValue((Element) n, tagName);                    
                }
                // if not found, go deeper
                if( tagValue == null ) {
                	tagValue = returnChildNodeValue(childNode, tagName);
                }
            }
        }
        return tagValue;
    }
}
