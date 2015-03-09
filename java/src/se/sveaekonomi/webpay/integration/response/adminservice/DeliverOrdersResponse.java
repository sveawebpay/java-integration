package se.sveaekonomi.webpay.integration.response.adminservice;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrdersResponse extends AdminServiceResponse implements Respondable {
																							// see AS/DeliverOrderResult:
    /** Id that identifies an order in sveawebpay's system */
    private Long orderId;   	    														// SveaOrderId (Long)
    // private String ClientOrderId															// ? ClientOrderId (String) -- not returned? TODO check
  	/** Id that identifies a client in sveawebpay's system */	
    private Long clientId;																	// ClientId (Long)
    private ORDERTYPE orderType;															// OrderType (Enum{Invoice|PaymentPlan})
    /** Invoice id for the delivered order (iff accepted, Invoice, else null) */    
    private Long invoiceId;																	// DeliveryReferenceNumber (Long)
    /** Contract number for the delivered order (iff accepted, PaymentPlan, else null) */
    private Long contractNumber;															// DeliveryReferenceNumber (Long)
	/** The amount delivered with this request (including vat)*/
    private Double amount;																	// DeliveredAmount (Decimal)
   

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
	

	public DeliverOrdersResponse(SOAPMessage soapResponse) throws SOAPException {
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setDeliverOrdersResponseAttributes( soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*") );
    	}
    }

	private void setDeliverOrdersResponseAttributes(NodeList xmlResponse) {
		
		//<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
		//   <s:Body>
		//      <DeliverOrdersResponse xmlns="http://tempuri.org/">
		//         <DeliverOrdersResult xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
		//            <a:ErrorMessage i:nil="true"/>
		//            <a:ResultCode>0</a:ResultCode>
		//            <a:OrdersDelivered>
		//               <a:DeliverOrderResult>
		//                  <a:ClientId>79021</a:ClientId>
		//                  <a:DeliveredAmount>1562.50</a:DeliveredAmount>
		//                  <a:DeliveryReferenceNumber>1047605</a:DeliveryReferenceNumber>
		//                  <a:OrderType>Invoice</a:OrderType>
		//                  <a:SveaOrderId>539672</a:SveaOrderId>
		//               </a:DeliverOrderResult>
		//            </a:OrdersDelivered>
		//         </DeliverOrdersResult>
		//      </DeliverOrdersResponse>
		//   </s:Body>
		//</s:Envelope>		
				
		Node deliverOrdersResult=xmlResponse.item(1);
		Node ordersDelivered = deliverOrdersResult.getChildNodes().item(2);
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