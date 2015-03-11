package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERDELIVERYSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERSTATUS;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PENDINGTYPE;

public class GetOrdersRequestIntegrationTest {

    // test querying positive query response (order found)
    @Test
    public void test_queried_order_found() {
    	
    	Long invoiceOrder = 504352L;	// existing order    	
        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertTrue( response.isOrderAccepted() );   
			assertEquals( "", response.getErrorMessage() );
			assertEquals( "0", response.getResultCode());
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }	
	
	// test querying negative query response (order not found)
    @Test
    public void test_queried_order_not_found() {
    	
    	Long invoiceOrder = 99999L;	// non-existant order    	

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertFalse( response.isOrderAccepted() );   
			assertEquals( "Order does not exist.", response.getErrorMessage() );
			assertEquals( "20004", response.getResultCode());
			
			assertEquals( null, response.getChangeDate() );
			assertEquals( null, response.getClientId() );
			assertEquals( null, response.getClientOrderId() );
			assertEquals( null, response.getCreatedDate() );			
			assertEquals( null, response.getCreditReportStatusAccepted() );
			assertEquals( null, response.getCreditReportStatusCreationDate() );					
			assertEquals( null, response.getCurrency() );
			assertEquals( null, response.getIndividualCustomer() );	
			assertEquals( null, response.getCustomerId() );
			assertEquals( null, response.getCustomerReference() );
			assertEquals( null, response.getIsPossibleToAdminister() );
			assertEquals( null, response.getIsPossibleToCancel() );
			assertEquals( null, response.getNotes() );
			assertEquals( null, response.getOrderDeliveryStatus() );
			assertEquals( null, response.getNumberedOrderRows() );
			assertEquals( null, response.getOrderStatus() );
			assertEquals( null, response.getOrderType() );
			assertEquals( null, response.getPaymentPlanDetailsContractLengthMonths() );
			assertEquals( null, response.getPaymentPlanDetailsContractNumber() );
			assertEquals( null, response.getPendingReasonsCreatedDate() );
			assertEquals( null, response.getPendingReasonsPendingType() );
			assertEquals( null, response.getOrderId() );
			assertEquals( null, response.getSveaWillBuy() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }          	
	
	// invoice, individual customer, one-many orderrows, price inc. vat
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
			assertEquals( null, response.getChangeDate() );
			//                  <a:ClientId>79021</a:ClientId>
			assertEquals( (Long)79021L, response.getClientId() );
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
			// NOT SUPPORTED countryCode
			//                     <b:CustomerType>Individual</b:CustomerType>
			assertTrue( response.getIndividualCustomer() instanceof IndividualCustomer );
			//                     <b:Email>ekonomi@madepeople.se</b:Email>
			//assertEquals( null, response.getIndividualCustomer().getEmail());  // -- returns current customer stats, may change in test			
			//                     <b:FullName>Persson, Tess T</b:FullName>
			assertEquals( "Persson, Tess T", response.getIndividualCustomer().getName());			
			//                     <b:HouseNumber i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getHouseNumber());	
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
			//assertEquals( "08 - 111 111 11", response.getIndividualCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test	
			//                     <b:PublicKey i:nil="true"/>
			// NOT SUPPORTED publicKey
			//                     <b:Street>Testgatan 1</b:Street>
			assertEquals( "Testgatan 1", response.getIndividualCustomer().getStreetAddress());			
			//                     <b:ZipCode>99999</b:ZipCode>
			assertEquals( "99999", response.getIndividualCustomer().getZipCode());			
			//                  </a:Customer>
			//                  <a:CustomerId>1000117</a:CustomerId>
			assertEquals( (Long)1000117L, response.getCustomerId() );
			//                  <a:CustomerReference/>
			assertEquals( null, response.getCustomerReference() );
			//                  <a:DeliveryAddress i:nil="true" xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			// NOT SUPPORTED deliveryAddress
			//                  <a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
			assertEquals( false, response.getIsPossibleToAdminister() );
			//                  <a:IsPossibleToCancel>true</a:IsPossibleToCancel>
			assertEquals( true, response.getIsPossibleToCancel() );
			//                  <a:Notes i:nil="true"/>
			assertEquals( null, response.getNotes() );					// <a:Notes i:nil="true" />
			//                  <a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
			assertEquals( ORDERDELIVERYSTATUS.CREATED, response.getOrderDeliveryStatus() );

			//                  <a:OrderRows>
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			assertEquals( 1, numberedOrderRows.size() );
			
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
				assertTrue( orderRow instanceof NumberedOrderRowBuilder );
				//                 <a:NumberedOrderRow>
				//                    <ArticleNumber i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
				assertEquals( null, orderRow.getArticleNumber() );
				//                    <Description xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">AO</Description>
				assertEquals( "AO", orderRow.getDescription() );
				//                    <DiscountPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
				assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
				//                    <NumberOfUnits xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
				assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
				//                    <PriceIncludingVat xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">true</PriceIncludingVat>
				//                    <PricePerUnit xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">125.00</PricePerUnit>
				assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
				//                    <Unit i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
				assertEquals( null, orderRow.getUnit() );
				//                    <VatPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
				assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
				//                    <a:CreditInvoiceId i:nil="true"/>
				assertEquals( null, orderRow.getCreditInvoiceId() );
				//                    <a:InvoiceId i:nil="true"/>
				assertEquals( null, orderRow.getInvoiceId() );
				//                    <a:RowNumber>1</a:RowNumber>
				assertEquals( (Integer)1, orderRow.getRowNumber() );
				//                    <a:Status>NotDelivered</a:Status>
				assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
				//                 </a:NumberedOrderRow>			
			//					</a:OrderRows>
			//              <a:OrderStatus>Active</a:OrderStatus>
			assertEquals( ORDERSTATUS.ACTIVE,response.getOrderStatus() );
			//                  <a:OrderType>Invoice</a:OrderType>
			assertEquals( ORDERTYPE.Invoice, response.getOrderType() );
			//                  <a:PaymentPlanDetails i:nil="true"/>
			assertEquals( null, response.getPaymentPlanDetailsContractLengthMonths() );
			assertEquals( null, response.getPaymentPlanDetailsContractNumber() );
			//                  <a:PendingReasons/>
			assertEquals( null, response.getPendingReasonsCreatedDate() );
			assertEquals( null, response.getPendingReasonsPendingType() );
			//                  <a:SveaOrderId>504352</a:SveaOrderId>
			assertEquals( (Long)504352L, response.getOrderId() );
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

    @Test
    public void test_SE_IndividualCustomer_multiple_rows() {
    	
    	Long invoiceOrder = 504353L;	// existing order    	
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
		//                     <web:Description>A</web:Description>
		//                     <web:PricePerUnit>125</web:PricePerUnit>
		//                     <web:NumberOfUnits>1</web:NumberOfUnits>
		//                     <web:PriceIncludingVat>true</web:PriceIncludingVat>
		//                     <web:VatPercent>25</web:VatPercent>
		//                     <web:DiscountPercent>0</web:DiscountPercent>
		//                  </web:OrderRow>
		//                     <web:OrderRow>
		//                     <web:Description>B</web:Description>
		//                     <web:PricePerUnit>125</web:PricePerUnit>
		//                     <web:NumberOfUnits>1</web:NumberOfUnits>
		//				 <web:PriceIncludingVat>true</web:PriceIncludingVat>
		//				 <web:VatPercent>25</web:VatPercent>
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

			assertEquals( null, response.getChangeDate() );
			assertEquals( (Long)79021L, response.getClientId() );
			assertEquals( "743", response.getClientOrderId() );
			assertEquals( "2015-01-12T10:36:02.887", response.getCreatedDate() );			
			assertEquals( true, response.getCreditReportStatusAccepted() );
			assertEquals( "2015-01-12T10:36:02.95", response.getCreditReportStatusCreationDate() );					
			assertEquals( "SEK", response.getCurrency() );
			assertEquals( "c/o Eriksson, Erik", response.getIndividualCustomer().getCoAddress());			
			assertTrue( response.getIndividualCustomer() instanceof IndividualCustomer );
			//assertEquals( null, response.getIndividualCustomer().getEmail());  // -- returns current customer stats, may change in test			
			assertEquals( "Persson, Tess T", response.getIndividualCustomer().getName());			
			assertEquals( null, response.getIndividualCustomer().getBirthDate());			
			assertEquals( null, response.getIndividualCustomer().getFirstName());			
			assertEquals( null, response.getIndividualCustomer().getInitials());			
			assertEquals( null, response.getIndividualCustomer().getLastName());				
			assertEquals( "Stan", response.getIndividualCustomer().getLocality());	
			assertEquals( "194605092222", response.getIndividualCustomer().getNationalIdNumber());			
			//assertEquals( "08 - 111 111 11", response.getIndividualCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test
			assertEquals( "Testgatan 1", response.getIndividualCustomer().getStreetAddress());			
			assertEquals( null, response.getIndividualCustomer().getHouseNumber());	
			assertEquals( "99999", response.getIndividualCustomer().getZipCode());			
			assertEquals( (Long)1000117L, response.getCustomerId() );
			assertEquals( null, response.getCustomerReference() );
			assertEquals( false, response.getIsPossibleToAdminister() );
			assertEquals( true, response.getIsPossibleToCancel() );
			assertEquals( null, response.getNotes() );
			assertEquals( ORDERDELIVERYSTATUS.CREATED, response.getOrderDeliveryStatus() );
			
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			assertEquals( 2, numberedOrderRows.size() );
			
			NumberedOrderRowBuilder orderRow =  numberedOrderRows.get(0);
				assertTrue( orderRow instanceof NumberedOrderRowBuilder );
				assertEquals( null, orderRow.getArticleNumber() );
				assertEquals( "A", orderRow.getDescription() );
				assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
				assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
				assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
				assertEquals( null, orderRow.getUnit() );
				assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
				assertEquals( null, orderRow.getCreditInvoiceId() );
				assertEquals( null, orderRow.getInvoiceId() );
				assertEquals( (Integer)1, orderRow.getRowNumber() );
				assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );

			orderRow =  numberedOrderRows.get(1);
				assertTrue( orderRow instanceof NumberedOrderRowBuilder );
				assertEquals( null, orderRow.getArticleNumber() );
				assertEquals( "B", orderRow.getDescription() );
				assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
				assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
				assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
				assertEquals( null, orderRow.getUnit() );
				assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
				assertEquals( null, orderRow.getCreditInvoiceId() );
				assertEquals( null, orderRow.getInvoiceId() );
				assertEquals( (Integer)2, orderRow.getRowNumber() );
				assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );			
				assertEquals( ORDERSTATUS.ACTIVE,response.getOrderStatus() );
				assertEquals( ORDERTYPE.Invoice, response.getOrderType() );
				assertEquals( null, response.getPaymentPlanDetailsContractLengthMonths() );
				assertEquals( null, response.getPaymentPlanDetailsContractNumber() );				
				assertEquals( null, response.getPendingReasonsCreatedDate() );				
				assertEquals( null, response.getPendingReasonsPendingType() );				
			assertEquals( (Long)504353L, response.getOrderId() );
			assertEquals( true, response.getSveaWillBuy() );	
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }     

    // invoice, company customer, one-many orderrows
    @Test
    public void test_SE_CompanyCustomer_single_row() {
    	
    	Long invoiceOrder = 504354L;	// existing order    	
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
		//                  <web:NationalIdNumber>4608142222</web:NationalIdNumber>
		//                  <web:CountryCode>SE</web:CountryCode>
		//                  <web:CustomerType>Company</web:CustomerType>
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
			assertEquals( null, response.getChangeDate() );		
			//                  <a:ClientId>79021</a:ClientId>
			assertEquals( (Long)79021L, response.getClientId() );
			//                  <a:ClientOrderId>743</a:ClientOrderId>
			assertEquals( "743", response.getClientOrderId() );
			//                  <a:CreatedDate>2015-01-12T10:37:32.797</a:CreatedDate>
			assertEquals( "2015-01-12T10:37:32.797", response.getCreatedDate() );			
			//                  <a:CreditReportStatus>
			//                     <a:Accepted>true</a:Accepted>
			assertEquals( true, response.getCreditReportStatusAccepted() );
			//                     <a:CreationDate>2015-01-12T10:37:32.873</a:CreationDate>
			assertEquals( "2015-01-12T10:37:32.873", response.getCreditReportStatusCreationDate() );					
			//                  </a:CreditReportStatus>
			//                  <a:Currency>SEK</a:Currency>
			assertEquals( "SEK", response.getCurrency() );

			//                  <a:Customer xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//                     <b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			assertEquals( "c/o Eriksson, Erik", response.getCompanyCustomer().getCoAddress());			
			//                     <b:CompanyIdentity>
			//                        <b:CompanyIdentification i:nil="true"/>
			// NOT IN USE CompanyIdentification
			//                        <b:CompanyVatNumber i:nil="true"/>
			assertEquals( null, response.getCompanyCustomer().getVatNumber() );	
			//                     </b:CompanyIdentity>
			//                     <b:CountryCode>SE</b:CountryCode>
			// NOT SUPPORTED countryCode
			//                     <b:CustomerType>Company</b:CustomerType>
			assertTrue( response.getCompanyCustomer() instanceof CompanyCustomer );
			//                     <b:Email i:nil="true"/>
			//assertEquals( null, response.getCompanyCustomer().getEmail());  // -- returns current customer stats, may change in test			
			//                     <b:FullName>Persson, Tess T</b:FullName>
			assertEquals( "Persson, Tess T", response.getCompanyCustomer().getCompanyName());			
			//                     <b:HouseNumber i:nil="true"/>
			assertEquals( null, response.getCompanyCustomer().getHouseNumber());	
			//                     <b:IndividualIdentity i:nil="true"/>
			//                     <b:Locality>Stan</b:Locality>
			assertEquals( "Stan", response.getCompanyCustomer().getLocality());	
			//                     <b:NationalIdNumber>164608142222</b:NationalIdNumber>
			assertEquals( "164608142222", response.getCompanyCustomer().getNationalIdNumber());			
			//                     <b:PhoneNumber i:nil="true"/>
			//assertEquals( null, response.getCompanyCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test			
			//                     <b:PublicKey i:nil="true"/>
			// NOT SUPPORTED publicKey
			//                     <b:Street>Testgatan 1</b:Street>
			assertEquals( "Testgatan 1", response.getCompanyCustomer().getStreetAddress());			
			//                     <b:ZipCode>99999</b:ZipCode>
			assertEquals( "99999", response.getCompanyCustomer().getZipCode());	
			//                  </a:Customer>
			//                  <a:CustomerId>1000119</a:CustomerId>
			assertEquals( (Long)1000119L, response.getCustomerId() );
			//                  <a:CustomerReference/>
			assertEquals( null, response.getCustomerReference() );
			//                  <a:DeliveryAddress i:nil="true" xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			// NOT SUPPORTED deliveryAddress
			//                  <a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
			assertEquals( false, response.getIsPossibleToAdminister() );
			//                  <a:IsPossibleToCancel>true</a:IsPossibleToCancel>
			assertEquals( true, response.getIsPossibleToCancel() );
			//                  <a:Notes i:nil="true"/>
			assertEquals( null, response.getNotes() );					
			//                  <a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
			assertEquals( ORDERDELIVERYSTATUS.CREATED, response.getOrderDeliveryStatus() );

			//                  <a:OrderRows>
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
			
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			assertEquals( 1, numberedOrderRows.size() );
			
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
				assertTrue( orderRow instanceof NumberedOrderRowBuilder );
				assertEquals( null, orderRow.getArticleNumber() );
				assertEquals( "AO", orderRow.getDescription() );
				assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
				assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
				assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
				assertEquals( null, orderRow.getUnit() );
				assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
				assertEquals( null, orderRow.getCreditInvoiceId() );
				assertEquals( null, orderRow.getInvoiceId() );
				assertEquals( (Integer)1, orderRow.getRowNumber() );
				assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
			
			//                  <a:OrderStatus>Active</a:OrderStatus>
			assertEquals( ORDERSTATUS.ACTIVE,response.getOrderStatus() );
			//                  <a:OrderType>Invoice</a:OrderType>
			assertEquals( ORDERTYPE.Invoice, response.getOrderType() );
			//                  <a:PaymentPlanDetails i:nil="true"/>
			assertEquals( null, response.getPaymentPlanDetailsContractLengthMonths() );
			assertEquals( null, response.getPaymentPlanDetailsContractNumber() );				
			//                  <a:PendingReasons/>
			assertEquals( null, response.getPendingReasonsCreatedDate() );				
			assertEquals( null, response.getPendingReasonsPendingType() );				
			//                  <a:SveaOrderId>504354</a:SveaOrderId>
			assertEquals( (Long)504354L, response.getOrderId() );
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

    @Test
    public void test_SE_CompanyCustomer_multiple_rows() {
    	
    	Long invoiceOrder = 504355L;	// existing order    	
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
		//                     <web:Description>A</web:Description>
		//                     <web:PricePerUnit>125</web:PricePerUnit>
		//                     <web:NumberOfUnits>1</web:NumberOfUnits>
		//                     <web:PriceIncludingVat>true</web:PriceIncludingVat>
		//                     <web:VatPercent>25</web:VatPercent>
		//                     <web:DiscountPercent>0</web:DiscountPercent>
		//                  </web:OrderRow>
		//                  <web:OrderRow>
		//                     <web:Description>B</web:Description>
		//                     <web:PricePerUnit>125</web:PricePerUnit>
		//                     <web:NumberOfUnits>1</web:NumberOfUnits>
		//                     <web:PriceIncludingVat>true</web:PriceIncludingVat>
		//                     <web:VatPercent>25</web:VatPercent>
		//                     <web:DiscountPercent>0</web:DiscountPercent>
		//                  </web:OrderRow>
		//               </web:OrderRows>
		//               <web:CustomerIdentity>
		//                  <web:NationalIdNumber>4608142222</web:NationalIdNumber>
		//                  <web:CountryCode>SE</web:CountryCode>
		//                  <web:CustomerType>Company</web:CustomerType>
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
	
			assertEquals( null, response.getChangeDate() );		
			assertEquals( (Long)79021L, response.getClientId() );
			assertEquals( "743", response.getClientOrderId() );
			assertEquals( "2015-01-12T10:39:18.003", response.getCreatedDate() );			
			assertEquals( true, response.getCreditReportStatusAccepted() );
			assertEquals( "2015-01-12T10:39:18.08", response.getCreditReportStatusCreationDate() );					
			assertEquals( "SEK", response.getCurrency() );
			assertEquals( "c/o Eriksson, Erik", response.getCompanyCustomer().getCoAddress());			
			// NOT SUPPORTED CompanyIdentification
			assertEquals( null, response.getCompanyCustomer().getVatNumber() );	
			// NOT SUPPORTED countryCode
			assertTrue( response.getCompanyCustomer() instanceof CompanyCustomer );
			//assertEquals( null, response.getCompanyCustomer().getEmail());  // -- returns current customer stats, may change in test			
			assertEquals( "Persson, Tess T", response.getCompanyCustomer().getCompanyName());			
			assertEquals( null, response.getCompanyCustomer().getHouseNumber());	
			assertEquals( "Stan", response.getCompanyCustomer().getLocality());	
			assertEquals( "164608142222", response.getCompanyCustomer().getNationalIdNumber());			
			//assertEquals( null, response.getCompanyCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test			
			// NOT SUPPORTED publicKey
			assertEquals( "Testgatan 1", response.getCompanyCustomer().getStreetAddress());			
			assertEquals( "99999", response.getCompanyCustomer().getZipCode());			
			assertEquals( (Long)1000119L, response.getCustomerId() );
			assertEquals( null, response.getCustomerReference() );
			// NOT SUPPORTED deliveryAddress
			assertEquals( false, response.getIsPossibleToAdminister() );
			assertEquals( true, response.getIsPossibleToCancel() );
			assertEquals( null, response.getNotes() );					
			assertEquals( ORDERDELIVERYSTATUS.CREATED, response.getOrderDeliveryStatus() );
			
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			assertEquals( 2, numberedOrderRows.size() );
			
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
			assertTrue( orderRow instanceof NumberedOrderRowBuilder );
			assertEquals( null, orderRow.getArticleNumber() );
			assertEquals( "A", orderRow.getDescription() );
			assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
			assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
			assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
			assertEquals( null, orderRow.getUnit() );
			assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
			assertEquals( null, orderRow.getCreditInvoiceId() );
			assertEquals( null, orderRow.getInvoiceId() );
			assertEquals( (Integer)1, orderRow.getRowNumber() );
			assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
			
			orderRow = numberedOrderRows.get(1); 			
			assertTrue( orderRow instanceof NumberedOrderRowBuilder );
			assertEquals( null, orderRow.getArticleNumber() );
			assertEquals( "B", orderRow.getDescription() );
			assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
			assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
			assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
			assertEquals( null, orderRow.getUnit() );
			assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
			assertEquals( null, orderRow.getCreditInvoiceId() );
			assertEquals( null, orderRow.getInvoiceId() );
			assertEquals( (Integer)2, orderRow.getRowNumber() );
			assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );			
			
			assertEquals( ORDERSTATUS.ACTIVE,response.getOrderStatus() );
			assertEquals( ORDERTYPE.Invoice, response.getOrderType() );
			assertEquals( null, response.getPaymentPlanDetailsContractLengthMonths() );
			assertEquals( null, response.getPaymentPlanDetailsContractNumber() );
			assertEquals( null, response.getPendingReasonsCreatedDate() );
			assertEquals( null, response.getPendingReasonsPendingType() );
			assertEquals( (Long)504355L, response.getOrderId() );
			assertEquals( true, response.getSveaWillBuy() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }     

    // paymentplan, paymentplandetails
    @Test
    public void test_PaymentPlan_SE_IndividualCustomer_single_row() {
    	
    	Long invoiceOrder = 504769L;	// existing order    	
    	//<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="https://webservices.sveaekonomi.se/webpay">
    	//    <soapenv:Header/>
    	//    <soapenv:Body>
    	//       <web:CreateOrderEu>
    	//          <web:request>
    	//             <web:Auth>
    	//                <web:ClientNumber>59999</web:ClientNumber>
    	//                <web:Username>sverigetest</web:Username>
    	//                <web:Password>sverigetest</web:Password>
    	//             </web:Auth>
    	//             <web:CreateOrderInformation>
    	//                <web:ClientOrderNumber>743</web:ClientOrderNumber>
    	//                <web:OrderRows>
    	//                   <web:OrderRow>
    	//                      <web:Description>AO</web:Description>
    	//                      <web:PricePerUnit>1250</web:PricePerUnit>
    	//                      <web:NumberOfUnits>1</web:NumberOfUnits>
    	//                      <web:PriceIncludingVat>true</web:PriceIncludingVat>
    	//                      <web:VatPercent>25</web:VatPercent>
    	//                      <web:DiscountPercent>0</web:DiscountPercent>
    	//                   </web:OrderRow>
    	//                </web:OrderRows>
    	//                <web:CustomerIdentity>
    	//                   <web:NationalIdNumber>4605092222</web:NationalIdNumber>
    	//                   <web:CountryCode>SE</web:CountryCode>
    	//                   <web:CustomerType>Individual</web:CustomerType>
    	//                </web:CustomerIdentity>
    	//                <web:OrderDate>2015-01-14T09:58:16.861</web:OrderDate>
    	//                <web:OrderType>PaymentPlan</web:OrderType>
    	//                <web:CreatePaymentPlanDetails>
    	//                   <web:CampaignCode>213060</web:CampaignCode>
    	//                   <web:SendAutomaticGiroPaymentForm>0</web:SendAutomaticGiroPaymentForm>
    	//                </web:CreatePaymentPlanDetails>
    	//             </web:CreateOrderInformation>
    	//          </web:request>
    	//       </web:CreateOrderEu>
    	//    </soapenv:Body>
    	//</soapenv:Envelope>

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryPaymentPlanOrder().doRequest();
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
			assertEquals( null, response.getChangeDate() );
			//                  <a:ClientId>59999</a:ClientId>
			assertEquals( (Long)59999L, response.getClientId() );
			//                  <a:ClientOrderId>743</a:ClientOrderId>
			assertEquals( "743", response.getClientOrderId() );
			//                  <a:CreatedDate>2015-01-14T10:59:50.107</a:CreatedDate>
			assertEquals( "2015-01-14T10:59:50.107", response.getCreatedDate() );			
			//                  <a:CreditReportStatus>
			//                     <a:Accepted>true</a:Accepted>
			assertEquals( true, response.getCreditReportStatusAccepted() );
			//                     <a:CreationDate>2015-01-14T10:59:50.217</a:CreationDate>
			assertEquals( "2015-01-14T10:59:50.217", response.getCreditReportStatusCreationDate() );					
			//                  </a:CreditReportStatus>
			//                  <a:Currency>SEK</a:Currency>
			assertEquals( "SEK", response.getCurrency() );

			//                  <a:Customer xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">
			//                     <b:CoAddress>c/o Eriksson, Erik</b:CoAddress>
			assertEquals( "c/o Eriksson, Erik", response.getIndividualCustomer().getCoAddress());			
			//                     <b:CompanyIdentity i:nil="true"/>
			//                     <b:CountryCode>SE</b:CountryCode>
			// NOT SUPPORTED countryCode			
			
			//                     <b:CustomerType>Individual</b:CustomerType>
			assertTrue( response.getIndividualCustomer() instanceof IndividualCustomer );
			//                     <b:Email i:nil="true"/>
			//assertEquals( null, response.getIndividualCustomer().getEmail());  // -- returns current customer stats, may change in test			
			//                     <b:FullName>Persson, Tess T</b:FullName>
			assertEquals( "Persson, Tess T", response.getIndividualCustomer().getName());			
			//                     <b:HouseNumber i:nil="true"/>
			assertEquals( null, response.getIndividualCustomer().getHouseNumber());	
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
			//                     <b:PhoneNumber i:nil="true"/>
			//assertEquals( "08 - 111 111 11", response.getIndividualCustomer().getPhoneNumber());  // -- returns current customer stats, may change in test	
			//                     <b:PublicKey i:nil="true"/>
			// NOT SUPPORTED publicKey
			//                     <b:Street>Testgatan 1</b:Street>
			assertEquals( "Testgatan 1", response.getIndividualCustomer().getStreetAddress());			
			//                     <b:ZipCode>99999</b:ZipCode>
			assertEquals( "99999", response.getIndividualCustomer().getZipCode());			
			//                  </a:Customer>
			//                  <a:CustomerId>1000013</a:CustomerId>
			assertEquals( (Long)1000013L, response.getCustomerId() );
			//                  <a:CustomerReference/>
			assertEquals( null, response.getCustomerReference() );
			//                  <a:DeliveryAddress i:nil="true" xmlns:b="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			// NOT SUPPORTED deliveryAddress
			//                  <a:IsPossibleToAdminister>false</a:IsPossibleToAdminister>
			assertEquals( false, response.getIsPossibleToAdminister() );
			//                  <a:IsPossibleToCancel>true</a:IsPossibleToCancel>
			assertEquals( true, response.getIsPossibleToCancel() );
			//                  <a:Notes i:nil="true"/>
			assertEquals( null, response.getNotes() );					// <a:Notes i:nil="true" />
			//                  <a:OrderDeliveryStatus>Created</a:OrderDeliveryStatus>
			assertEquals( ORDERDELIVERYSTATUS.CREATED, response.getOrderDeliveryStatus() );

			//                  <a:OrderRows>
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			assertEquals( 1, numberedOrderRows.size() );
			
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
			assertTrue( orderRow instanceof NumberedOrderRowBuilder );
			//                     <a:NumberedOrderRow>
			//                        <ArticleNumber i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			assertEquals( null, orderRow.getArticleNumber() );
			//                        <Description xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">AO</Description>
			assertEquals( "AO", orderRow.getDescription() );
			//                        <DiscountPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">0.00</DiscountPercent>
			assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
			//                        <NumberOfUnits xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1.00</NumberOfUnits>
			assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
			//                        <PriceIncludingVat xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">true</PriceIncludingVat>
			assertEquals( Double.valueOf(1250.00), Double.valueOf(orderRow.getAmountIncVat()) );
			//                        <PricePerUnit xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">1250.00</PricePerUnit>
			//                        <Unit i:nil="true" xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice"/>
			assertEquals( null, orderRow.getUnit() );
			//                        <VatPercent xmlns="http://schemas.datacontract.org/2004/07/DataObjects.Webservice">25.00</VatPercent>
			assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
			//                        <a:CreditInvoiceId i:nil="true"/>
			assertEquals( null, orderRow.getCreditInvoiceId() );
			//                        <a:InvoiceId i:nil="true"/>
			assertEquals( null, orderRow.getInvoiceId() );
			//                        <a:RowNumber>1</a:RowNumber>
			assertEquals( (Integer)1, orderRow.getRowNumber() );
			//                        <a:Status>NotDelivered</a:Status>
			assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
			//                     </a:NumberedOrderRow>
			//                  </a:OrderRows>
			
			//                  <a:OrderStatus>Active</a:OrderStatus>
			assertEquals( ORDERSTATUS.ACTIVE,response.getOrderStatus() );
			//                  <a:OrderType>PaymentPlan</a:OrderType>
			assertEquals( ORDERTYPE.PaymentPlan, response.getOrderType() );
			//                  <a:PaymentPlanDetails>
			//                     <a:CampaignCode>213060</a:CampaignCode>
			//                     <a:ContractLengthMonths>3</a:ContractLengthMonths>
			assertEquals( (Integer)3, response.getPaymentPlanDetailsContractLengthMonths() );
			//                     <a:ContractNumber i:nil="true"/>
			assertEquals( null, response.getPaymentPlanDetailsContractNumber() );
			//                  </a:PaymentPlanDetails>
			//                  <a:PendingReasons/>
			assertEquals( null, response.getPendingReasonsCreatedDate() );
			assertEquals( null, response.getPendingReasonsPendingType() );
			//                  <a:SveaOrderId>504769</a:SveaOrderId>
			assertEquals( (Long)504769L, response.getOrderId() );
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
    
    // pendingreasons
    @Test
    public void test_pendingreasons() {
    	
    	Long invoiceOrder = 140396L;	// existing order (note, also has DeliveryAddress set)

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertTrue( response.isOrderAccepted() );        

			assertEquals( (Long)140396L, response.getOrderId() );     	
			//   <a:PendingReasons>
			//	      <a:PendingReason>
			//	         <a:CreatedDate>2013-05-29T15:42:28.777</a:CreatedDate>
			//	         <a:PendingType>UseOfDeliveryAddress</a:PendingType>
			//	      </a:PendingReason>
			//   </a:PendingReasons>
			assertEquals( "2013-05-29T15:42:28.777", response.getPendingReasonsCreatedDate() );
			assertEquals( PENDINGTYPE.USEOFDELIVERYADDRESS, response.getPendingReasonsPendingType() );
			assertEquals( "UseOfDeliveryAddress", response.getPendingReasonsPendingType().toString() );
        }
        catch( Exception e ) {
        	Assert.fail( "Unexpected exception: " + e.getMessage());
        }
    }     

	// price ex.vat
    @Test
    public void test_price_given_ex_vat() {
    	
    	Long invoiceOrder = 504816L;	// existing order    	

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertTrue( response.isOrderAccepted() );     

			//               <web:OrderRows>
			//                  <web:OrderRow>
			//                     <web:Description>AO_exvat</web:Description>
			//                     <web:PricePerUnit>100</web:PricePerUnit>
			//                     <web:NumberOfUnits>1</web:NumberOfUnits>
			//                     <web:PriceIncludingVat>false</web:PriceIncludingVat>
			//                     <web:VatPercent>25</web:VatPercent>
			//                     <web:DiscountPercent>0</web:DiscountPercent>
			//                  </web:OrderRow>
			//               </web:OrderRows>

			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
			assertEquals( null, orderRow.getArticleNumber() );
			assertEquals( "AO_exvat", orderRow.getDescription() );
			assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
			assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
			assertEquals( Double.valueOf(Double.NaN), Double.valueOf(orderRow.getAmountIncVat()) );
			assertEquals( Double.valueOf(100.00), Double.valueOf(orderRow.getAmountExVat()) );
			assertEquals( null, orderRow.getUnit() );
			assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
			assertEquals( null, orderRow.getCreditInvoiceId() );
			assertEquals( null, orderRow.getInvoiceId() );
			assertEquals( (Integer)1, orderRow.getRowNumber() );
			assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }     

    public void test_price_given_inc_vat() {
    	
    	Long invoiceOrder = 504354L;	// existing order    	

        // query order
        QueryOrderBuilder queryOrderBuilder = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
            .setOrderId( invoiceOrder )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();
			assertTrue( response.isOrderAccepted() );     

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

			ArrayList<NumberedOrderRowBuilder> numberedOrderRows = response.getNumberedOrderRows();
			NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
			assertEquals( null, orderRow.getArticleNumber() );
			assertEquals( "AO", orderRow.getDescription() );
			assertEquals( Double.valueOf(0.00), Double.valueOf(orderRow.getDiscountPercent()) );
			assertEquals( Double.valueOf(1.00), Double.valueOf(orderRow.getQuantity()) );
			assertEquals( Double.valueOf(125.00), Double.valueOf(orderRow.getAmountIncVat()) );
			assertEquals( Double.valueOf(Double.NaN), Double.valueOf(orderRow.getAmountExVat()) );
			assertEquals( null, orderRow.getUnit() );
			assertEquals( Double.valueOf(25.00), Double.valueOf(orderRow.getVatPercent()) );
			assertEquals( null, orderRow.getCreditInvoiceId() );
			assertEquals( null, orderRow.getInvoiceId() );
			assertEquals( (Integer)1, orderRow.getRowNumber() );
			assertEquals( ORDERROWSTATUS.NOTDELIVERED, orderRow.getStatus() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }     
    
}
