package se.sveaekonomi.webpay.integration.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrderRowsResponse extends AdminServiceResponse {

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

	public DeliverOrderRowsResponse(SOAPMessage soapResponse) throws SOAPException {
		// set common response attributes
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setDeliverOrderRowsResponseAttributes( soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*") );
    	}
    }
    
	private void setDeliverOrderRowsResponseAttributes(NodeList xmlResponse) {
		//<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
		//   <s:Body>
		//      <DeliverPartialResponse xmlns="http://tempuri.org/">
		//         <DeliverPartialResult xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
    	Node deliverPartialResponse=xmlResponse.item(0);
		Node deliverPartialResult=xmlResponse.item(1);
		//            <a:ErrorMessage i:nil="true"/>
		//            <a:ResultCode>0</a:ResultCode>
		//            <a:OrdersDelivered>
		Node ordersDelivered = deliverPartialResult.getChildNodes().item(2);		// 0: ErrorMessage, 1: ResultCode
		//               <a:DeliverOrderResult>
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);	// we allow deliveries of 1 order only, so use first result node		
		//                  <a:ClientId>79021</a:ClientId>
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		//                  <a:DeliveredAmount>125.00</a:DeliveredAmount>
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		//                  <a:DeliveryReferenceNumber>1043580</a:DeliveryReferenceNumber>
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		//                  <a:OrderType>Invoice</a:OrderType>
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setInvoiceId( deliveryReferenceNumber );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		if( orderType.equals(ORDERTYPE.PaymentPlan.toString()) ) {
			this.setContractNumber( deliveryReferenceNumber );		
			this.setOrderType( ORDERTYPE.PaymentPlan );			
		}
		//                  <a:SveaOrderId>507018</a:SveaOrderId>
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( sveaOrderId );
		//               </a:DeliverOrderResult>
		//            </a:OrdersDelivered>
		//         </DeliverPartialResult>
		//      </DeliverPartialResponse>
		//   </s:Body>
		//</s:Envelope>
	}
}
