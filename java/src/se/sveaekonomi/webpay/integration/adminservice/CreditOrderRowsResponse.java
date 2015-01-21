package se.sveaekonomi.webpay.integration.adminservice;

import java.util.ArrayList;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class CreditOrderRowsResponse extends AdminServiceResponse {

	private String orderId;
	
	private Double amount;	
	
	private ORDERTYPE orderType;

	/** the creditInvoiceId for the credit invoice issued with this request (set iff accepted, orderType Invoice only) */
	public String creditInvoiceId;
	
    /** Id that identifies a client in sveawebpay system */
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
		// set common response attributes
		super(soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*"));

    	if( this.isOrderAccepted() ) {
    		setCreditOrderRowsResponseAttributes(soapResponse);
    	}
	}
	
	private void setCreditOrderRowsResponseAttributes(SOAPMessage soapResponse) throws SOAPException {
		
		//<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
		//   <s:Body>
		NodeList xmlResponse = soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("*");
		//      <CreditInvoiceRowsResponse xmlns="http://tempuri.org/">
		//         <CreditInvoiceRowsResult xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
    	Node creditInvoiceRowsResponse=xmlResponse.item(0);
		Node creditInvoiceRowsResult=xmlResponse.item(1);
		//            <a:ErrorMessage i:nil="true"/>
		//            <a:ResultCode>0</a:ResultCode>
		//            <a:OrdersDelivered>
		Node ordersDelivered = creditInvoiceRowsResult.getChildNodes().item(2);		// 0: ErrorMessage, 1: ResultCode
		//               <a:DeliverOrderResult>
		Element dor = (Element) ordersDelivered.getChildNodes().item(0);	// we allow deliveries of 1 order only, so use first result node		
		//                  <a:ClientId>79021</a:ClientId>
		String clientId = dor.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		//                  <a:DeliveredAmount>125.00</a:DeliveredAmount>
		String deliveredAmount = dor.getElementsByTagName("a:DeliveredAmount").item(0).getTextContent();
		this.setAmount( Double.valueOf(deliveredAmount) );
		//                  <a:DeliveryReferenceNumber>1043822</a:DeliveryReferenceNumber>
		String deliveryReferenceNumber = dor.getElementsByTagName("a:DeliveryReferenceNumber").item(0).getTextContent();
		//                  <a:OrderType>Invoice</a:OrderType>
		String orderType = dor.getElementsByTagName("a:OrderType").item(0).getTextContent();
		if( orderType.equals(ORDERTYPE.Invoice.toString()) ) {
			this.setCreditInvoiceId( deliveryReferenceNumber );
			this.setOrderType( ORDERTYPE.Invoice );
		}
		//                  <a:SveaOrderId>509100</a:SveaOrderId>
		String sveaOrderId = dor.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( sveaOrderId );
		//               </a:DeliverOrderResult>
		//            </a:OrdersDelivered>
		//         </CreditInvoiceRowsResult>
		//      </CreditInvoiceRowsResponse>
		//   </s:Body>
		//</s:Envelope>		
	}
}
