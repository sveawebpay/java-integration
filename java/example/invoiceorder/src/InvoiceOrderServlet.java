package se.sveaekonomi.webpay.integration;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		// The shop cart contains one item "Billy" which cost 700,99 kr excluding vat (25%).
		// When selecting to pay using the invoice payment method, the customer has also provided their social security number, which is required for invoice orders.

		// Begin the order creation process by creating an order builder object using the WebPay::createOrder() method:
		CreateOrderBuilder myOrder = WebPay.createOrder(myConfig);
		
		// We then add information to the order object by using the various methods in the CreateOrderBuilder class.

		// We begin by adding any additional information required by the payment method, which for an invoice order means:
		myOrder.setCountryCode(COUNTRYCODE.SE);
		myOrder.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
		
		// To add the cart contents to the order we first create and specify a new orderRow item using methods from the Svea\OrderRow class:
		OrderRowBuilder boughtItem = Item.orderRow();
		boughtItem.setDescription("Billy");
		boughtItem.setAmountExVat(700.99);
		boughtItem.setVatPercent(25);
		boughtItem.setQuantity(1.0);

		// Add the order rows to the order: 
		myOrder.addOrderRow( boughtItem ); 
		
		// Next, we create a customer identity object, for invoice orders Svea will look up the customer address et al based on the social security number
		IndividualCustomer customerInformation = Item.individualCustomer();
		customerInformation.setNationalIdNumber("194605092222");
		
		// Add the customer to the order: 
		myOrder.addCustomerDetails(customerInformation);
		
		// We have now completed specifying the order, and wish to send the payment request to Svea. To do so, we first select the invoice payment method:
		InvoicePayment myInvoiceOrderRequest = myOrder.useInvoicePayment();

		// Then send the request to Svea using the doRequest method, and immediately receive the service response object
		CreateOrderResponse myResponse = myInvoiceOrderRequest.doRequest();
		
		request.setAttribute("invoiceorder_result", "foo");		
		// If the response attribute accepted is true, the payment succeeded.
		if( myResponse.isOrderAccepted() ) {
			request.setAttribute("invoiceorder_result", "invoice payment succeeded");
		}
		else {
			request.setAttribute("invoiceorder_result", "invoice payment failed");
		}
				
		// set hello_string attribute in request, and forward the request to the hello.jsp page where it will be presented to the user
		request.getRequestDispatcher("/invoiceorder.jsp").forward(request, response);
		
	}
}
