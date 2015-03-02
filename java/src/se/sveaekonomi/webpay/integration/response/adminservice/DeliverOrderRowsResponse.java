package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrderRowsResponse extends AdminServiceResponse {
																				// see AS/DeliverOrderResult	
	/** Id that identifies an order in sveawebpay's system */
	private Long orderId;														// SveaOrderId		
	// N/A																		// ClientOrderId	
  	/** Id that identifies a client in sveawebpay's system */	
    private Long clientId;														// ClientId
	private ORDERTYPE orderType;												// OrderType
    /** The invoice id (set iff accepted, orderType Invoice, else 0)*/
    public Long invoiceId;														// DeliveryReferenceNumber
    /**  The contract number for the delivered order (set iff accepted, orderType PaymentPlan, else 0)  */
    public Long contractNumber;													// DeliveryReferenceNumber
	/** The amount delivered with this request */
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
	public Long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	public Long getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(Long contractNumber) {
		this.contractNumber = contractNumber;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public DeliverOrderRowsResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setDeliverOrderRowsResponseAttributes( soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*") );
    	}
    }
    
	private void setDeliverOrderRowsResponseAttributes(NodeList xmlResponse) {
		Node deliverPartialResult=xmlResponse.item(1);
		Node ordersDelivered = deliverPartialResult.getChildNodes().item(2);
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);		
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( Long.valueOf(clientId) );
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setInvoiceId( Long.valueOf(deliveryReferenceNumber) );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		if( orderType.equals(ORDERTYPE.PaymentPlan.toString()) ) {
			this.setContractNumber( Long.valueOf(deliveryReferenceNumber) );		
			this.setOrderType( ORDERTYPE.PaymentPlan );			
		}
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( Long.parseLong(sveaOrderId) );
	}
}
