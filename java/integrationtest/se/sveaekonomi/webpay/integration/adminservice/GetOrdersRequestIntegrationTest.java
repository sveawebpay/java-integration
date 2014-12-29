package se.sveaekonomi.webpay.integration.adminservice;


import static org.junit.Assert.*;

import javax.xml.soap.SOAPMessage;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class GetOrdersRequestIntegrationTest {

    @Test
    public void test_attributes_existing_order() {

    	Long invoiceOrder = 348629L;	// existing order
    	
        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersRequest request = queryOrderBuilder.queryInvoiceOrder();
        	
        	SOAPMessage tmp = request.prepareRequest();
        	        	
        	GetOrdersResponse response = request.doRequest();

			assertTrue( response.isOrderAccepted() );     
			assertEquals( String.valueOf(invoiceOrder), response.getOrderId() );

			//Response SOAP Message:
			//	<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
			//		<s:Body>
			//			<GetOrdersResponse xmlns="http://tempuri.org/">
			//				<GetOrdersResult
			//					xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service"
			//					xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
			//					<a:ErrorMessage i:nil="true" />
			//					<a:ResultCode>0</a:ResultCode>
			//					<a:Orders>
			//						<a:Order>
			//							<a:ChangedDate i:nil="true" />
			//							<a:ClientId>79021</a:ClientId>
			//							<a:ClientOrderId>449</a:ClientOrderId>
			//							<a:CreatedDate>2014-05-19T16:04:54.787</a:CreatedDate>
			//							<a:CreditReportStatus>
			//								<a:Accepted>true</a:Accepted>
			//								<a:CreationDate>2014-05-19T16:04:54.893</a:CreationDate>
			//							</a:CreditReportStatus>
			//							<a:Currency>SEK</a:Currency>
			//							<a:Customer
			//								xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//								<b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			//								<b:CompanyIdentity i:nil="true" />
			//								<b:CountryCode>SE</b:CountryCode>
			//								<b:CustomerType>Individual</b:CustomerType>
			//								<b:Email>jonatan@askas.se</b:Email>
			//								<b:FullName>Persson, Tess T</b:FullName>
			//								<b:HouseNumber i:nil="true" />
			//								<b:IndividualIdentity>
			//									<b:BirthDate i:nil="true" />
			//									<b:FirstName i:nil="true" />
			//									<b:Initials i:nil="true" />
			//									<b:LastName i:nil="true" />
			//								</b:IndividualIdentity>
			//								<b:Locality>Stan</b:Locality>
			//								<b:NationalIdNumber>194605092222</b:NationalIdNumber>
			//								<b:PhoneNumber />
			//								<b:PublicKey i:nil="true" />
			//								<b:Street>Testgatan 1</b:Street>
			//								<b:ZipCode>99999</b:ZipCode>
			//							</a:Customer>
			//							<a:CustomerId>1000117</a:CustomerId>
			//							<a:CustomerReference />
			//							<a:DeliveryAddress i:nil="true"
			//								xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//							<a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
			//							<a:IsPossibleToCancel>true</a:IsPossibleToCancel>
			//							<a:Notes i:nil="true" />
			//							<a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
			//							<a:OrderRows>
			//								<a:NumberedOrderRow>
			//									<ArticleNumber i:nil="true"
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<Description
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Dyr produkt 25%</Description>
			//									<DiscountPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			//									<NumberOfUnits
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2.00</NumberOfUnits>
			//									<PriceIncludingVat
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
			//									<PricePerUnit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">2000.00</PricePerUnit>
			//									<Unit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<VatPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			//									<a:CreditInvoiceId i:nil="true" />
			//									<a:InvoiceId i:nil="true" />
			//									<a:RowNumber>1</a:RowNumber>
			//									<a:Status>NotDelivered</a:Status>
			//								</a:NumberedOrderRow>
			//								<a:NumberedOrderRow>
			//									<ArticleNumber i:nil="true"
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<Description
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Testprodukt 1kr 25%</Description>
			//									<DiscountPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			//									<NumberOfUnits
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
			//									<PriceIncludingVat
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
			//									<PricePerUnit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</PricePerUnit>
			//									<Unit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<VatPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			//									<a:CreditInvoiceId i:nil="true" />
			//									<a:InvoiceId i:nil="true" />
			//									<a:RowNumber>2</a:RowNumber>
			//									<a:Status>NotDelivered</a:Status>
			//								</a:NumberedOrderRow>
			//								<a:NumberedOrderRow>
			//									<ArticleNumber i:nil="true"
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<Description
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Fastpris (Fast fraktpris)</Description>
			//									<DiscountPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			//									<NumberOfUnits
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
			//									<PriceIncludingVat
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
			//									<PricePerUnit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">4.00</PricePerUnit>
			//									<Unit i:nil="true"
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<VatPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			//									<a:CreditInvoiceId i:nil="true" />
			//									<a:InvoiceId i:nil="true" />
			//									<a:RowNumber>3</a:RowNumber>
			//									<a:Status>NotDelivered</a:Status>
			//								</a:NumberedOrderRow>
			//								<a:NumberedOrderRow>
			//									<ArticleNumber
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<Description
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">Svea Fakturaavgift:: 20.00kr (SE)</Description>
			//									<DiscountPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			//									<NumberOfUnits
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
			//									<PriceIncludingVat
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">false</PriceIncludingVat>
			//									<PricePerUnit
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">20.00</PricePerUnit>
			//									<Unit i:nil="true"
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice" />
			//									<VatPercent
			//										xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</VatPercent>
			//									<a:CreditInvoiceId i:nil="true" />
			//									<a:InvoiceId i:nil="true" />
			//									<a:RowNumber>4</a:RowNumber>
			//									<a:Status>NotDelivered</a:Status>
			//								</a:NumberedOrderRow>
			//							</a:OrderRows>
			//							<a:OrderStatus>Active</a:OrderStatus>
			//							<a:OrderType>Invoice</a:OrderType>
			//							<a:PaymentPlanDetails i:nil="true" />
			//							<a:PendingReasons />
			//							<a:SveaOrderId>348629</a:SveaOrderId>
			//							<a:SveaWillBuy>true</a:SveaWillBuy>
			//						</a:Order>
			//					</a:Orders>
			//				</GetOrdersResult>
			//			</GetOrdersResponse>
			//		</s:Body>
			//	</s:Envelope>
			
			assertEquals( null, response.getChangedDate() );
			assertEquals( "79021", response.getClientId() );
			assertEquals( "449", response.getClientOrderId() );
			assertEquals( "2014-05-19T16:04:54.787", response.getCreatedDate() );			
			assertEquals( true, response.getCreditReportStatusAccepted() );
			assertEquals( "2014-05-19T16:04:54.893", response.getCreditReportStatusCreationDate() );		
			assertEquals( "SEK", response.getCurrency() );
			
			//<a:Customer
			//	xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//	<b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			//	<b:CompanyIdentity i:nil="true" />
			//	<b:CountryCode>SE</b:CountryCode>
			//	<b:CustomerType>Individual</b:CustomerType>
			//	<b:Email>jonatan@askas.se</b:Email>
			//	<b:FullName>Persson, Tess T</b:FullName>
			//	<b:HouseNumber i:nil="true" />
			//	<b:IndividualIdentity>
			//		<b:BirthDate i:nil="true" />
			//		<b:FirstName i:nil="true" />
			//		<b:Initials i:nil="true" />
			//		<b:LastName i:nil="true" />
			//	</b:IndividualIdentity>
			//	<b:Locality>Stan</b:Locality>
			//	<b:NationalIdNumber>194605092222</b:NationalIdNumber>
			//	<b:PhoneNumber />
			//	<b:PublicKey i:nil="true" />
			//	<b:Street>Testgatan 1</b:Street>
			//	<b:ZipCode>99999</b:ZipCode>
			//</a:Customer>			

			assertTrue( response.getIndividualCustomer() instanceof IndividualCustomer );
			
			assertEquals( "194605092222", response.getIndividualCustomer().getNationalIdNumber());			
			assertEquals( null, response.getIndividualCustomer().getInitials());			
			assertEquals( null, response.getIndividualCustomer().getBirthDate());			
			assertEquals( null, response.getIndividualCustomer().getFirstName());			
			assertEquals( null, response.getIndividualCustomer().getLastName());			
//			//assertEquals( null, response.getIndividualCustomer().getEmail());  // -- returns current customer stats, may change 			
//			//assertEquals( "08 - 111 111 11", response.getIndividualCustomer().getPhoneNumber());	// -- returns current customer stats, may change	
			assertEquals( "Persson, Tess T", response.getIndividualCustomer().getName());			
			assertEquals( "Testgatan 1", response.getIndividualCustomer().getStreetAddress());			
			assertEquals( "c/o Eriksson, Erik", response.getIndividualCustomer().getCoAddress());			
			assertEquals( "99999", response.getIndividualCustomer().getZipCode());			
			assertEquals( "Stan", response.getIndividualCustomer().getLocality());			
			
			assertEquals( "1000117", response.getCustomerId() );
			assertEquals( null, response.getCustomerReference() );		// <a:CustomerReference /> =
			// deliveryaddress -- not supported			
			assertEquals( false, response.getIsPossibleToAdminister() );
			assertEquals( true, response.getIsPossibleToCancel() );
			assertEquals( null, response.getNotes() );					// <a:Notes i:nil="true" />
			assertEquals( "Created", response.getOrderDeliveryStatus() );
//			// TODO orderrows	
			assertEquals( "Active", response.getOrderStatus() );
			assertEquals( "Invoice", response.getOrderType() );
//			// TODO paymentplandetails
//			// TODO pendingreasons
			assertEquals( "348629", response.getOrderId() );
			assertEquals( true, response.getSveaWillBuy() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    } 

    @Test
    public void test_SE_IndividualCustomer_single_row() {
    	
    	Long invoiceOrder = 504352L;	// existing order    	
		//<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="https://webservices.sveaekonomi.se/webpay">
		//   <soapenv:Header/>
		//   <soapenv:Body>
		//      <web:CreateOrderEu>
		//         <web:request>
		//            <web:Auth>
		//               <web:ClientNumber>79021</web:ClientNumber>
		//               <web:Username>sverigetest</web:Username>
		//               <web:Password>sverigetest</web:Password>
		//            </web:Auth>
		//            <web:CreateOrderInformation>
		//               <web:ClientOrderNumber>743</web:ClientOrderNumber>
		//               <web:OrderRows>
		//                  <web:OrderRow>
		//                     <web:Description>AO</web:Description>
		//                     <web:PricePerUnit>125</web:PricePerUnit>
		//                     <web:NumberOfUnits>1</web:NumberOfUnits>
		//                     <web:PriceIncludingVat>true</web:PriceIncludingVat>
		//                     <web:VatPercent>25</web:VatPercent>
		//                     <web:DiscountPercent>0</web:DiscountPercent>
		//                  </web:OrderRow>
		//               </web:OrderRows>
		//               <web:CustomerIdentity>
		//                  <web:NationalIdNumber>4605092222</web:NationalIdNumber>
		//                  <web:CountryCode>SE</web:CountryCode>
		//                  <web:CustomerType>Individual</web:CustomerType>
		//               </web:CustomerIdentity>
		//               <web:OrderDate>2014-07-22T10:07:18+02:00</web:OrderDate>
		//               <web:OrderType>Invoice</web:OrderType>
		//            </web:CreateOrderInformation>
		//         </web:request>
		//      </web:CreateOrderEu>
		//   </soapenv:Body>
		//</soapenv:Envelope>

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertTrue( response.isOrderAccepted() );     
			//<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
			//   <s:Body>
			//      <GetOrdersResponse xmlns="http://tempuri.org/">
			//         <GetOrdersResult xmlns:a="http://schemas.datacontract.org/2004/07/DataObjects.Admin.Service" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
			//            <a:ErrorMessage i:nil="true"/>
			//            <a:ResultCode>0</a:ResultCode>
			//            <a:Orders>
			//               <a:Order>
			//                  <a:ChangedDate i:nil="true"/>
			assertEquals( null, response.getChangedDate() );
			//                  <a:ClientId>79021</a:ClientId>
			assertEquals( "79021", response.getClientId() );
			//                  <a:ClientOrderId>743</a:ClientOrderId>
			assertEquals( "743", response.getClientOrderId() );
			//                  <a:CreatedDate>2015-01-12T10:35:34.027</a:CreatedDate>
			assertEquals( "2015-01-12T10:35:34.027", response.getCreatedDate() );			
			//                  <a:CreditReportStatus>
			//                     <a:Accepted>true</a:Accepted>
			assertEquals( true, response.getCreditReportStatusAccepted() );
			//                     <a:CreationDate>2015-01-12T10:35:34.09</a:CreationDate>
			assertEquals( "2015-01-12T10:35:34.09", response.getCreditReportStatusCreationDate() );					
			//                  </a:CreditReportStatus>
			//                  <a:Currency>SEK</a:Currency>
			assertEquals( "SEK", response.getCurrency() );
			//                  <a:Customer xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//                     <b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			assertEquals( "c/o Eriksson, Erik", response.getIndividualCustomer().getCoAddress());			
			//                     <b:CompanyIdentity i:nil="true"/>
			//                     <b:CountryCode>SE</b:CountryCode>
//			// NOT SUPPORTED countryCode
			//                     <b:CustomerType>Individual</b:CustomerType>
			assertTrue( response.getIndividualCustomer() instanceof IndividualCustomer );
			//                     <b:Email>ekonomi@madepeople.se</b:Email>
//			//assertEquals( null, response.getIndividualCustomer().getEmail());  // -- returns current customer stats, may change in test			
			//                     <b:FullName>Persson, Tess T</b:FullName>
			assertEquals( "Persson, Tess T", response.getIndividualCustomer().getName());			
			//                     <b:HouseNumber i:nil="true"/>
			//                     <b:IndividualIdentity>
			//                        <b:BirthDate i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getBirthDate());			
			//                        <b:FirstName i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getFirstName());			
			//                        <b:Initials i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getInitials());			
			//                        <b:LastName i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getLastName());				
			//                     </b:IndividualIdentity>
			//                     <b:Locality>Stan</b:Locality>
			assertEquals( "Stan", response.getIndividualCustomer().getLocality());	
			//                     <b:NationalIdNumber>194605092222</b:NationalIdNumber>
			assertEquals( "194605092222", response.getIndividualCustomer().getNationalIdNumber());			
			//                     <b:PhoneNumber>08 - 111 111 11</b:PhoneNumber>
//			assertEquals( "08 - 111 111 11", response.getIndividualCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test	
			//                     <b:PublicKey i:nil="true"/>
//			// NOT SUPPORTED publicKey
			//                     <b:Street>Testgatan 1</b:Street>
			assertEquals( "Testgatan 1", response.getIndividualCustomer().getStreetAddress());			
			//                     <b:ZipCode>99999</b:ZipCode>
			assertEquals( "99999", response.getIndividualCustomer().getZipCode());			
			//                  </a:Customer>
			//                  <a:CustomerId>1000117</a:CustomerId>
			assertEquals( "1000117", response.getCustomerId() );
			//                  <a:CustomerReference/>
			assertEquals( null, response.getCustomerReference() );
			//                  <a:DeliveryAddress i:nil="true" xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
//			// NOT SUPPORTED deliveryAddress
			//                  <a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
			assertEquals( false, response.getIsPossibleToAdminister() );
			//                  <a:IsPossibleToCancel>true</a:IsPossibleToCancel>
			assertEquals( true, response.getIsPossibleToCancel() );
			//                  <a:Notes i:nil="true"/>
			assertEquals( null, response.getNotes() );					// <a:Notes i:nil="true" />
			//                  <a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
			assertEquals( "Created", response.getOrderDeliveryStatus() );
			//                  <a:OrderRows>
//			// TODO orderrows	
			//                     <a:NumberedOrderRow>
			//                        <ArticleNumber i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			//                        <Description xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">AO</Description>
			//                        <DiscountPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			//                        <NumberOfUnits xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
			//                        <PriceIncludingVat xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">true</PriceIncludingVat>
			//                        <PricePerUnit xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">125.00</PricePerUnit>
			//                        <Unit i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			//                        <VatPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			//                        <a:CreditInvoiceId i:nil="true"/>
			//                        <a:InvoiceId i:nil="true"/>
			//                        <a:RowNumber>1</a:RowNumber>
			//                        <a:Status>NotDelivered</a:Status>
			//                     </a:NumberedOrderRow>
			//                  </a:OrderRows>
			//                  <a:OrderStatus>Active</a:OrderStatus>
			assertEquals( "Active", response.getOrderStatus() );
			//                  <a:OrderType>Invoice</a:OrderType>
			assertEquals( "Invoice", response.getOrderType() );
			//                  <a:PaymentPlanDetails i:nil="true"/>
//			// TODO paymentplandetails
			//                  <a:PendingReasons/>
//			// TODO pendingreasons
			//                  <a:SveaOrderId>504352</a:SveaOrderId>
			assertEquals( "504352", response.getOrderId() );
			//                  <a:SveaWillBuy>true</a:SveaWillBuy>
			assertEquals( true, response.getSveaWillBuy() );
			//               </a:Order>
			//            </a:Orders>
			//         </GetOrdersResult>
			//      </GetOrdersResponse>
			//   </s:Body>
			//</s:Envelope>			
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }     
    
    
    
    
    
    // paymentplan
    // TODO
}
