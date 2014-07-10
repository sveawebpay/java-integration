<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="se.sveaekonomi.webpay.integration.response.hosted.SveaResponse" %>
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

	SveaResponse cardOrderResponse = (SveaResponse) request.getAttribute("cardorder_response");

	// Response attributes
	out.println( "SveaResponse: " + cardOrderResponse );
	out.println( "    isOrderAccepted: " + cardOrderResponse.isOrderAccepted() );
	out.println( "    resultCode: " + cardOrderResponse.getResultCode() );
	out.println( "    errorMessage: " + cardOrderResponse.getErrorMessage() );
	// SveaResponse attributes	
	out.println( "    transactionId: " + cardOrderResponse.getTransactionId() );
	out.println( "    paymentMethod: " + cardOrderResponse.getPaymentMethod() );
	out.println( "    merchantId: " + cardOrderResponse.getMerchantId() );
	out.println( "    clientOrderNumber: " + cardOrderResponse.getClientOrderNumber() );
	out.println( "    amount: " + cardOrderResponse.getAmount() );
	out.println( "    currency: " + cardOrderResponse.getCurrency() );
	out.println( "    subscriptionId: " + cardOrderResponse.getSubscriptionId() );
	out.println( "    subscriptionType: " + cardOrderResponse.getSubscriptionType() );
	out.println( "    cardtype: " + cardOrderResponse.getCardType() );
	out.println( "    maskedCardNumber: " + cardOrderResponse.getMaskedCardNumber() );
	out.println( "    expiryMonth: " + cardOrderResponse.getExpiryMonth() );
	out.println( "    expiryYear: " + cardOrderResponse.getExpiryYear() );
	out.println( "    authCode: " + cardOrderResponse.getAuthCode() );

%>	
</pre>
<font color='blue'>
<pre>
An example of a successful request response. The 'accepted' attribute is true, and resultcode/errormessage is 0/not set.

SveaResponse: se.sveaekonomi.webpay.integration.response.hosted.SveaResponse@790edbed
    isOrderAccepted: true
    resultCode: 0 (ORDER_ACCEPTED)
    errorMessage: null
    transactionId: 584407
    paymentMethod: KORTCERT
    merchantId: 1130
    clientOrderNumber: order #1405007001211
    amount: 886.24
    currency: SEK
    subscriptionId: null
    subscriptionType: null
    cardtype: VISA
    maskedCardNumber: 444433xxxxxx1100
    expiryMonth: 01
    expiryYear: 15
    authCode: 160061

</pre>
</body>
</html>