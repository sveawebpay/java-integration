<%@page import="se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="se.sveaekonomi.webpay.integration.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Invoice Order</title>
</head>
<body>
<pre>
<% 
	// Get the paymentForm passed to us from the servlet
	PaymentForm myPaymentForm = (PaymentForm) request.getAttribute("payment_form");

	// Then send the form to Svea, and receive the response on the landingpage after the customer has completed the card checkout at certitrade
	out.println( "Press submit button to send card payment request to Svea:" );
	out.println( myPaymentForm.getCompleteForm() );
	
	// The service will process the payment request and send the response to the specified return url, i.e. to /landingpage, which routes to the LandingPageServlet
%>	
</pre>
</body>
</html>