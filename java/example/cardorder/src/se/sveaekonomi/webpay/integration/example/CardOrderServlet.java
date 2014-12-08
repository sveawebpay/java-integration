package se.sveaekonomi.webpay.integration.example;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaTestConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * example file, how to create a card order request
 * 
 * @author Kristian Grossman-madsen for Svea WebPay
 */
public class CardOrderServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public CardOrderServlet() {
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

		// The customer has bought three items, one "Billy" which cost 700,99 kr excluding vat (25%) and two hotdogs for 5 kr (incl. vat).

		// We'll also need information about the customer country, and the currency used for this order, etc., see below		
		
		// Begin the order creation process by creating an order builder object using the WebPay::createOrder() method:
		CreateOrderBuilder myOrder = WebPay.createOrder(myConfig);

		// We then add information to the order object by using the various methods in the CreateOrderBuilder class.

		// We begin by adding any additional information required by the intended payment method, which for an card order means:
		myOrder.setCountryCode(COUNTRYCODE.SE);					// customer country, we recommend basing this on the customer billing address
		myOrder.setCurrency(CURRENCY.SEK);                      // order currency
		myOrder.setClientOrderNumber( new String( "order #" + new java.util.Date().getTime() ) );  // required - use a not previously sent client side order identifier, i.e. "order #20140519-371"
		
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
		
		// Also, for card orders addCustomerDetails() is optional, but recommended -- we'll just add what info we have to the order
		customerInformation.setName(customerFirstName, customerLastName);
		customerInformation.setStreetAddress(customerAddress, customerHouseNumber);
		customerInformation.setZipCode(customerZipCode).setLocality(customerCity);
		
		// Add the customer to the order: 
		myOrder.addCustomerDetails(customerInformation);
		
		// We have now completed specifying the order, and wish to send the payment request to Svea. To do so, we first select the invoice payment method:
		// For card orders, we recommend using the .usePaymentMethod(PaymentMethod::KORTCERT), which processes card orders via Certitrade.
		PaymentMethodPayment myCardOrderRequest = (PaymentMethodPayment) myOrder.usePaymentMethod(PAYMENTMETHOD.KORTCERT);

		// Then set any additional required request attributes as detailed below. (See PaymentMethodPayment and HostedPayment classes for details.)
		myCardOrderRequest.setPayPageLanguageCode(LANGUAGECODE.sv);
		myCardOrderRequest.setReturnUrl("http://localhost:8080/CardOrder/landingpage");		// TODO move to constant etc.
				
		// Get a payment form object which you can use to send the payment request to Svea
		PaymentForm myCardOrderPaymentForm = myCardOrderRequest.getPaymentForm();
		
		// Go to cardorder.jsp view to state that we have sent the card order, before we receive the callback and are sent to landingpage
		request.setAttribute("payment_form", myCardOrderPaymentForm);
		request.getRequestDispatcher("/cardorder.jsp").forward(request, response);				
	}
}
