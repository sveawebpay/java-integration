package se.sveaekonomi.webpay.integration.adminservice;


import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class GetOrdersRequestIntegrationTest {     

    @Test
    public void test_attributest_existing_order() {

    	Long invoiceOrder = 348629L;	// existing order
    	
        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();

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
//			// TODO customer			
			assertEquals( "1000117", response.getCustomerId() );					
			assertEquals( null, response.getCustomerReference() );		// <a:CustomerReference /> =
//			// TODO deliveryaddress			
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
 
    @Test
    public void test_attributes_individual_customer() throws Exception {

    	Long invoiceOrder = 348629L;	// existing order
    	
        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;

    	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();

		assertTrue( response.isOrderAccepted() );     
		assertEquals( String.valueOf(invoiceOrder), response.getOrderId() );

		assertTrue( response.getCustomer() instanceof IndividualCustomer );
		assertEquals( "194605092222", response.getCustomer().getNationalIdNumber() );
		// email
		
    }
    // paymentplan
    // TODO
}
