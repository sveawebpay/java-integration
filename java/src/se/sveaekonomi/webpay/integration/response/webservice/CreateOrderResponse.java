package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public CustomerIdentityResponse customerIdentity;
    public String expirationDate;
	public String clientOrderNumber;
	/** derived from customerIdentity customerType */
    public boolean isIndividualIdentity;
    
    // getters and setters use same types as php package, non-fluent return type
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
    
    public CustomerIdentityResponse getCustomerIdentity() {
		return customerIdentity;
	}
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
            
            // Set child nodes from CustomerIdentity
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
}
