package se.sveaekonomi.webpay.integration;

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
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;

public class InvoiceOrderServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public InvoiceOrderServlet() {
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
		CreateOrderResponse myResponse = myInvoiceOrderRequest.doRequest();
		
		// Pass service response to invoiceorder.jsp view as attribute in HttpServletRequest
		request.setAttribute("invoiceorder_response", myResponse);
		request.getRequestDispatcher("/invoiceorder.jsp").forward(request, response);				
	}
}
