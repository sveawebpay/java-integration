package se.sveaekonomi.webpay.integration.response.adminservice;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.ORDERDELIVERYSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PENDINGTYPE;

public class GetOrdersResponse extends AdminServiceResponse {
																							// see AS/Order structure:
    /** Order number from client's ordersystem */
    public String clientOrderId;															// ClientOrderId	
    /** Unique Id for the created order. Used for any further order webservice requests. */
    public Long orderId;																	// SveaOrderId		
    public ORDERDELIVERYSTATUS orderDeliveryStatus;											// OrderDeliveryStatus
    public ORDERTYPE orderType; 															// OrderType
    public ORDERSTATUS orderStatus;															// OrderStatus    
    /** Date when order was first created. */
    public String createdDate;																// CreatedDate    
    /** @var CompanyCustomer|IndividualCustomer $customer -- customer identity as associated with the order by Svea, also Shipping address. */
    public CustomerIdentity<?> customer;													// Customer
    /** Customer id that is created by SveaWebPay system. */
    public Long customerId;    																// CustomerId
    /** Customer Reference. (Gets printed on the invoice.)*/
    public String customerReference;    													// CustomerReference
	/** Date when order status was changed, e.g when order was delivered, or null if not set.*/
    public String changeDate;																// ChangeDate
    /** Country currency */																	
    public String currency;																	// Currency
    /** Describes whether SveaWebPay will buy the order or just administrate it */
    public Boolean sveaWillBuy;																// SveaWillBuy
    /** Text on order created by client */
    public String notes;																	// Notes
    /** Id that identifies a client in sveawebpay system */
    public Long clientId;																	// ClientId
    /** Contract length or null for invoice orders */
    public Integer paymentPlanDetailsContractLengthMonths;									// PaymentPlanDetails.ContractLengthMonths
    /** Contract number of a specific contract or null for invoice orders */
    public Long paymentPlanDetailsContractNumber;											// PaymentPlanDetails.ContractNumber
    public Boolean isPossibleToAdminister;													// IsPossibleToAdminister
    /** Date of order credit decision. */
    public String creditReportStatusCreationDate;											// CreditReportStatus.CreationDate
    /** Tells if credit decision is accepted or not */
    public Boolean creditReportStatusAccepted;												// CreditReportStatus.Accepted
    /** @var Svea\OrderRow[] $numberedOrderRows  array of OrderRow objects, note that invoice and payment plan order rows name attribute will be null */
    public ArrayList<NumberedOrderRowBuilder> numberedOrderRows;							// OrderRows
    /** Tells if order can be cancelled or not */
    public Boolean isPossibleToCancel;														// IsPossibleToCancel
    public PENDINGTYPE pendingReasonsPendingType; 											// PendingReasons.PendingType
    public String pendingReasonsCreatedDate;												// PendingReasons.CreatedDate

	public String getClientOrderId() {
		return clientOrderId;
	}
	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}
    public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public ORDERDELIVERYSTATUS getOrderDeliveryStatus() {
		return orderDeliveryStatus;
	}
	public void setOrderDeliveryStatus(ORDERDELIVERYSTATUS orderDeliveryStatus) {
		this.orderDeliveryStatus = orderDeliveryStatus;
	}    
	public ORDERTYPE getOrderType() {
		return orderType;
	}
	public void setOrderType(ORDERTYPE orderType) {
		this.orderType = orderType;
	}
	public ORDERSTATUS getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(ORDERSTATUS orderStatus) {
		this.orderStatus = orderStatus;
	}	
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}	
	private CustomerIdentity getCustomer() {
		return customer;
	}	
	public void setCustomer( CustomerIdentity customer ) {
		this.customer = customer;
	}	
	public IndividualCustomer getIndividualCustomer() {
		return (IndividualCustomer) getCustomer();
	}
	public CompanyCustomer getCompanyCustomer() {
		return (CompanyCustomer) getCustomer();
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getCustomerReference() {
		return customerReference;
	}
	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}
    public String getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}	
	public Boolean getSveaWillBuy() {
		return sveaWillBuy;
	}
	public void setSveaWillBuy(Boolean sveaWillBuy) {
		this.sveaWillBuy = sveaWillBuy;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	/** null for invoice orders */
	public Integer getPaymentPlanDetailsContractLengthMonths() {
		return paymentPlanDetailsContractLengthMonths;
	}
	public void setPaymentPlanDetailsContractLengthMonths( Integer paymentPlanDetailsContractLengthMonths) {
		this.paymentPlanDetailsContractLengthMonths = paymentPlanDetailsContractLengthMonths;
	}
	/** null for invoice orders */
	public Long getPaymentPlanDetailsContractNumber() {
		return paymentPlanDetailsContractNumber;
	}
	public void setPaymentPlanDetailsContractNumber( Long paymentPlanDetailsContractNumber) {
		this.paymentPlanDetailsContractNumber = paymentPlanDetailsContractNumber;
	}
	public Boolean getIsPossibleToAdminister() {
		return isPossibleToAdminister;
	}
	public void setIsPossibleToAdminister(Boolean isPossibleToAdminister) {
		this.isPossibleToAdminister = isPossibleToAdminister;
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
	public void setCreditReportStatusCreationDate(String creditReportStatusCreationDate) {
		this.creditReportStatusCreationDate = creditReportStatusCreationDate;
	}
	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return this.numberedOrderRows;
	}	
	public void setNumberedOrderRows( ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows = numberedOrderRows;
	}
	public Boolean getIsPossibleToCancel() {
		return isPossibleToCancel;
	}
	public void setIsPossibleToCancel(Boolean isPossibleToCancel) {
		this.isPossibleToCancel = isPossibleToCancel;
	}
	public PENDINGTYPE getPendingReasonsPendingType() {
		return pendingReasonsPendingType;
	}
	public void setPendingReasonsPendingType(PENDINGTYPE pendingReasonsPendingType) {
		this.pendingReasonsPendingType = pendingReasonsPendingType;
	}
	public String getPendingReasonsCreatedDate() {
		return pendingReasonsCreatedDate;
	}
	public void setPendingReasonsCreatedDate(String pendingReasonsCreatedDate) {
		this.pendingReasonsCreatedDate = pendingReasonsCreatedDate;
	}


    public GetOrdersResponse(NodeList xmlResponse) {		
    	super( xmlResponse ); // handle ErrorMessage, ResultCode
    	if( this.isOrderAccepted() ) {
    		setGetOrdersResponseAttributes(xmlResponse);
    	}
    }
    
	private void setGetOrdersResponseAttributes(NodeList xmlResponse) {
    	Node getOrdersResponse=xmlResponse.item(0);
		Node getOrdersResult=xmlResponse.item(1);
		Node orders = getOrdersResult.getChildNodes().item(2);
		Element o = (Element) orders.getChildNodes().item(0);
		String changedDate = o.getElementsByTagName("a:ChangedDate").item(0).getTextContent(); // getTextContent() of <a:ChangedDate i:nil="true" /> is ""
		this.setChangeDate( changedDate.equals("") ? null : changedDate );
		String clientId = o.getElementsByTagName("a:ClientId").item(0).getTextContent();
		this.setClientId( clientId.equals("") ? null : Long.valueOf(clientId) );
		String clientOrderId = o.getElementsByTagName("a:ClientOrderId").item(0).getTextContent();
		this.setClientOrderId( clientOrderId) ;
		String createdDate = o.getElementsByTagName("a:CreatedDate").item(0).getTextContent();
		this.setCreatedDate(createdDate);
		Node creditReportStatus = o.getElementsByTagName("a:CreditReportStatus").item(0);
		Boolean creditReportStatusAccepted = Boolean.parseBoolean((creditReportStatus.getChildNodes().item(0).getTextContent())); 
		this.setCreditReportStatusAccepted(creditReportStatusAccepted);		
		this.setCreditReportStatusCreationDate(creditReportStatus.getChildNodes().item(1).getTextContent());			
		String currency = o.getElementsByTagName("a:Currency").item(0).getTextContent();
		this.setCurrency(currency);		
		
		Element c = (Element) o.getElementsByTagName("a:Customer").item(0);
		String cCoAddress = c.getElementsByTagName("b:CoAddress").item(0).getTextContent();
		String cCompanyIdentity = c.getElementsByTagName("b:CompanyIdentity").item(0).getTextContent();
		// NOT IN USE					<b:CompanyIdentification i:nil="true"/>
		String ciCompanyVatNumber = cCompanyIdentity.equals("") ? null : c.getElementsByTagName("b:CompanyVatNumber").item(0).getTextContent();
		String cCountryCode = c.getElementsByTagName("b:CountryCode").item(0).getTextContent();
		String cCustomerType = c.getElementsByTagName("b:CustomerType").item(0).getTextContent();
		String cEmail = c.getElementsByTagName("b:Email").item(0).getTextContent();
		String cFullName = c.getElementsByTagName("b:FullName").item(0).getTextContent();
		String cHouseNumber = c.getElementsByTagName("b:HouseNumber").item(0).getTextContent().equals("") ? null : c.getElementsByTagName("b:HouseNumber").item(0).getTextContent();
		String cIndividualIdentity = c.getElementsByTagName("b:IndividualIdentity").item(0).getTextContent();
		String iiBirthDate = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:BirthDate").item(0).getTextContent();
		String iiFirstName = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:FirstName").item(0).getTextContent();
		String iiInitials = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:Initials").item(0).getTextContent();
		String iiLastName = cIndividualIdentity.equals("") ? null : c.getElementsByTagName("b:LastName").item(0).getTextContent();
		String cLocality = c.getElementsByTagName("b:Locality").item(0).getTextContent();
		String cNationalIdNumber = c.getElementsByTagName("b:NationalIdNumber").item(0).getTextContent();
		String cPhoneNumber = c.getElementsByTagName("b:PhoneNumber").item(0).getTextContent();
		// NOT SUPPORTED			<b:PublicKey i:nil="true" />
		String cStreet = c.getElementsByTagName("b:Street").item(0).getTextContent();
		String cZipCode = c.getElementsByTagName("b:ZipCode").item(0).getTextContent();

		if( cCustomerType.endsWith("Individual") ) {
			
			//<a:Customer xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//    <b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			//    <b:CompanyIdentity i:nil="true"/>
			//    <b:CountryCode>SE</b:CountryCode>
			//    <b:CustomerType>Individual</b:CustomerType>
			//    <b:Email i:nil="true"/>
			//    <b:FullName>Persson, Tess T</b:FullName>
			//    <b:HouseNumber i:nil="true"/>
			//    <b:IndividualIdentity>
			//       <b:BirthDate i:nil="true"/>
			//       <b:FirstName i:nil="true"/>
			//       <b:Initials i:nil="true"/>
			//       <b:LastName i:nil="true"/>
			//    </b:IndividualIdentity>
			//    <b:Locality>Stan</b:Locality>
			//    <b:NationalIdNumber>194605092222</b:NationalIdNumber>
			//    <b:PhoneNumber i:nil="true"/>
			//    <b:PublicKey i:nil="true"/>
			//    <b:Street>Testgatan 1</b:Street>
			//    <b:ZipCode>99999</b:ZipCode>
			// </a:Customer>

			//  //ci
			//  private String phoneNumber;
			//  private String email;
			//  private String ipAddress;
			//  private String coAddress;
			//  private String streetAddress;
			//  private String housenumber;
			//  private String zipCode;
			//  private String locality;    	
			//  //ic
			//  private String ssn;
			//  private String birthDate;
			//  private String firstName;
			//  private String lastName;
			//  private String initials;
			//  private String name;
			
			IndividualCustomer individualCustomer = new IndividualCustomer();
		
			individualCustomer.setPhoneNumber( cPhoneNumber );
			individualCustomer.setEmail( cEmail );
			individualCustomer.setIpAddress( null );
			individualCustomer.setCoAddress( cCoAddress );
			individualCustomer.setStreetAddress( cStreet, cHouseNumber );
			individualCustomer.setZipCode( cZipCode );
			individualCustomer.setLocality( cLocality );
			individualCustomer.setNationalIdNumber( cNationalIdNumber );
			individualCustomer.setName( iiFirstName, iiLastName ); // FirstName, LastName
			individualCustomer.setBirthDate( iiBirthDate );
			individualCustomer.setInitials( iiInitials );
			individualCustomer.setName( cFullName ); // FullName

			this.setCustomer(individualCustomer);
		}
		
		if( cCustomerType.endsWith("Company") ) {
			
			//<a:Customer xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//    <b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			//    <b:CompanyIdentity>
			//       <b:CompanyIdentification i:nil="true"/>
			//       <b:CompanyVatNumber i:nil="true"/>
			//    </b:CompanyIdentity>
			//    <b:CountryCode>SE</b:CountryCode>
			//    <b:CustomerType>Company</b:CustomerType>
			//    <b:Email/>
			//    <b:FullName>Persson, Tess T</b:FullName>
			//    <b:HouseNumber i:nil="true"/>
			//    <b:IndividualIdentity i:nil="true"/>
			//    <b:Locality>Stan</b:Locality>
			//    <b:NationalIdNumber>164608142222</b:NationalIdNumber>
			//    <b:PhoneNumber/>
			//    <b:PublicKey i:nil="true"/>
			//    <b:Street>Testgatan 1</b:Street>
			//    <b:ZipCode>99999</b:ZipCode>
			// </a:Customer>
			
			//  //ci
			//  private String phoneNumber;
			//  private String email;
			//  private String ipAddress;
			//  private String coAddress;
			//  private String streetAddress;
			//  private String housenumber;
			//  private String zipCode;
			//  private String locality;    		
			//  //cc
			//  private String companyName;
			//  private String orgNumber;
			//  private String companyVatNumber;
			//  private String addressSelector;    	
			
			CompanyCustomer companyCustomer = new CompanyCustomer();
			
			companyCustomer.setPhoneNumber( cPhoneNumber );
			companyCustomer.setEmail( cEmail );
			companyCustomer.setIpAddress( null );
			companyCustomer.setCoAddress( cCoAddress );
			companyCustomer.setStreetAddress( cStreet, cHouseNumber );
			companyCustomer.setZipCode( cZipCode );
			companyCustomer.setLocality( cLocality );
			companyCustomer.setCompanyName( cFullName );
			companyCustomer.setNationalIdNumber( cNationalIdNumber );
			companyCustomer.setVatNumber(ciCompanyVatNumber);
			companyCustomer.setAddressSelector( null );
			
			
			this.setCustomer(companyCustomer);
		}		
		String customerId = o.getElementsByTagName("a:CustomerId").item(0).getTextContent();
		this.setCustomerId( Long.valueOf(customerId) );
		String customerReference = o.getElementsByTagName("a:CustomerReference").item(0).getTextContent();
		this.setCustomerReference(customerReference.equals("") ? null : customerReference);	
		//NOT SUPPORTED			<a:DeliveryAddress i:nil="true" xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
		String isPossibleToAdminister = o.getElementsByTagName("a:IsPossibleToAdminister").item(0).getTextContent();	
		this.setIsPossibleToAdminister(isPossibleToAdminister.equals("true") ? true : false);
		String isPossibleToCancel = o.getElementsByTagName("a:IsPossibleToCancel").item(0).getTextContent();	
		this.setIsPossibleToCancel(isPossibleToCancel.equals("true") ? true : false);
		String notes = o.getElementsByTagName("a:Notes").item(0).getTextContent();	
		this.setNotes(notes.equals("") ? null : notes); // for <a:Notes i:nil="true" /> getTextContent() returns ""
		String cOrderDeliveryStatus = o.getElementsByTagName("a:OrderDeliveryStatus").item(0).getTextContent();	
		this.setOrderDeliveryStatus(ORDERDELIVERYSTATUS.fromString(cOrderDeliveryStatus));

		// all order rows
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		NodeList orderRows = o.getElementsByTagName("a:NumberedOrderRow");
		for( int i=0; i < orderRows.getLength(); i++ ) {
			Element row = (Element) orderRows.item(i);	
			String rArticleNumber = row.getElementsByTagName("ArticleNumber").item(0).getTextContent();
			String rDescription = row.getElementsByTagName("Description").item(0).getTextContent();
			String rDiscountPercent = row.getElementsByTagName("DiscountPercent").item(0).getTextContent();
			String rNumberOfUnits = row.getElementsByTagName("NumberOfUnits").item(0).getTextContent();
			String rPriceIncludingVat = row.getElementsByTagName("PriceIncludingVat").item(0).getTextContent();
			String rPricePerUnit = row.getElementsByTagName("PricePerUnit").item(0).getTextContent();
			String rUnit = row.getElementsByTagName("Unit").item(0).getTextContent();
			String rVatPercent = row.getElementsByTagName("VatPercent").item(0).getTextContent();
			String rCreditInvoiceId = row.getElementsByTagName("a:CreditInvoiceId").item(0).getTextContent();
			String rInvoiceId = row.getElementsByTagName("a:InvoiceId").item(0).getTextContent();
			String rRowNumber = row.getElementsByTagName("a:RowNumber").item(0).getTextContent();
			String rStatus = row.getElementsByTagName("a:Status").item(0).getTextContent();	
			
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
			numberedOrderRow.setCreditInvoiceId(rCreditInvoiceId.equals("") ? null : Long.valueOf(rCreditInvoiceId));
			numberedOrderRow.setInvoiceId(rInvoiceId.equals("") ? null : Long.valueOf(rInvoiceId));
			numberedOrderRow.setRowNumber(Integer.valueOf(rRowNumber));
			try {
				numberedOrderRow.setStatus( ORDERROWSTATUS.fromString(rStatus) );
			} catch (Exception e) {
				//ignore unknown status
			}
			
			numberedOrderRows.add(numberedOrderRow);
		}
		this.setNumberedOrderRows( numberedOrderRows );
		String orderStatus = o.getElementsByTagName("a:OrderStatus").item(0).getTextContent();
		this.setOrderStatus(ORDERSTATUS.fromString(orderStatus));	
		String orderType = o.getElementsByTagName("a:OrderType").item(0).getTextContent();
		this.setOrderType( ORDERTYPE.fromString(orderType) );
		Element ppd = (Element) o.getElementsByTagName("a:PaymentPlanDetails").item(0);		
		if( ppd.getChildNodes().getLength() > 0 ) {
			// NOT SUPPORTED     <a:CampaignCode>213060</a:CampaignCode>
			String ppdPaymentPlanDetailsContractLengthMonths = ppd.getElementsByTagName("a:ContractLengthMonths").item(0).getTextContent();
			this.setPaymentPlanDetailsContractLengthMonths(ppdPaymentPlanDetailsContractLengthMonths.equals("") ? null : Integer.valueOf(ppdPaymentPlanDetailsContractLengthMonths));
			String ppdPaymentPlanDetailsContractNumber = ppd.getElementsByTagName("a:ContractNumber").item(0).getTextContent();
			this.setPaymentPlanDetailsContractNumber(ppdPaymentPlanDetailsContractNumber.equals("") ? null : Long.valueOf(ppdPaymentPlanDetailsContractNumber));
		}
		Element pr = (Element) o.getElementsByTagName("a:PendingReasons").item(0);
		if( pr.getChildNodes().getLength() > 0 ) {
			String prPendingReasonsPendingType = pr.getElementsByTagName("a:PendingType").item(0).getTextContent();
			this.setPendingReasonsPendingType(prPendingReasonsPendingType.equals("") ? null : PENDINGTYPE.fromString(prPendingReasonsPendingType));
			String prPendingReasonsCreatedDate = pr.getElementsByTagName("a:CreatedDate").item(0).getTextContent();
			this.setPendingReasonsCreatedDate(prPendingReasonsCreatedDate.equals("") ? null : prPendingReasonsCreatedDate);
		}
		String orderId = o.getElementsByTagName("a:SveaOrderId").item(0).getTextContent();
		this.setOrderId( Long.valueOf(orderId) );
		String sveaWillBuy = o.getElementsByTagName("a:SveaWillBuy").item(0).getTextContent();	
		this.setSveaWillBuy(sveaWillBuy.equals("true") ? true : false);
	}
}
