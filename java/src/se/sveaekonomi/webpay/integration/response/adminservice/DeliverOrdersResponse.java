package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrdersResponse extends AdminServiceResponse implements Respondable {

    /** Id that identifies an order in sveawebpay's system */
    public long orderId;   	
    /** The amount delivered with this request */
    public double amount;
    public ORDERTYPE orderType;
    /** The invoice id for the delivered order (set iff accepted and orderType Invoice, else 0)*/
    public int invoiceId;
    /**  The contract number for the delivered order (set iff accepted, orderType PaymentPlan, else 0)  */
    public int contractNumber;
  	/** Id that identifies a client in sveawebpay's system */	
    public int clientId;
	
	public int getClientId() { return clientId;	}
	public void setClientId(int clientId) { this.clientId = clientId; }

	public double getAmount() { return amount; }
	public void setAmount(double amount) { this.amount = amount; }

	public int getInvoiceId() {return invoiceId;}
	public void setInvoiceId(int invoiceId) {this.invoiceId = invoiceId;}

	public int getContractNumber() {return contractNumber;}
	public void setContractNumber(int contractNumber) {	this.contractNumber = contractNumber;}

	public ORDERTYPE getOrderType() {return orderType;}
	public void setOrderType(ORDERTYPE orderType) {	this.orderType = orderType; }

	public long getOrderId() {return orderId; }
	public void setOrderId(long orderId) {this.orderId = orderId; }

	public DeliverOrdersResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setDeliverOrdersResponseAttributes( soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*") );
    	}
    }

	private void setDeliverOrdersResponseAttributes(NodeList xmlResponse) {
		Node deliverOrdersResult=xmlResponse.item(1);
		Node ordersDelivered = deliverOrdersResult.getChildNodes().item(2);
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);		
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( Integer.parseInt(clientId) );
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setInvoiceId( Integer.parseInt(deliveryReferenceNumber) );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		if( orderType.equals(ORDERTYPE.PaymentPlan.toString()) ) {
			this.setContractNumber( Integer.parseInt(deliveryReferenceNumber) );		
			this.setOrderType( ORDERTYPE.PaymentPlan );			
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( Long.parseLong(sveaOrderId) );
	}	
}