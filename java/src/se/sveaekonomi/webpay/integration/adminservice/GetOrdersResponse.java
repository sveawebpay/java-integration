package se.sveaekonomi.webpay.integration.adminservice;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.OrderRowStatus;

public class GetOrdersResponse extends AdminServiceResponse {

	// javadoc attributes below takes its info from admin service api Order structure
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
    public CustomerIdentity<?> customer;

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
    public ArrayList<NumberedOrderRowBuilder> numberedOrderRows;

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

	/** null for invoice orders */
	public String getPaymentPlanDetailsContractLengthMonths() {
		return paymentPlanDetailsContractLengthMonths;
	}

	public void setPaymentPlanDetailsContractLengthMonths(
			String paymentPlanDetailsContractLengthMonths) {
		this.paymentPlanDetailsContractLengthMonths = paymentPlanDetailsContractLengthMonths;
	}

	/** null for invoice orders */
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
	
	public void setNumberedOrderRows( ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows = numberedOrderRows;
	}
	
	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return this.numberedOrderRows;
	}

	/** @var one of {Invoice,PaymentPlan} */
    public String orderType; // TODO change to enum!

    /** Contract length or null for invoice orders */
    public String paymentPlanDetailsContractLengthMonths;
    /** Contract number of a specific contract or null for invoice orders */
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
    	if( this.isOrderAccepted() ) {
    		setGetOrdersResponseAttributes(xmlResponse);
    	}
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
		Element o = (Element) orders.getChildNodes().item(0);	// we allow queries for 1 order only, so use first result node

		//						<a:ChangedDate i:nil="true" />
		String changedDate = o.getElementsByTagName("a:ChangedDate").item(0).getTextContent(); // getTextContent() of <a:ChangedDate i:nil="true" /> is ""
		this.setChangedDate( changedDate.equals("") ? null : changedDate );
		//						<a:ClientId>79021</a:ClientId>
		String clientId = o.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId );
		//						<a:ClientOrderId>449</a:ClientOrderId>
		String clientOrderId = o.getElementsByTagName("a:ClientOrderId").item(0).getTextContent();
		this.setClientOrderId(clientOrderId);
		//						<a:CreatedDate>2014-05-19T16:04:54.787</a:CreatedDate>
		String createdDate = o.getElementsByTagName("a:CreatedDate").item(0).getTextContent();
		this.setCreatedDate(createdDate);
		//						<a:CreditReportStatus>		
		Node creditReportStatus = o.getElementsByTagName("a:CreditReportStatus").item(0);
		//							<a:Accepted>true</a:Accepted>
		Boolean creditReportStatusAccepted = Boolean.valueOf(creditReportStatus.getChildNodes().item(0).getTextContent()); 
		this.setCreditReportStatusAccepted(creditReportStatusAccepted);		
		//							<a:CreationDate>2014-05-19T16:04:54.893</a:CreationDate>
		//						</a:CreditReportStatus>
		this.setCreditReportStatusCreationDate(creditReportStatus.getChildNodes().item(1).getTextContent());			
		//						<a:Currency>SEK</a:Currency>
		String currency = o.getElementsByTagName("a:Currency").item(0).getTextContent();
		this.setCurrency(currency);
			
		// all individual customer fields

		//       				<a:Customer
		Element c = (Element) o.getElementsByTagName("a:Customer").item(0);
		//							xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
		//							<b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
		String cCoAddress = c.getElementsByTagName("b:CoAddress").item(0).getTextContent();
		//	  						<b:CompanyIdentity i:nil="true" />	// iff IndividualIdentity
		//    						<b:CompanyIdentity>
		String cCompanyIdentity = c.getElementsByTagName("b:CompanyIdentity").item(0).getTextContent();
		//       						<b:CompanyIdentification i:nil="true"/>
		// NOT IN USE
		//       						<b:CompanyVatNumber i:nil="true"/>
		String ciCompanyVatNumber = cCompanyIdentity.equals("") ? null : c.getElementsByTagName("b:CompanyVatNumber").item(0).getTextContent();
		//    						</b:CompanyIdentity>		
		//							<b:CountryCode>SE</b:CountryCode>
		String cCountryCode = c.getElementsByTagName("b:CountryCode").item(0).getTextContent();
		//							<b:CustomerType>Individual</b:CustomerType>
		String cCustomerType = c.getElementsByTagName("b:CustomerType").item(0).getTextContent();
		//							<b:Email>daniel@colourpicture.se</b:Email>
		String cEmail = c.getElementsByTagName("b:Email").item(0).getTextContent();
		//							<b:FullName>Persson, Tess T</b:FullName>
		String cFullName = c.getElementsByTagName("b:FullName").item(0).getTextContent();
		//							<b:HouseNumber i:nil="true" />
		String cHouseNumber = c.getElementsByTagName("b:HouseNumber").item(0).getTextContent().equals("") ? null : c.getElementsByTagName("b:HouseNumber").item(0).getTextContent();
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
		// NOT SUPPORTED
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
			CompanyCustomer companyCustomer = new CompanyCustomer();
			
			companyCustomer.setNationalIdNumber( cNationalIdNumber );
			companyCustomer.setEmail( cEmail );
			companyCustomer.setPhoneNumber( cPhoneNumber );
			//ipAddress;
			companyCustomer.setCompanyName( cFullName );
			companyCustomer.setStreetAddress( cStreet, cHouseNumber );
			companyCustomer.setCoAddress( cCoAddress );
			companyCustomer.setZipCode( cZipCode );
			companyCustomer.setLocality( cLocality );
			companyCustomer.setVatNumber(ciCompanyVatNumber);
			
			this.setCustomer(companyCustomer);
		}		
		//						<a:CustomerId>1000117</a:CustomerId>
		String customerId = o.getElementsByTagName("a:CustomerId").item(0).getTextContent();
		this.setCustomerId(customerId);
		//						<a:CustomerReference />
		String customerReference = o.getElementsByTagName("a:CustomerReference").item(0).getTextContent();
		this.setCustomerReference(customerReference.equals("") ? null : customerReference);	// for <a:CustomerReference /> getTextContent() returns ""
		//						<a:DeliveryAddress i:nil="true"
		//							xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		//not supported	
		//						<a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
		String isPossibleToAdminister = o.getElementsByTagName("a:IsPossibleToAdminister").item(0).getTextContent();	
		this.setIsPossibleToAdminister(isPossibleToAdminister.equals("true") ? true : false);
		//						<a:IsPossibleToCancel>true</a:IsPossibleToCancel>
		String isPossibleToCancel = o.getElementsByTagName("a:IsPossibleToCancel").item(0).getTextContent();	
		this.setIsPossibleToCancel(isPossibleToCancel.equals("true") ? true : false);
		//						<a:Notes i:nil="true" />
		String notes = o.getElementsByTagName("a:Notes").item(0).getTextContent();	
		this.setNotes(notes.equals("") ? null : notes); // for <a:Notes i:nil="true" /> getTextContent() returns ""
		//						<a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
		String cOrderDeliveryStatus = o.getElementsByTagName("a:OrderDeliveryStatus").item(0).getTextContent();	
		this.setOrderDeliveryStatus(cOrderDeliveryStatus);

		//						<a:OrderRows>
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		NodeList orderRows = o.getElementsByTagName("a:NumberedOrderRow");
		for( int i=0; i < orderRows.getLength(); i++ ) {
			//						<a:NumberedOrderRow>
			Element row = (Element) orderRows.item(i);	
			//							<ArticleNumber i:nil="true"
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			String rArticleNumber = row.getElementsByTagName("ArticleNumber").item(0).getTextContent();
			//							<Description
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Dyr produkt 25%</Description>
			String rDescription = row.getElementsByTagName("Description").item(0).getTextContent();
			//							<DiscountPercent
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			String rDiscountPercent = row.getElementsByTagName("DiscountPercent").item(0).getTextContent();
			//							<NumberOfUnits
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2.00</NumberOfUnits>
			String rNumberOfUnits = row.getElementsByTagName("NumberOfUnits").item(0).getTextContent();
			//							<PriceIncludingVat
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
			String rPriceIncludingVat = row.getElementsByTagName("PriceIncludingVat").item(0).getTextContent();
			//							<PricePerUnit
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2000.00</PricePerUnit>
			String rPricePerUnit = row.getElementsByTagName("PricePerUnit").item(0).getTextContent();
			//							<Unit
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			String rUnit = row.getElementsByTagName("Unit").item(0).getTextContent();
			//							<VatPercent
			//								xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			String rVatPercent = row.getElementsByTagName("VatPercent").item(0).getTextContent();
			//							<a:CreditInvoiceId i:nil="true" />
			String rCreditInvoiceId = row.getElementsByTagName("a:CreditInvoiceId").item(0).getTextContent();
			//							<a:InvoiceId i:nil="true" />
			String rInvoiceId = row.getElementsByTagName("a:InvoiceId").item(0).getTextContent();
			//							<a:RowNumber>1</a:RowNumber>
			String rRowNumber = row.getElementsByTagName("a:RowNumber").item(0).getTextContent();
			//							<a:Status>NotDelivered</a:Status>
			String rStatus = row.getElementsByTagName("a:Status").item(0).getTextContent();	
			//						</a:NumberedOrderRow>
			
			NumberedOrderRowBuilder numberedOrderRow = new NumberedOrderRowBuilder();
			numberedOrderRow.setArticleNumber(rArticleNumber.equals("") ? null : rArticleNumber);
			numberedOrderRow.setDescription(rDescription);
			numberedOrderRow.setDiscountPercent(Double.valueOf(rDiscountPercent));
			numberedOrderRow.setQuantity(Double.valueOf(rNumberOfUnits));
			if( rPriceIncludingVat.equals("true") ) {
				numberedOrderRow.setAmountIncVat(Double.valueOf(rPricePerUnit));
				numberedOrderRow.setAmountExVat( Double.NaN );
			}
			if( rPriceIncludingVat.equals("false") ) {
				numberedOrderRow.setAmountIncVat(Double.NaN);
				numberedOrderRow.setAmountExVat(Double.valueOf(rPricePerUnit));
			}
			numberedOrderRow.setUnit(rUnit.equals("") ? null : rUnit);
			numberedOrderRow.setVatPercent(Double.valueOf(rVatPercent));
			numberedOrderRow.setCreditInvoiceId(rCreditInvoiceId.equals("") ? null : rCreditInvoiceId);
			numberedOrderRow.setInvoiceId(rInvoiceId.equals("") ? null : rInvoiceId);
			numberedOrderRow.setRowNumber(Integer.valueOf(rRowNumber));
			try {
				numberedOrderRow.setStatus( OrderRowStatus.fromString(rStatus) );
			} catch (Exception e) {
				//ignore unknown status
			}
			
			numberedOrderRows.add(numberedOrderRow);
		}
		//						</a:OrderRows>
		this.setNumberedOrderRows( numberedOrderRows );
		//						<a:OrderStatus>Active</a:OrderStatus>
		String orderStatus = o.getElementsByTagName("a:OrderStatus").item(0).getTextContent();
		this.setOrderStatus(orderStatus);	
		//						<a:OrderType>Invoice</a:OrderType>
		String orderType = o.getElementsByTagName("a:OrderType").item(0).getTextContent();
		this.setOrderType(orderType);

		//						<a:PaymentPlanDetails i:nil="true" />		// iff invoice order
		//	                    <a:PaymentPlanDetails>
		Element ppd = (Element) o.getElementsByTagName("a:PaymentPlanDetails").item(0);		
		if( ppd.getChildNodes().getLength() > 0 ) {
			//	                <a:CampaignCode>213060</a:CampaignCode>
			// NOT SUPPORTED
			//	                <a:ContractLengthMonths>3</a:ContractLengthMonths>
			String ppdPaymentPlanDetailsContractLengthMonths = ppd.getElementsByTagName("a:ContractLengthMonths").item(0).getTextContent();
			this.setPaymentPlanDetailsContractLengthMonths(ppdPaymentPlanDetailsContractLengthMonths.equals("") ? null : ppdPaymentPlanDetailsContractLengthMonths);
			//                   <a:ContractNumber i:nil="true"/>
			String ppdPaymentPlanDetailsContractNumber = ppd.getElementsByTagName("a:ContractNumber").item(0).getTextContent();
			this.setPaymentPlanDetailsContractNumber(ppdPaymentPlanDetailsContractNumber.equals("") ? null : ppdPaymentPlanDetailsContractNumber);
		}
		//	                   </a:PaymentPlanDetails>
		
		//						<a:PendingReasons />
		Element pr = (Element) o.getElementsByTagName("a:PendingReasons").item(0);
		if( pr.getChildNodes().getLength() > 0 ) {
			String prPendingReasonsPendingType = pr.getElementsByTagName("a:PendingType").item(0).getTextContent();
			this.setPendingReasonsPendingType(prPendingReasonsPendingType.equals("") ? null : prPendingReasonsPendingType);
			String prPendingReasonsCreatedDate = pr.getElementsByTagName("a:CreatedDate").item(0).getTextContent();
			this.setPendingReasonsCreatedDate(prPendingReasonsCreatedDate.equals("") ? null : prPendingReasonsCreatedDate);
		}
		//						<a:SveaOrderId>348629</a:SveaOrderId>
		String orderId = o.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId(orderId);
		//						<a:SveaWillBuy>true</a:SveaWillBuy>
		String sveaWillBuy = o.getElementsByTagName("a:SveaWillBuy").item(0).getTextContent();	
		this.setSveaWillBuy(sveaWillBuy.equals("true") ? true : false); //	or (Boolean.valueOf(node.getChildNodes().item(0).getTextContent()));
		//					</a:Order>
		//				</a:Orders>
		//			</GetOrdersResult>
		//		</GetOrdersResponse>
		//	</s:Body>
		//</s:Envelope>
	}
}
