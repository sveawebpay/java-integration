package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class CreditOrderRowsResponse extends AdminServiceResponse {
																				// see AS/DeliverOrderResult	
	/** Id that identifies an order in sveawebpay's system */
	private Long orderId;														// SveaOrderId		
  	/** Id that identifies a client in sveawebpay's system */	
    private Long clientId;														// ClientId
	private ORDERTYPE orderType;												// OrderType
    /** The credit invoice id (set iff accepted, orderType Invoice, else 0)*/
	private Long creditInvoiceId;												// DeliveryReferenceNumber
    /** The amount credited with this request */
	private Double amount;														// DeliveredAmount
 
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public ORDERTYPE getOrderType() {
		return orderType;
	}
	public void setOrderType(ORDERTYPE orderType) {
		this.orderType = orderType;
	}
	public  Long getCreditInvoiceId() {
		return creditInvoiceId;
	}
	public void setCreditInvoiceId(Long creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
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
		this.setClientId(Long.valueOf(clientId));
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.parseDouble(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setCreditInvoiceId( Long.valueOf(deliveryReferenceNumber) );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( Long.valueOf(sveaOrderId) );
	}
}
