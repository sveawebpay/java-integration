package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrdersResponse extends AdminServiceResponse implements Respondable {

    /** Id that identifies a client in sveawebpay's system */	
    public String clientId;
	
    /** The amount delivered with this request */
    public Double amount;

    /** The invoice id for the delivered order (set iff accepted, orderType Invoice)*/
    public String invoiceId;

    /**  The contract number for the delivered order (set iff accepted, orderType PaymentPlan)  */
    public String contractNumber;
    
    public ORDERTYPE orderType;

    /** Id that identifies an order in sveawebpay's system */
    public String orderId;   	
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public ORDERTYPE getOrderType() {
		return orderType;
	}

	public void setOrderType(ORDERTYPE orderType) {
		this.orderType = orderType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public DeliverOrdersResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setDeliverOrdersResponseAttributes( soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*") );
    	}
    }

	private void setDeliverOrdersResponseAttributes(NodeList xmlResponse) {
		Node deliverOrdersResult=xmlResponse.item(1);
		Node ordersDelivered = deliverOrdersResult.getChildNodes().item(2);		// 0: ErrorMessage, 1: ResultCode
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);	// we allow deliveries of 1 order only, so use first result node		
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setInvoiceId( deliveryReferenceNumber );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		if( orderType.equals(ORDERTYPE.PaymentPlan.toString()) ) {
			this.setContractNumber( deliveryReferenceNumber );		
			this.setOrderType( ORDERTYPE.PaymentPlan );			
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( sveaOrderId );
	}	
}