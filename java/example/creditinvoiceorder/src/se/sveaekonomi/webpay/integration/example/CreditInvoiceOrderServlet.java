package se.sveaekonomi.webpay.integration.example;

import java.io.IOException;




// import servlet classes (provided in /lib/servlet-api.jar)
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




// import Svea integration package (provided in /lib/sveawebpay.jar)
import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaTestConfigurationProvider;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;

public class CreditInvoiceOrderServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public CreditInvoiceOrderServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
				
		// get configuration object holding the Svea service login credentials
		ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
				
		// We assume that you've collected the following information about the order in your shop: 
		
		// customer information:
		String customerFirstName = "Tess T";
		String customerLastName = "Persson";
		String customerAddress = "Testgatan";
		String customerHouseNumber = "1";
		String customerZipCode = "99999";
		String customerCity = "Stan";
		String customerCountry = "Sverige";

		// The customer has bought three items, one "Billy" which cost 700,99 kr excluding vat (25%) and two hotdogs for 5 kr (incl. vat).

		// We'll also need information about the customer country, and the currency used for this order, etc., see below		
		
		// Begin the order creation process by creating an order builder object using the WebPay::createOrder() method:
		CreateOrderBuilder myOrder = WebPay.createOrder(myConfig);

		// We then add information to the order object by using the various methods in the CreateOrderBuilder class.

		// We begin by adding any additional information required by the intended payment method, which for an invoice order means:
		myOrder.setCountryCode(COUNTRYCODE.SE);
		myOrder.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		
		// To add the cart contents to the order we first create and specify a new orderRow item using methods from the Svea\OrderRow class:
		OrderRowBuilder boughtItem = Item.orderRow();
		boughtItem.setDescription("Billy");
		boughtItem.setAmountExVat(700.99);
		boughtItem.setVatPercent(25);
		boughtItem.setQuantity(1.0);

		// Add boughtItem to the order: 
		myOrder.addOrderRow( boughtItem ); 
		
		// Add a second item in a fluent fashion 
		myOrder
			.addOrderRow( 
				Item.orderRow()
					.setAmountIncVat(5.00)
					.setVatPercent(12.00)
					.setQuantity(2.0)
					.setDescription("Korv med bröd") 
			)
		;		
		
		// Next, we create a customer identity object, note that for invoice orders Svea overrides any given address w/verified credit report address in the response.
		IndividualCustomer customerInformation = Item.individualCustomer();	// there's also a companyCustomer() method, used for non-person entities
		customerInformation.setNationalIdNumber("194605092222");			// sole required field for an invoice order
		
		// Also, for card orders addCustomerDetails() is optional, but recommended -- we'll just add what info we have, but do remember to check the response address!		
		customerInformation.setName(customerFirstName, customerLastName);
		customerInformation.setStreetAddress(customerAddress, customerHouseNumber);
		customerInformation.setZipCode(customerZipCode).setLocality(customerCity);
		
		// Add the customer to the order: 
		myOrder.addCustomerDetails(customerInformation);
		
		// We have now completed specifying the order, and wish to send the payment request to Svea. To do so, we first select the invoice payment method:
		InvoicePayment myInvoiceOrderRequest = myOrder.useInvoicePayment();

		// Then send the request to Svea using the doRequest method, and immediately receive the service response object
		CreateOrderResponse createOrderResponse = myInvoiceOrderRequest.doRequest();
		
		// Pass service response to creditinvoiceorder.jsp view as attribute in HttpServletRequest
		request.setAttribute("createinvoiceorder_response", createOrderResponse);

		/// Deliver the order, i.e. issue an invoice
		
		// To perform a credit order request the order first has to be delivered to Svea -- we first perform a deliverInvoiceOrder request:
		DeliverOrderBuilder myDeliverInvoiceOrder = WebPay.deliverOrder(myConfig);
		
		// To deliver an order in full, we specify order rows in the DeliverOrderBuilder that match the rows in the original CreateOrderBuilder.		
		// We conveniently retained the first order row in a variable, so we just specify that row.
		myDeliverInvoiceOrder.addOrderRow( boughtItem );
		// The second order row has to be rebuilt, making sure all information matches the item from original order.
		myDeliverInvoiceOrder
			.addOrderRow( 
				Item.orderRow()
					.setAmountIncVat(5.00)
					.setVatPercent(12.00)
					.setQuantity(2.0)
					.setDescription("Korv med bröd") 
			)
		;		
		
		// To deliver an invoice order the following DeliverOrderBuilder methods are required:
		myDeliverInvoiceOrder.setOrderId( createOrderResponse.orderId );
		myDeliverInvoiceOrder.setCountryCode(COUNTRYCODE.SE);
		myDeliverInvoiceOrder.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post);

		// Then get a request object with deliverInvoiceOrder(), and call doRequest() which validates and sends the request, returning a response:
		DeliverOrderResponse deliverOrderResponse = myDeliverInvoiceOrder.deliverInvoiceOrder().doRequest();
				
		request.setAttribute("deliverinvoiceorder_response", deliverOrderResponse);
		
		/// Credit part of the order
		
		// To complete the credit order request we'll issue a credit invoice using the deliverInvoiceOrder request with some additional methods:
		DeliverOrderBuilder myCreditInvoiceOrder = WebPay.deliverOrder(myConfig);
				
		// To deliver an order in full, we specify a credit order row.		
		// The add an order row to the credit invoice.
		myCreditInvoiceOrder
			.addOrderRow( 
				Item.orderRow()
					.setAmountIncVat(100.00)
					.setVatPercent(25.00)
					.setQuantity(1.0)
					.setDescription("Credit issued due to late delivery.") 
			)
		;		
		
		// To deliver a credit invoice order the following DeliverOrderBuilder methods are required:
		myCreditInvoiceOrder.setOrderId( createOrderResponse.orderId );
		myCreditInvoiceOrder.setCountryCode(COUNTRYCODE.SE);
		myCreditInvoiceOrder.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post);
		myCreditInvoiceOrder.setCreditInvoice( Integer.toString( deliverOrderResponse.getInvoiceId() )  ); // the invoice to credit
		
		// Send the credit order request
		DeliverOrderResponse creditOrderResponse = myCreditInvoiceOrder.deliverInvoiceOrder().doRequest();
				
		request.setAttribute("creditinvoiceorder_response", creditOrderResponse);
		
		// pass the request on to the view
		request.getRequestDispatcher("/creditinvoiceorder.jsp").forward(request, response);				
	}
}
