package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class CreditOrderRowsResponse extends AdminServiceResponse {

    /** Id that identifies an order in sveawebpay's system */
	private long orderId;
    /** The amount credited with this request */
	private double amount;	
	private ORDERTYPE orderType;
    /** The credit invoice id (set iff accepted, orderType Invoice, else 0)*/
	private int creditInvoiceId;
  	/** Id that identifies a client in sveawebpay's system */	
    private int clientId;
    
	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public  int getCreditInvoiceId() {
		return creditInvoiceId;
	}

	public void setCreditInvoiceId(int creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
	}

	public ORDERTYPE getOrderType() {
		return orderType;
	}

	public void setOrderType(ORDERTYPE orderType) {
		this.orderType = orderType;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
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
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);		
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId(Integer.parseInt(clientId));
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.parseDouble(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setCreditInvoiceId( Integer.parseInt(deliveryReferenceNumber) );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( Long.parseLong(sveaOrderId) );
	}
}
