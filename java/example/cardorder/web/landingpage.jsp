<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="se.sveaekonomi.webpay.integration.response.hosted.SveaResponse" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Card Order Landing Page</title>
</head>
<body>
<pre>
Your card order payment request response:

<% 
	out.println( "<p id='svearesponse'>Raw response:</p>" );
	String rawResponse = (String) request.getAttribute("raw_response");
	out.println( "<span id='rawresponse'>" + rawResponse + "</span>" );

	SveaResponse cardOrderResponse = (SveaResponse) request.getAttribute("cardorder_response");

	out.println( "<p id='svearesponse'>SveaResponse:</p>" );
	// Response attributes	
	out.println( "    isOrderAccepted: <span id='accepted'>" + cardOrderResponse.isOrderAccepted() +"</span>" );
	out.println( "    resultCode: <span id='resultCode'>" + cardOrderResponse.getResultCode() +"</span>" );
	out.println( "    errorMessage: <span id='errorMessage'>" + cardOrderResponse.getErrorMessage() +"</span>" );
	// SveaResponse attributes	
	out.println( "    transactionId: <span id='transactionId'>" + cardOrderResponse.getTransactionId() +"</span>" );
	out.println( "    paymentMethod: <span id='paymentMethod'>" + cardOrderResponse.getPaymentMethod() +"</span>" );
	out.println( "    merchantId: <span id='merchantId'>" + cardOrderResponse.getMerchantId() +"</span>" );
	out.println( "    clientOrderNumber: <span id='clientOrderNumber'>" + cardOrderResponse.getClientOrderNumber() +"</span>" );
	out.println( "    amount: <span id='amount'>" + cardOrderResponse.getAmount() +"</span>" );
	out.println( "    currency: <span id='currency'>" + cardOrderResponse.getCurrency() +"</span>" );
	out.println( "    subscriptionId: <span id='subscriptionId'>" + cardOrderResponse.getSubscriptionId() +"</span>" );
	out.println( "    cardtype: <span id='cardtype'>" + cardOrderResponse.getCardType() +"</span>" );
	out.println( "    maskedCardNumber: <span id='maskedCardNumber'>" + cardOrderResponse.getMaskedCardNumber() +"</span>" );
	out.println( "    expiryMonth: <span id='expiryMonth'>" + cardOrderResponse.getExpiryMonth() +"</span>" );
	out.println( "    expiryYear: <span id='expiryYear'>" + cardOrderResponse.getExpiryYear() +"</span>" );
	out.println( "    authCode: <span id='authCode'>" + cardOrderResponse.getAuthCode() +"</span>" );
		
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