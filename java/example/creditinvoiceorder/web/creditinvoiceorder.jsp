<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse" %>
<%@ page import="se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse" %>

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
	CreateOrderResponse invoiceOrderResponse = (CreateOrderResponse) request.getAttribute("createinvoiceorder_response");

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

Your deliver order request response:

<% 
	DeliverOrderResponse deliverOrderResponse = (DeliverOrderResponse) request.getAttribute("deliverinvoiceorder_response");

	// Response attributes
	out.println( "deliverOrderResponse: " + deliverOrderResponse );
	out.println( "    isOrderAccepted: " + deliverOrderResponse.isOrderAccepted() );
	out.println( "    resultCode: " + deliverOrderResponse.getResultCode() );
	out.println( "    errorMessage: " + deliverOrderResponse.getErrorMessage() );
	// DeliverOrderResponse attributes for an invoice order (note that payment plan orders sets different attributes)
	out.println( "    amount: " + deliverOrderResponse.getAmount() );
	out.println( "    invoiceId: " + deliverOrderResponse.getInvoiceId() );
	out.println( "    dueDate: " + deliverOrderResponse.getDueDate() );
	out.println( "    invoiceDate: " + deliverOrderResponse.getInvoiceDate() );	
	out.println( "    invoiceDistributionType: " + deliverOrderResponse.getInvoiceDistributionType() );
	out.println( "    ocr: " + deliverOrderResponse.getOcr() );
	out.println( "    lowestAmountToPay: " + deliverOrderResponse.getLowestAmountToPay() );
%>

</pre>
<font color='blue'>
<pre>
An example of a successful deliver request response. The 'accepted' attribute is true, and resultcode/errormessage is 0/not set. 
(Note that the customerIdentity received in the response indicates the Svea invoice address, which should normally match the order shipping address.)

deliverOrderResponse: se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse@13eafae1
    isOrderAccepted: true
    resultCode: 0
    errorMessage: null
    amount: 886.23
    invoiceId: 1037321
    dueDate: 2014-12-03T00:00:00+01:00
    invoiceDate: 2014-11-03T00:00:00+01:00
    invoiceDistributionType: Post
    ocr: 79021000103732177
    lowestAmountToPay: 0.0
	
</pre>
</body>
</html>