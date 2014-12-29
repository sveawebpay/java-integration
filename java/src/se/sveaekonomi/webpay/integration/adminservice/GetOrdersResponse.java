package se.sveaekonomi.webpay.integration.adminservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;

public class GetOrdersResponse extends AdminServiceResponse {

	// phpdoc attributes below takes its info from admin service api Order structure
    /** Date when order status was changed, e.g when order was delivered, or null if not set.*/
    public String changedDate;
    /** Id that identifies a client in sveawebpay system */
    public String clientId;
    /** Order number from client's ordersystem */
    public String clientOrderId;
    /** Date when order was first created. */
    public String createdDate;

    /** Tells if credit decision is accepted or not */
    public Boolean creditReportStatusAccepted;
    /** Date of order credit decision. */
    public String creditReportStatusCreationDate;

    /** Country currency */
    public String currency;

    /** @var CompanyCustomer|IndividualCustomer $customer -- customer identity as associated with the order by Svea, also Shipping address. */
    public CustomerIdentity customer;

    /** Customer id that is created by SveaWebPay system. */
    public String customerId;
    /** Customer Reference. (Gets printed on the invoice.)*/
    public String customerReference;
    public Boolean isPossibleToAdminister;
    /** Tells if order can be cancelled or not */
    public Boolean isPossibleToCancel;
    /** Text on order created by client */
    public String notes;
    /** one of {Created,PartiallyDelivered,Delivered,Cancelled} */
    public String orderDeliveryStatus; // TODO change to enum!

    /** @var Svea\OrderRow[] $numberedOrderRows  array of OrderRow objects, note that invoice and payment plan order rows name attribute will be null */
    //public YYY numberedOrderRows;

    /** one of {Created,Pending,Active,Denied,Error}*/
    public String orderStatus; // TODO change to enum!
    public String getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(String changedDate) {
		this.changedDate = changedDate;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getCreditReportStatusAccepted() {
		return creditReportStatusAccepted;
	}

	public void setCreditReportStatusAccepted(Boolean creditReportStatusAccepted) {
		this.creditReportStatusAccepted = creditReportStatusAccepted;
	}

	public String getCreditReportStatusCreationDate() {
		return creditReportStatusCreationDate;
	}

	public void setCreditReportStatusCreationDate(
			String creditReportStatusCreationDate) {
		this.creditReportStatusCreationDate = creditReportStatusCreationDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public IndividualCustomer getIndividualCustomer() {
		return (IndividualCustomer) getCustomer();
	}
	
	public CompanyCustomer getCompanyCustomer() {
		return (CompanyCustomer) getCustomer();
	}

	
	private CustomerIdentity getCustomer() {
		return customer;
	}	
	
	public void setCustomer( CustomerIdentity customer ) {
		this.customer = customer;
	}
	
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public Boolean getIsPossibleToAdminister() {
		return isPossibleToAdminister;
	}

	public void setIsPossibleToAdminister(Boolean isPossibleToAdminister) {
		this.isPossibleToAdminister = isPossibleToAdminister;
	}

	public Boolean getIsPossibleToCancel() {
		return isPossibleToCancel;
	}

	public void setIsPossibleToCancel(Boolean isPossibleToCancel) {
		this.isPossibleToCancel = isPossibleToCancel;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOrderDeliveryStatus() {
		return orderDeliveryStatus;
	}

	public void setOrderDeliveryStatus(String orderDeliveryStatus) {
		this.orderDeliveryStatus = orderDeliveryStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPaymentPlanDetailsContractLengthMonths() {
		return paymentPlanDetailsContractLengthMonths;
	}

	public void setPaymentPlanDetailsContractLengthMonths(
			String paymentPlanDetailsContractLengthMonths) {
		this.paymentPlanDetailsContractLengthMonths = paymentPlanDetailsContractLengthMonths;
	}

	public String getPaymentPlanDetailsContractNumber() {
		return paymentPlanDetailsContractNumber;
	}

	public void setPaymentPlanDetailsContractNumber(
			String paymentPlanDetailsContractNumber) {
		this.paymentPlanDetailsContractNumber = paymentPlanDetailsContractNumber;
	}

	public String getPendingReasonsPendingType() {
		return pendingReasonsPendingType;
	}

	public void setPendingReasonsPendingType(String pendingReasonsPendingType) {
		this.pendingReasonsPendingType = pendingReasonsPendingType;
	}

	public String getPendingReasonsCreatedDate() {
		return pendingReasonsCreatedDate;
	}

	public void setPendingReasonsCreatedDate(String pendingReasonsCreatedDate) {
		this.pendingReasonsCreatedDate = pendingReasonsCreatedDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Boolean getSveaWillBuy() {
		return sveaWillBuy;
	}

	public void setSveaWillBuy(Boolean sveaWillBuy) {
		this.sveaWillBuy = sveaWillBuy;
	}

	/** @var one of {Invoice,PaymentPlan} */
    public String orderType; // TODO change to enum!

    public String paymentPlanDetailsContractLengthMonths;
    /** Contract number of a specific contract. */
    public String paymentPlanDetailsContractNumber;

    /** one of {SMSOnHighAmount,UseOfDeliveryAddress} */
    public String pendingReasonsPendingType;  // TODO change to enum!
    public String pendingReasonsCreatedDate;

    /** Unique Id for the created order. Used for any further order webservice requests. */
    public String orderId;
    /** Describes whether SveaWebPay will buy the order or just administrate it */
    public Boolean sveaWillBuy;	

    public GetOrdersResponse(NodeList xmlResponse) {		
    	super( xmlResponse ); // handle ErrorMessage, ResultCode
    	setGetOrdersResponseAttributes(xmlResponse);
	}
    
	private void setGetOrdersResponseAttributes(NodeList xmlResponse) {
	    //<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
		//	<s:Body>
		//		<GetOrdersResponse xmlns="http://tempuri.org/">
		//			<GetOrdersResult
		//				xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service"
		//				xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
		//				<a:ErrorMessage i:nil="true" />
		//				<a:ResultCode>0</a:ResultCode>
		//				<a:Orders>
		//					<a:Order>
    	Node getOrdersResponse=xmlResponse.item(0);
		Node getOrdersResult=xmlResponse.item(1);
		Node orders = getOrdersResult.getChildNodes().item(2);		// 0: ErrorMessage, 1: ResultCode
		Element order = (Element) orders.getChildNodes().item(0);	// we allow queries for 1 order only, so use first result node
//		
//System.out.println("--");
//int size = order.getChildNodes().getLength();
//for (int i = 0; i < size; i++) {
//	Element node = (Element) order.getChildNodes().item(i);
//	String nodeName = node.getNodeName();
//	System.out.println(nodeName);	
//}		

		//						<a:ChangedDate i:nil="true" />
		String changedDate = order.getElementsByTagName("a:ChangedDate").item(0).getTextContent(); // getTextContent() of <a:ChangedDate i:nil="true" /> is ""
		this.setChangedDate( changedDate.equals("") ? null : changedDate );
		//						<a:ClientId>79021</a:ClientId>
		String clientId = order.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		//						<a:ClientOrderId>449</a:ClientOrderId>
		String clientOrderId = order.getElementsByTagName("a:ClientOrderId").item(0).getTextContent();
		this.setClientOrderId(clientOrderId);
		//						<a:CreatedDate>2014-05-19T16:04:54.787</a:CreatedDate>
		String createdDate = order.getElementsByTagName("a:CreatedDate").item(0).getTextContent();
		this.setCreatedDate(createdDate);
		//						<a:CreditReportStatus>		
		Node creditReportStatus = order.getElementsByTagName("a:CreditReportStatus").item(0);
		//							<a:Accepted>true</a:Accepted>
		Boolean creditReportStatusAccepted = Boolean.valueOf(creditReportStatus.getChildNodes().item(0).getTextContent()); 
		this.setCreditReportStatusAccepted(creditReportStatusAccepted);		
		//							<a:CreationDate>2014-05-19T16:04:54.893</a:CreationDate>
		//						</a:CreditReportStatus>
		this.setCreditReportStatusCreationDate(creditReportStatus.getChildNodes().item(1).getTextContent());			
		//						<a:Currency>SEK</a:Currency>
		String currency = order.getElementsByTagName("a:Currency").item(0).getTextContent();
		this.setCurrency(currency);
		//       				<a:Customer
		Element c = (Element) order.getElementsByTagName("a:Customer").item(0);
		//							xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
		//							<b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
		String cCoAddress = c.getElementsByTagName("b:CoAddress").item(0).getTextContent();
		//							<b:CompanyIdentity i:nil="true" />
		String cCompanyIdentity = c.getElementsByTagName("b:CompanyIdentity").item(0).getTextContent();
		//							<b:CountryCode>SE</b:CountryCode>
		String cCountryCode = c.getElementsByTagName("b:CountryCode").item(0).getTextContent();
		//							<b:CustomerType>Individual</b:CustomerType>
		String cCustomerType = c.getElementsByTagName("b:CustomerType").item(0).getTextContent();
		//							<b:Email>daniel@colourpicture.se</b:Email>
		String cEmail = c.getElementsByTagName("b:Email").item(0).getTextContent();
		//							<b:FullName>Persson, Tess T</b:FullName>
		String cFullName = c.getElementsByTagName("b:FullName").item(0).getTextContent();
		//							<b:HouseNumber i:nil="true" />
		String cHouseNumber = c.getElementsByTagName("b:HouseNumber").item(0).getTextContent();
		//							<b:IndividualIdentity>
		String cIndividualIdentity = c.getElementsByTagName("b:IndividualIdentity").item(0).getTextContent();
		//								<b:BirthDate i:nil="true" />
		String iiBirthDate = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:BirthDate").item(0).getTextContent();
		//								<b:FirstName i:nil="true" />
		String iiFirstName = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:FirstName").item(0).getTextContent();
		//								<b:Initials i:nil="true" />
		String iiInitials = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:Initials").item(0).getTextContent();
		//								<b:LastName i:nil="true" />
		String iiLastName = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:LastName").item(0).getTextContent();
		//							</b:IndividualIdentity>
		//							<b:Locality>Stan</b:Locality>
		String cLocality = c.getElementsByTagName("b:Locality").item(0).getTextContent();
		//							<b:NationalIdNumber>194605092222</b:NationalIdNumber>
		String cNationalIdNumber = c.getElementsByTagName("b:NationalIdNumber").item(0).getTextContent();
		//							<b:PhoneNumber>08-11111111</b:PhoneNumber>
		String cPhoneNumber = c.getElementsByTagName("b:PhoneNumber").item(0).getTextContent();
		//							<b:PublicKey i:nil="true" />
		String cPublicKey = c.getElementsByTagName("b:PublicKey").item(0).getTextContent(); //TODO not used in return
		//							<b:Street>Testgatan 1</b:Street>
		String cStreet = c.getElementsByTagName("b:Street").item(0).getTextContent();
		//							<b:ZipCode>99999</b:ZipCode>
		String cZipCode = c.getElementsByTagName("b:ZipCode").item(0).getTextContent();
		//						</a:Customer>			
		if( cCustomerType.endsWith("Individual") ) {
			IndividualCustomer individualCustomer = new IndividualCustomer();
		
			individualCustomer.setNationalIdNumber( cNationalIdNumber );
			individualCustomer.setEmail( cEmail );
			individualCustomer.setPhoneNumber( cPhoneNumber );
			//ipAddress;
			individualCustomer.setName( cFullName ); // FullName
			individualCustomer.setStreetAddress( cStreet, cHouseNumber );
			individualCustomer.setCoAddress( cCoAddress );
			individualCustomer.setZipCode( cZipCode );
			individualCustomer.setLocality( cLocality );
			individualCustomer.setName( iiFirstName, iiLastName ); // FirstName, LastName
			individualCustomer.setInitials( iiInitials );
			individualCustomer.setBirthDate( iiBirthDate );

			this.setCustomer(individualCustomer);
		}
		if( cCustomerType.endsWith("Company") ) {
			// TODO handle companyCustomer
		}		
		//						<a:CustomerId>1000117</a:CustomerId>
		String customerId = order.getElementsByTagName("a:CustomerId").item(0).getTextContent();
		this.setCustomerId(customerId);
		//						<a:CustomerReference />
		String customerReference = order.getElementsByTagName("a:CustomerReference").item(0).getTextContent();			//TODO check value when <... />
		this.setCustomerReference(customerReference.equals("") ? null : customerReference);
		//						<a:DeliveryAddress i:nil="true"
		//							xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//not supported	
		//						<a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
		String isPossibleToAdminister = order.getElementsByTagName("a:IsPossibleToAdminister").item(0).getTextContent();	
		this.setIsPossibleToAdminister(isPossibleToAdminister.equals("true") ? true : false);
		//						<a:IsPossibleToCancel>true</a:IsPossibleToCancel>
		String isPossibleToCancel = order.getElementsByTagName("a:IsPossibleToCancel").item(0).getTextContent();	
		this.setIsPossibleToCancel(isPossibleToCancel.equals("true") ? true : false);
		//						<a:Notes i:nil="true" />
		String notes = order.getElementsByTagName("a:Notes").item(0).getTextContent();	
		this.setNotes(notes.equals("") ? null : notes);
		//						<a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
		String cOrderDeliveryStatus = order.getElementsByTagName("a:OrderDeliveryStatus").item(0).getTextContent();	
		this.setOrderDeliveryStatus(cOrderDeliveryStatus);
		//						<a:OrderRows>
		// TODO handle rows
		//							<a:NumberedOrderRow>
		//								<ArticleNumber i:nil="true"
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<Description
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Dyr produkt 25%</Description>
		//								<DiscountPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
		//								<NumberOfUnits
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2.00</NumberOfUnits>
		//								<PriceIncludingVat
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
		//								<PricePerUnit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2000.00</PricePerUnit>
		//								<Unit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<VatPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
		//								<a:CreditInvoiceId i:nil="true" />
		//								<a:InvoiceId i:nil="true" />
		//								<a:RowNumber>1</a:RowNumber>
		//								<a:Status>NotDelivered</a:Status>
		//							</a:NumberedOrderRow>
		//							<a:NumberedOrderRow>
		//								<ArticleNumber i:nil="true"
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<Description
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Testprodukt 1kr 25%</Description>
		//								<DiscountPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
		//								<NumberOfUnits
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
		//								<PriceIncludingVat
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
		//								<PricePerUnit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</PricePerUnit>
		//								<Unit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<VatPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
		//								<a:CreditInvoiceId i:nil="true" />
		//								<a:InvoiceId i:nil="true" />
		//								<a:RowNumber>2</a:RowNumber>
		//								<a:Status>NotDelivered</a:Status>
		//							</a:NumberedOrderRow>
		//							<a:NumberedOrderRow>
		//								<ArticleNumber i:nil="true"
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<Description
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Fastpris (Fast fraktpris)</Description>
		//								<DiscountPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
		//								<NumberOfUnits
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
		//								<PriceIncludingVat
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
		//								<PricePerUnit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">4.00</PricePerUnit>
		//								<Unit i:nil="true"
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<VatPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
		//								<a:CreditInvoiceId i:nil="true" />
		//								<a:InvoiceId i:nil="true" />
		//								<a:RowNumber>3</a:RowNumber>
		//								<a:Status>NotDelivered</a:Status>
		//							</a:NumberedOrderRow>
		//							<a:NumberedOrderRow>
		//								<ArticleNumber
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<Description
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Svea Fakturaavgift:: 20.00kr (SE)</Description>
		//								<DiscountPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
		//								<NumberOfUnits
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
		//								<PriceIncludingVat
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
		//								<PricePerUnit
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">20.00</PricePerUnit>
		//								<Unit i:nil="true"
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//								<VatPercent
		//									xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</VatPercent>
		//								<a:CreditInvoiceId i:nil="true" />
		//								<a:InvoiceId i:nil="true" />
		//								<a:RowNumber>4</a:RowNumber>
		//								<a:Status>NotDelivered</a:Status>
		//							</a:NumberedOrderRow>
		//						</a:OrderRows>
		//						<a:OrderStatus>Active</a:OrderStatus>
		String orderStatus = order.getElementsByTagName("a:OrderStatus").item(0).getTextContent();
		this.setOrderStatus(orderStatus);	
		//						<a:OrderType>Invoice</a:OrderType>
		String orderType = order.getElementsByTagName("a:OrderType").item(0).getTextContent();
		this.setOrderType(orderType);
		//						<a:PaymentPlanDetails i:nil="true" />
		Node paymentPlanDetails = order.getElementsByTagName("a:PaymentPlanDetails").item(0);
		// TODO check paymentplandetails when querying payment plan order		
//		String paymentPlanDetailsContractLengthMonths = paymentPlanDetails.getChildNodes().item(0).getTextContent(); 	// TODO check
//		this.setPaymentPlanDetailsContractLengthMonths(paymentPlanDetailsContractLengthMonths);
//		String paymentPlanDetailsContractNumber = paymentPlanDetails.getChildNodes().item(1).getTextContent(); 	// TODO check
//		this.setPaymentPlanDetailsContractNumber(paymentPlanDetailsContractNumber);
		
		//						<a:PendingReasons />
		Node pendingReasons = order.getElementsByTagName("a:PendingReasons").item(0);
		// TODO check pendingReasons when querying order		
//		String pendingReasonsPendingType = pendingReasons.getChildNodes().item(0).getTextContent(); 	// TODO check
//		this.setPendingReasonsPendingType(pendingReasonsPendingType);
//		String pendingReasonsCreatedDate = paymentPlanDetails.getChildNodes().item(1).getTextContent(); 	// TODO check
//		this.setPendingReasonsCreatedDate(pendingReasonsCreatedDate);
		
		//						<a:SveaOrderId>348629</a:SveaOrderId>
		String orderId = order.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId(orderId);
		//						<a:SveaWillBuy>true</a:SveaWillBuy>
		String sveaWillBuy = order.getElementsByTagName("a:SveaWillBuy").item(0).getTextContent();	
		this.setSveaWillBuy(sveaWillBuy.equals("true") ? true : false); //	or (Boolean.valueOf(node.getChildNodes().item(0).getTextContent()));

		//					</a:Order>
		//				</a:Orders>
		//			</GetOrdersResult>
		//		</GetOrdersResponse>
		//	</s:Body>
		//</s:Envelope>
	}
}
