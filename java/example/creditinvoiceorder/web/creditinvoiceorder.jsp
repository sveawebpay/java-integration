<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Invoice Order</title>
</head>
<body>
<pre>
Your order invoice payment request response:

<% 
	CreateOrderResponse invoiceOrderResponse = (CreateOrderResponse) request.getAttribute("creditinvoiceorder_response");

	// Response attributes
	out.println( "createOrderResponse: " + invoiceOrderResponse );
	out.println( "    isOrderAccepted: " + invoiceOrderResponse.isOrderAccepted() );
	out.println( "    resultCode: " + invoiceOrderResponse.getResultCode() );
	out.println( "    errorMessage: " + invoiceOrderResponse.getErrorMessage() );
	// CreateOrderResponse attributes	
	out.println( "    orderId: " + invoiceOrderResponse.orderId );
	out.println( "    orderType: " + invoiceOrderResponse.orderType );
	out.println( "    sveaWillBuyOrder: " + invoiceOrderResponse.sveaWillBuyOrder );	
	out.println( "    amount: " + invoiceOrderResponse.amount );
	out.println( "    expirationDate: " + invoiceOrderResponse.expirationDate );
	out.println( "    clientOrderNumber: " + invoiceOrderResponse.clientOrderNumber );
	out.println( "    isIndividualIdentity: " + invoiceOrderResponse.isIndividualIdentity );
	out.println( "    customerIdentity: " + invoiceOrderResponse.customerIdentity );
	// CustomerIdentityResponse attributes
	out.println( "        nationalIdNumber: " + invoiceOrderResponse.customerIdentity.getNationalIdNumber() );
	out.println( "        fullName: " + invoiceOrderResponse.customerIdentity.getFullName() );
	out.println( "        intitials: " + invoiceOrderResponse.customerIdentity.getIntitials() );
	out.println( "        coAddress: " + invoiceOrderResponse.customerIdentity.getCoAddress() );
	out.println( "        street: " + invoiceOrderResponse.customerIdentity.getStreet() );
	out.println( "        houseNumber: " + invoiceOrderResponse.customerIdentity.getHouseNumber() );
	out.println( "        zipCode: " + invoiceOrderResponse.customerIdentity.getZipCode() );
	out.println( "        city: " + invoiceOrderResponse.customerIdentity.getCity() );
	out.println( "        countryCode: " + invoiceOrderResponse.customerIdentity.getCountryCode() );
	out.println( "        phoneNumber: " + invoiceOrderResponse.customerIdentity.getPhoneNumber() );
	out.println( "        email: " + invoiceOrderResponse.customerIdentity.getEmail() );
	out.println( "        customerType: " + invoiceOrderResponse.customerIdentity.getCustomerType() );
	out.println( "        ipAddress: " + invoiceOrderResponse.customerIdentity.getIpAddress() );
%>	
</pre>
<font color='blue'>
<pre>
An example of a successful request response. The 'accepted' attribute is true, and resultcode/errormessage is 0/not set. 
(Note that the customerIdentity received in the response indicates the Svea invoice address, which should normally match the order shipping address.)

createOrderResponse: se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse@c383d72
    isOrderAccepted: true
    resultCode: 0
    errorMessage: null
    orderId: 375567
    orderType: Invoice
    sveaWillBuyOrder: true
    amount: 886.24
    expirationDate: 2014-09-07T00:00:00+02:00
    clientOrderNumber: null
    isIndividualIdentity: true
    customerIdentity: se.sveaekonomi.webpay.integration.response.webservice.CustomerIdentityResponse@404a0da2
        nationalIdNumber: 194605092222
        fullName: Persson, Tess T
        intitials: null
        coAddress: c/o Eriksson, Erik
        street: Testgatan 1
        houseNumber: null
        zipCode: 99999
        city: Stan
        countryCode: SE
        phoneNumber: null
        email: null
        customerType: Individual
        ipAddress: null	
</pre>
</body>
</html>