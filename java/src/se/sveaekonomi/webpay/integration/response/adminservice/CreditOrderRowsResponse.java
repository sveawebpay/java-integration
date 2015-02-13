package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class CreditOrderRowsResponse extends AdminServiceResponse {

	private String orderId;
	
	private Double amount;	
	
	private ORDERTYPE orderType;

	public String creditInvoiceId;
	
    public String clientId;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCreditInvoiceId() {
		return creditInvoiceId;
	}

	public void setCreditInvoiceId(String creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
	}

	public ORDERTYPE getOrderType() {
		return orderType;
	}

	public void setOrderType(ORDERTYPE orderType) {
		this.orderType = orderType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public CreditOrderRowsResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setCreditOrderRowsResponseAttributes(soapResponse);
    	}
	}
	
	private void setCreditOrderRowsResponseAttributes(SOAPMessage soapResponse) throws SOAPException {
		NodeList xmlResponse = soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*");
		Node creditInvoiceRowsResult=xmlResponse.item(1);
		Node ordersDelivered = creditInvoiceRowsResult.getChildNodes().item(2);
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);	// we allow deliveries of 1 order only, so use first result node		
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setCreditInvoiceId( deliveryReferenceNumber );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( sveaOrderId );
	}
}
