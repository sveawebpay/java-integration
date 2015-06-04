---
layout: index
---
# Svea Java Integration Package Documentation

## Version 2.0.2

## Index <a name="index"></a>

* [I. Introduction](http://sveawebpay.github.io/java-integration#introduction)
* [II. 2.0.0 release notes](http://sveawebpay.github.io/java-integration#200releasenotes)
* [1. Installing and configuration](http://sveawebpay.github.io/java-integration#i1)
* [2. "Hello World"](http://sveawebpay.github.io/java-integration#i2)
* [3. Building an order](http://sveawebpay.github.io/java-integration#i3)
    * [3.1 Order builder](http://sveawebpay.github.io/java-integration#i31)
    * [3.2 Order row items](http://sveawebpay.github.io/java-integration#i32)
    * [3.3 Customer identity](http://sveawebpay.github.io/java-integration#i33)
    * [3.4 Additional order attributes](http://sveawebpay.github.io/java-integration#i34)
    * [3.5 Payment method selection](http://sveawebpay.github.io/java-integration#i35)
    * [3.6 Recommended payment method usage](http://sveawebpay.github.io/java-integration#i36)
* [4. Payment method reference](http://sveawebpay.github.io/java-integration#i4)
    * [4.1 Svea Invoice payment](http://sveawebpay.github.io/java-integration#i41)
    * [4.2 Svea Payment plan payment](http://sveawebpay.github.io/java-integration#i42)
    * [4.3 Card payment](http://sveawebpay.github.io/java-integration#i43)
    * [4.4 Direct bank payment](http://sveawebpay.github.io/java-integration#i44)
    * [4.5 Using the Svea PayPage](http://sveawebpay.github.io/java-integration#i45)
    * [4.6 Examples](http://sveawebpay.github.io/java-integration#i46)
* [5. WebPayItem reference](http://sveawebpay.github.io/java-integration#i5)
    * [5.1 Specifying item price](http://sveawebpay.github.io/java-integration#i51)
    * [5.2 WebPayItem.orderRow()](http://sveawebpay.github.io/java-integration#i52)
    * [5.3 WebPayItem.shippingFee()](http://sveawebpay.github.io/java-integration#i53)
    * [5.4 WebPayItem.invoiceFee()](http://sveawebpay.github.io/java-integration#i54)
    * [5.5 WebPayItem.fixedDiscount()](http://sveawebpay.github.io/java-integration#i55)
    * [5.6 WebPayItem.relativeDiscount](http://sveawebpay.github.io/java-integration#i56)
    * [5.7 WebPayItem.individualCustomer()](http://sveawebpay.github.io/java-integration#i57)
    * [5.8 WebPayItem.companyCustomer()](http://sveawebpay.github.io/java-integration#i58)
    * [5.9 WebPayItem.numberedOrderRow()](http://sveawebpay.github.io/java-integration#i59)
* [6. WebPay entrypoint method reference](http://sveawebpay.github.io/java-integration#i6)
    * [6.1 WebPay.createOrder()](http://sveawebpay.github.io/java-integration#i61)
    * [6.2 WebPay.deliverOrder()](http://sveawebpay.github.io/java-integration#i62)
    * [6.3 WebPay.getAddresses()](http://sveawebpay.github.io/java-integration#i63)
    * [6.4 WebPay.getPaymentPlanParams()](http://sveawebpay.github.io/java-integration#i64)
* [7. WebPayAdmin entrypoint method reference](http://sveawebpay.github.io/java-integration#i7)
    * [7.1 WebPayAdmin.cancelOrder()](http://sveawebpay.github.io/java-integration#i71)
    * [7.2 WebPayAdmin.queryOrder()](http://sveawebpay.github.io/java-integration#i72)
    * [7.3 WebPayAdmin.cancelOrderRows()](http://sveawebpay.github.io/java-integration#i73)
    * [7.4 WebPayAdmin.creditOrderRows()](http://sveawebpay.github.io/java-integration#i74)
    * [7.5 WebPayAdmin.addOrderRows()](http://sveawebpay.github.io/java-integration#i75)
    * [7.6 WebPayAdmin.updateOrderRows()](http://sveawebpay.github.io/java-integration#i76)
    * [7.7 WebPayAdmin.deliverOrderRows()](http://sveawebpay.github.io/java-integration#i77)
* [8. SveaResponse](http://sveawebpay.github.io/java-integration#i8)
    * [8.1. Parsing an asynchronous service response](http://sveawebpay.github.io/java-integration#i81)
    * [8.2. Response accepted and result code](http://sveawebpay.github.io/java-integration#i82)
* [9. Additional Developer Resources and notes](http://sveawebpay.github.io/java-integration#i9)
    * [9.1 Helper.paymentPlanPricePerMonth()](http://sveawebpay.github.io/java-integration#i91)
    * [9.2 Inspect prepareRequest(), validateOrder() methods](http://sveawebpay.github.io/java-integration#i92)
* [10. Frequently Asked Questions](http://sveawebpay.github.io/java-integration#i10)
    * [10.1 Supported currencies](http://sveawebpay.github.io/java-integration#i101)
* [11. Example servlets](http://sveawebpay.github.io/java-integration#i11)
    * [11.1 Running the examples](http://sveawebpay.github.io/java-integration#i111)
    * [11.2 Svea invoice order](http://sveawebpay.github.io/java-integration#i112)
    * [11.3 Card order](http://sveawebpay.github.io/java-integration#i113)

## I. Introduction <a name="introduction"></a>

### Documentation format
This file contains an overview along with class and method reference, as well as examples of how to use the Svea API through the WebPay and WebPayAdmin entrypoint classes.

### Svea API
The WebPay and WebPayAdmin classes make up the Svea API. Together they provide unified entrypoints to the various Svea web services. The API also encompass the support classes ConfigurationProvider, SveaResponse and WebPayItem, as well as various constant container classes and support classes.

The WebPay class methods contains the functions needed to create orders and perform payment requests using Svea payment methods. It contains methods to define order contents, send order requests, as well as support methods needed to do this.

The WebPayAdmin class methods are used to administrate orders after they have been accepted by Svea. It includes functions to update, deliver, cancel and credit orders et.al.

The WebPayItem class methods are used to get i.e. the various order row and customer identity objects that goes into the above order objects.

Each method returns a its own response class object, please refer to these objects to see exactly what attributes are available in for each service request. When administrating an order, you may need to perform some additional type conversion.

### Package design philosophy
In general, a request using the Svea API starts out with you creating an instance of an order builder class, which is then built up with data using fluid method calls. At a certain point, a method is used to select which service the request will go against. This method then returns an instance of a service request class which handles the specifics of building the request, which in turn returns an instance of the corresponding service response class for inspection.

The WebPay API consists of the entrypoint methods in the WebPay and WebPayAdmin classes. These instantiate builder classes in the Svea namespace. Given i.e. an order builder instance, you then use method calls to populate it with order rows and customer identifiction data. You then choose the payment method and get a request class in return. You then send the request and get a service response from Svea in return. In general, the request classes will validate that all required builder class attributes are present, and if not will throw an exception stating what methods are missing for the request in question.

### Synchronous and asynchronous requests
Most service requests are synchronous and return a response immediately. For asynchronous hosted service payment requests, the customer will be redirected to i.e. the selected card payment provider or bank, and you will get a callback to a return url, where where you receive and parse the response.

### Package structure
The package is roughly organised as around the underlying Svea web-, admin- and hosted services, along with the integration package order builder abstraction class.

## II. 2.0.0 release notes <a name="200releasenotes"></a>

### New in 2.0.0
Release 2.0.0 introduces the WebPayAdmin class, which contains entrypoint methods used to administrate orders placed with Svea.

There has also been an effort to consolidate the various request and response class method types.

### Compatibility
Every effort has been made to keep this release backwards compatible with existing integrations based on 1.x of the Java integration package.

For existing response classes any public attributes stay visible, though in several cases new getter/setter methods have been added that are the recommended way of accessing the returned data for new integrations.

For existing request classes overloaded setters have been provided that accept the previously returned attribute types, these are deprecated for new integrations.

In case your existing integration depend on primitive types being returned by response class setters, you may have to do some casting to i.e. (int), and check for null value attributes being returned.

A few unused or meaningless methods have removed, but these should serve no purpose in existing integrations.

[To index](http://sveawebpay.github.io/java-integration#index)

## 1. Installing and configuration <a name="i1"></a>

### 1.1 Requirements
The package should be compatible with most current java versions. Required dependencies can be found in the ant file.

### 1.2 Build and install package jar
To build the svea integration package jar file, use the ant build file located at java/build.xml. Other public targets can be found in the build.xml file. Build the jar file using the command:
```
	ant clean-jar
```
to build, unit test, and create the sveawebpay.jar file (in the target/jar directory), or preferrably
```
	ant clean-integrationtest
```
that will build the jar and also run the integration tests on the created jar. (You may get some warnings during the compile.)

Then make sure to include the resulting sveawebpay.jar in your project build path, and import the Svea package se.sveaekonomi.webpay.integration.*;

### 1.3 Configuration
In order to make use of the Svea services you need to supply your account credentials to authorize yourself against the Svea services. For the Invoice and Payment Plan payment methods, the credentials consist of a set of Username, Password and Client number (one set for each country and service type). For Card and Direct Bank payment methods, the credentials consist of a (single) set of Merchant id and Secret Word.

You should have received the above credentials from Svea when creating a service account. If not, please contact your Svea account manager.

### 1.4 Using your own account credentials with the package
The WebPay and WebPayAdmin entrypoint methods all require a config object when called. The easiest way to get such an object is to use the default SveaConfig.getDefaultConfig() method. It will return a config object with the Svea test account credentials as used by the integration package test suite.

In order to use your own account credentials, you should implement the ConfigurationProvider interface in a class of your own -- your implementation could for instance fetch the needed credentials from a database instead of them being hardcoded in a source file. 

That being said, the easiest way implement the ConfigurationProvider interface may be to copy the provided SveaTestConfigurationProvider.java twice, i.e. giving you a MyTestConfigurationProvider.java and a MyProductionConfigurationProvider.java. 

You could then replace the credentials in these files with your test and production credentials, respectively, and when calling the WebPay or WebPayAdmin entrypoint method instantiate a test or production configuration and pass in as the config argument. 

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 2. "Hello World" <a name="i2"></a>
An example of the WebPay API workflow is the following invoice payment, where we wish to perform an invoice order. Assume that we have already collected all needed order data, and will now build an order containing the ordered items (with price, article number info, et al) and customer information (name, address, et al), select a payment method, and send the payment request to Svea.

### 2.1 A complete invoice order
The following is a complete example of how to place an order using the invoice payment method:

```java

	// get configuration object holding the Svea service login credentials
	ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
			
	// We assume that you've collected the following information about the order in your shop: 
	
	// The customer has bought three items, one "Billy" which cost 700,99 kr excluding vat (25%) and two hotdogs for 5 kr (incl. vat).

	// We'll also need information about the customer country, and sufficient information to identify the customer, see below:		
	
	// Begin the order creation process by creating an order builder object using the WebPay.createOrder() method:
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
				.setDescription("Korv med br√∂d") 
		)
	;		
	
	// Next, we create a customer identity object, noting that for invoice orders Svea overrides any customer-provided address 
	// w/verified credit report address in the create order request response.
	IndividualCustomer customerInformation = WebPayItem.individualCustomer();
	customerInformation.setNationalIdNumber("194605092222");			// sole required field for an invoice order customer in Sweden
		
	// Add the customer to the order: 
	myOrder.addCustomerDetails(customerInformation);
	
	// We have now completed specifying the order, and wish to send the payment request to Svea. To do so, we select the invoice payment method:
	InvoicePayment myInvoiceOrderRequest = myOrder.useInvoicePayment();

	// And then send the request to Svea using the doRequest method. We then immediately receive a create order response object in return
	CreateOrderResponse myResponse = myInvoiceOrderRequest.doRequest();		

```

https://github.com/sveawebpay/java-integration/tree/master/java/example/creditinvoiceorder

See the sample invoice order implementation in the <a href="https://github.com/sveawebpay/java-integration/tree/master/java/example/creditinvoiceorder" target="_blank">example/creditinvoiceorder</a> folder.

### 2.2 What just happened?
Above, we start out by calling the API method WebPay.createOrder(), which returns an instance of the CreateOrderBuilder class.

Then, the class methods addOrderRow(), addCustomerDetails(), setOrderDate(), setCountryCode(), setCustomerReference(), and setClientOrderNumber() are used to populate the orderbuilder object with all required order information needed for an invoice order.

Then, the useInvoicePayment() method is called, returning an instance of the WebService\InvoicePayment class. We then call the doRequest() method, which validates the provided order information, and makes the request to Svea, returning an instance of the WebService\CreateOrderResponse class.

To determine the outcome of the payment request, we can then inspect the response attributes, i.e. check if $response->accepted == true.

### 2.3 Oh, that's cool, but how to use the services directly?
The package structure enables the WebPay and WebPayAdmin entrypoint methods to confine themselves to the order domain, and pushes the various service request details lower into the package stack, away from the immediate viewpoint of the integrator. Thus all payment methods and services are accessed in a uniform way, with the package doing the main work of massaging the order data to fit the selected payment method or service request.

This also provides future compatibility, as the main WebPay and WebPayAdmin entrypoint methods stay stable whereas the details of how the services are being called by the package may change in the future.

That being said, there are no additional prohibitions on using the various service call wrapper classes to access the Svea services directly, while still not having to worry about the details on how to i.e. build the various SOAP calls or format the XML data structures.

It is possible to instantiate the service request classes directly, making sure to set all relevant attributes before performing service request. In general you need to set attributes in the internal request classes directly, as no setter methods are provided.

Now continue reading, and we'll work through the recommended WebPay order building procedure using the WebPay and WebPayAdmin entrypoint methods.

[< To index](http://sveawebpay.github.io/java-integration#index)

## 3. Building an order <a name="i3"></a>
We show how to specify an order, working through the various steps and options along the way:

### 3.1 Order builder <a name="i31"></a>
Start by creating an order using the WebPay.createOrder method. Pass in your configuration and get an instance of OrderBuilder in return.

```
...
	ConfigurationProvider config = new MyConfigurationProvider();	// MyConfiguration implements ConfigurationProvider 
	CreateOrderBuilder myOrder = WebPay.createOrder(config);		// get an order builder class instance
...
```

See further the <a href="https://github.com/sveawebpay/java-integration/blob/master/java/src/se/sveaekonomi/webpay/integration/order/create/CreateOrderBuilder.java" target="_blank">CreateOrderBuilder</a> class for the methods used to add information to an order.

### 3.2 Order row items <a name="i32"></a>
Order row, fee and discount items can be added to the order. Together the individual row item amounts add up to the order total amount to pay.

```
...
	myOrderRow = WebPayItem.orderRow();		// create the order row object
	
	myOrderRow.setQuantity(1);        		// required
	myOrderRow.setAmountIncVat(12.50)		// recommended to specify price using AmountIncVat & VatPercent
	myOrderRow.setVatPercent(25.00)			// recommended to specify price using AmountIncVat & VatPercent
	
	myOrder.addOrderRow( myOrderRow );       // add order row to the order
...
	/* the above code expressed in a more compact, fluent style:
	myOrder.addOrderRow( WebPayItem.orderRow().setQuantity(1).setAmountIncVat(12.50).setVatPercent(25.00) );
	*/
```
https://github.com/sveawebpay/java-integration/blob/master/java/src/se/sveaekonomi/webpay/integration/order/row/OrderRowBuilder.java

See the <a href="https://github.com/sveawebpay/java-integration/blob/master/java/src/se/sveaekonomi/webpay/integration/order/row/OrderRowBuilder.java" target="_blank">OrderRowBuilder</a> class for details.

See [5.2](http://sveawebpay.github.io/java-integration#52) to 5.6 in the WebPayItem class documentation below for more information.

### 3.3 Customer identity <a name="i33"></a>
Create a customer identity object using the WebPayItem.individualCustomer() or WebPayItem.companyCustomer() methods. Use the addCustomerDetails() method to add the customer information to the order.

Customer identity is required for Invoice and Payment plan orders. For Card and Direct bank orders it is optional but recommended.

####3.3.1 Options for individual customers
```
...
	myOrder
	    ...
	    .addCustomerDetails(
	        WebPayItem.individualCustomer()
	       		.setNationalIdNumber(194605092222)
	       		.setName("Tess", "Testson")			// firstName, lastName
	       		.setStreetAddress("Gatan 23")		// or ("Gatan", "23") if a specific HouseNumber is required (NL/DE)
	       		.setZipCode("12345")
	       		.setLocality("Stan")
	        ...
	    )
	;
...
```

See [5.7](http://sveawebpay.github.io/java-integration#57) and 5.8 in the WebPayItem class documentation below for more information on how to specify customer identity items.

### 3.4 Additional order attributes <a name="i34"></a>
Set any additional attributes needed to complete the order using the OrderBuilder methods.

```java
...
	myOrder
	    ...
	    .setCountryCode("SE")                  // required
	    .setCurrency("SEK")                    // required for card payment, direct bank & PayPage payments. Ignored for invoice and payment plan.
	    .setClientOrderNumber("A123456")       // required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
	    .setCustomerReference("att: kgm")      // optional, ignored for card & direct bank orders, max length 30 chars.
	    .setOrderDate("2015-03-09")            // required for invoice and payment plan payments
	;
...
```

### 3.5 Payment method selection <a name="i35"></a>
Finish the order specification process by choosing a payment method with the order builder useXX() methods.

#### 3.5.1 Synchronous payments
Invoice and Payment plan payment methods will perform a synchronous request to Svea and return a response object which you can then inspect.

#### 3.5.2 Asynchronous payments
Hosted payment methods, like Card, Direct Bank and any payment methods accessed via the PayPage, are asynchronous.

After selecting an asynchronous payment method you generally use a request class method to get a payment form object in return. The form is then posted to Svea, where the customer is redirected to the card payment provider service or bank. After the customer completes the payment, a response is sent back to your provided return url, where it can be processed and inspected.

#### 3.5.3 Response URL:s
For asynchronous payment methods, you must specify where to receive the request response. Use the following methods:

`.setReturnUrl()` (required) When a hosted payment transaction completes the payment service will answer with a response xml message sent to the return url. This is also the return url used if the user cancels at i.e. the Certitrade card payment page.

`.setCallbackUrl()` (optional) In case the hosted payment transaction completes, but the service is unable to return a response to the return url, Svea will retry several times using the callback url as a fallback, if specified. This may happen if i.e. the user closes the browser before the payment service redirects back to the shop, or if the transaction times out in lieu of user input. In the latter case, Svea will fail the transaction after at most 30 minutes, and will try to redirect to the callback url.

`.setCancelUrl()` (optional, paypage only) Presents a cancel button on the PayPage. In case the payment method selection is cancelled by the user, Svea will redirect back to the cancel url. Unless a cancel url is specified, no cancel button will be presented at the PayPage.

All of the above url:s should be specified in full, including the scheme part. I.e. always use an url on the format "http://myshop.com/callback", with a maximum length of 256 characters. (See http://www.w3.org/Addressing/URL/url-spec.txt). The callback url further needs to be publicly visible; it can't be on i.e. localhost or only accessible via a private ip address.

The service response received is sent as a base64 encoded XML message. Feed this message to the HostedPaymentResponse response handler along with the customer credentials class to get a parsed a response object. For details, see [HostedPaymentResponse](http://sveawebpay.github.io/java-integration#8-HostedPaymentResponse) below.

### 3.6 Recommended payment method usage <a name="i36"></a>
*I am using the invoice and/or payment plan payment methods in my integration.*

>The best way is to use `.useInvoicePayment()` and `.usePaymentPlanPayment()`. These payment methods are synchronous and will give you an instant response.

*I am using the card and/or direct bank payment methods in my integration.*

>The best way if you know what specific payment you want to use, is to go direct to that specific payment, bypassing the PayPage step, by using
`.usePaymentMethod()`. You can always fetch the available payment methods (cards, banks) configured on your merchant account by using the `WebPay.listPaymentMethods()` method.
>
>You can also use the PayPage with `.usePayPageCardOnly()` and `.usePayPageDirectBankOnly()`, then the customer will get to select from a subset of payment methods on the PayPage.

*I am using all payment methods in my integration, and wish to let the customer select which to use, but don't want to present the PayPage.*

>The most effective way is to use `.useInvoicePayment()` and `.usePaymentPlanPayment()` for the synchronous payments, and use the `.usePaymentMethod()` for the asynchronous requests. First use `WebPay.listPaymentMethods()` to fetch and present the different payment methods configured on you account to the customer, and then build the correct payment request based on your customer's payment method selection.

*I am using more than one payment method and want them all gathered in one place.*

>You can send the customer to *PayPage* and choose to show all your merchant configured payment methods there, or choose to include/exclude one or more payment methods from being presented to the customer. Use the `.usePayPage()` method. Note that sending the customer to *PayPage* may introduce an additional step in the customer checkout flow. Note also that Invoice and PaymentPlan payments will return an asynchronous response when used via PayPage, we strongly recommend not using the *PayPage* with Invoice or PaymentPlan payments.

*I wish to prepare an order and receive a link that I can mail to a customer, who then will complete the order payment using their card.*

>Create and build the order, then select a card payment method with the `.usePaymentMethod()`, but instead of getting a form with `.getPaymentForm()`, use `.getPaymentUrl()` to get an url to present to the user.

*I wish to set up a subscription using recurring card payments, which will renew each month without further end user interaction.*

>For recurring payments, first create an order and select a card payment method with `.usePaymentMethod()`. You then use the `.setSubscriptionType()` method on the resulting payment request object. When the end user completes the transaction, you will receive a subscription id in the response.

>For the subsequent recurring payments, you again build an order and select the card payment method with `.usePaymentMethod()`. Then use `.setSubscriptionId()` with the subscription id from the initial request and send the payment request using the `.doRecur()` method. You will probably want to set up some sort of periodic cron job for i.e. subscription payments.

>See also section 4.3.3.

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 4. Payment method reference <a name="i4"></a>
First, make sure that you have added all required information to your order builder instance, see WebPay.createOrder below:

```java
...
	CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .addFee()         			   		// optional, see WebPayItem for invoice, shipping fee
	    .addDiscount()          			// optional, see WebPayItem for fixed, relative discount
	    .addCustomerDetails()    			// required for invoice and payment plan payments, card getPaymentUrl. See WebPayItem for individual, company customer
	    .setCountryCode()               	// required
	    .setOrderDate()            			// required for invoice and payment plan payments
	    .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
	    .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
	    .setCustomerReference()    			// optional, ignored for card & direct bank orders, max length 30 chars.
	;
...
```

Select payment method to use with the CreateOrderBuilder class useXX() methods, which return an instance of the appropriate payment request class.

### 4.1 Svea Invoice payment <a name="i41"></a>
To perform an invoice payment, select .useInvoicePayment(). This will return an instance of the InvoicePayment request class. Then send the request using .doRequest(). You will receive an instance of the CreateOrderResponse response class in return.

```java
...
	CreateOrderBuilder myOrder = WebPay.createOrder(myConfig)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .addCustomerDetails()    			// required for invoice and payment plan payments, see WebPayItem for individual, company customer
	    .setCountryCode()               	// required
	    .setOrderDate()            			// required for invoice and payment plan payments
	;		
	InvoicePayment myInvoiceOrderRequest = myOrder.useInvoicePayment();
	CreateOrderResponse myResponse = myInvoiceOrderRequest.doRequest();
...

#### 4.1.1 Runnable invoice order example
A complete, runnable example of an invoice order can be found in the examples/invoiceorder folder.


### 4.2 Svea Payment plan payment <a name="i42"></a>
To perform an invoice payment, select .usePaymentPlanPayment(). This will return an instance of the PaymentPlanPayment request class. Then send the request using .doRequest(). You will receive an instance of the CreateOrderResponse response class in return.

The Payment plan payment method is restricted to individual customers and can not be used by legal entities, i.e. companies or organisations.

First use WebPay.getPaymentPlanParams() to get the various campaigns in an instance of GetPaymentPlanParams. Then chose a campaign to pass as parameter to the .usePaymentPlanPayment() method.
		
```java
...		
	// get payment plan params
    PaymentPlanParamsResponse myParams = WebPay.getPaymentPlanParams(myConfig)
			.setCountryCode()				// required
            .doRequest();

	// select campaign to use
    String myCampaign = myParams
    	.getCampaignCodes()					
    	.get(0)
    	.getCampaignCode();

	// create order
	CreateOrderBuilder myOrder = WebPay.createOrder(myConfig)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .addCustomerDetails()    			// required for invoice and payment plan payments, see WebPayItem for individual, company customer
	    .setCountryCode()               	// required
	    .setOrderDate()            			// required for invoice and payment plan payments
	;	

    // choose payment plan payment method, passing in the campaign and do request
    CreateOrderResponse response = myOrder
    	.usePaymentPlanPayment(myCampaign)
    	.doRequest()
	;
...
```		

#### 4.2.1 Example
Example to come later. Sorry.


### 4.3 Card payment <a name="i43"></a>
Select i.e. .usePaymentMethod(PAYMENTMETHOD.KORTCERT) to perform a card payment via the Certitrade card payment provider. 

You will then use .getPaymentForm() to fetch a prepared html request form, which you may then use to redirect the customer to Certitrade, where they can enter their credit card credentials and complete the payment.

You may also use the .getUrl() method to get an url which you may direct the customer to visit at a later time in order to complete the order at Certitrade.

#### 4.3.1 .getPaymentForm()
Get an  instance of PaymentForm containing the request XML data and the complete html form as a string, along with the form elements in an array.

```java
...		
	CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .setCountryCode()               	// required
	    .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
	    .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
	;
            
    // choose payment method
    PaymentMethodPayment request = (PaymentMethodPayment) order.usePaymentMethod(PAYMENTMETHOD.KORTCERT);	

	// then perform any additional asynchronous request settings needed and receive request information
	request.
		.setReturnUrl()						// required
		.setCallbackUrl()					// optional but recommended
		.setCancelUrl()						// optional, applies to paypage only
		.setPayPageLanguageCode()			// optional, defaults to english
	;

	// get the PaymentForm instance containing the request html form
    PaymentForm form = request.getPaymentForm();
        
...	
```

##### 4.3.1.1 Runnable card order example, using .getPaymentForm()
A complete, runnable example of an invoice order can be found in the examples/cardorder folder.

#### 4.3.2 .getPaymentUrl()
Get an url containing a link to a prepared payment. 

To get a payment url you also need to supply the customer ip address in the order builder customer identity.

```java
...		
	CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .addCustomerDetails()    			// required for invoice and payment plan payments, card getPaymentUrl. See WebPayItem for individual, company customer
	    .setCountryCode()               	// required
	    .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
	    .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
	;
            
    // choose payment method
    PaymentMethodPayment request = (PaymentMethodPayment) order.usePaymentMethod(PAYMENTMETHOD.KORTCERT);	

	// then perform any additional asynchronous request settings needed and receive request information
	request.
		.setReturnUrl()						// required
		.setCallbackUrl()					// optional but recommended
		.setCancelUrl()						// optional, applies to paypage only
		.setPayPageLanguageCode()			// optional, defaults to english
	;
	
	// get the PaymentUrl instance containing the url to the prepared payment request
    PaymentUrl url = request.getPaymentUrl();        
...	
```

#### 4.3.3 Recurring card payments
Recurring card payments are set up in two steps. First a card payment including the subscription request, where the customer enters their credentials, and then any subsequent recur payment requests, where the subscription id is used in lieu of customer interaction.

For recurring payments, first create an order and select an available card payment method with .usePaymentMethod(). You then use the .setSubscriptionType() method on the resulting payment request object. When the end user completes the transaction, you will receive a subscription id with the request response.

For the subsequent recurring payment requests, you build an order and again select the card payment method with .usePaymentMethod(). You then use setSubscriptionId(), passing in the subscription id from the initial request response. Then send the payment request using the .doRecur() method. The payment request will then be processed, without any end user involvement.

##### 4.3.3.1 Recurring card order example, with initial subscription setup and subsequent .doRecur() request
An example of an recurring card order, both the setup transaction and a recurring payment, can be found in the example/cardorder_recur folder.

### 4.4 Direct bank payment <a name="i44"></a>
Select i.e. .usePaymentMethod(PAYMENTMETHOD.NORDEA_SE) to perform a direct bank transfer payment using the Swedish bank Nordea.

```java
...		
	CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
	 	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
	    .setCountryCode()               	// required
	    .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
	    .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
	;
            
    // choose payment method
    PaymentMethodPayment request = (PaymentMethodPayment) order.usePaymentMethod(PAYMENTMETHOD.NORDEA_SE);	

	// then perform any additional asynchronous request settings needed and receive request information
	request.
		.setReturnUrl()						// required
		.setCallbackUrl()					// optional but recommended
	;

	// get the PaymentForm instance containing the request html form
    PaymentForm form = request.getPaymentForm();        
...	
```

### 4.5 Using the Svea PayPage <a name="i45"></a>
The Svea PayPage presents all or a subset of a merchant's configured payment methods to the customer to select from when performing a payment.

#### 4.5.1 Specifying a payment method to use
It is possible to send the customer directly to a specified payment method, bypassing the PayPage completely. By specifying a specific payment method you may then eliminate one step in the payment process.

Note that you can use WebPay.listPaymentMethods() entrypoint to get the various payment methods available.

```java
...
	HostedPayment<?> request = order
    	.usePaymentMethod(PaymentMethod.KORTCERT)		// Use WebPay.listPaymentMethods() to get available payment methods, here we use Certitrade
		...												// any additional request options as dictated by the chosen payment method type
	;		
	PaymentForm form = request.getPaymentForm();
...	
```

#### 4.5.2 Select a card payment method
Send user to PayPage to select from available card payment methods (only), after which they'll be redirected and perform a card payment.

```java
...
	CardPayment request = order
    	.usePayPageCardOnly()							
		...												// any additional request options as dictated by the chosen payment method type
	;		
	PaymentForm form = request.getPaymentForm();
...	
```

#### 4.5.3 Select a direct bank payment method
Send user to PayPage to select from available bank payment methods (only), after which they'll be redirected and perform a direct bank payment.

```java
...
	DirectPayment request = order
    	.usePayPageDirectBankOnly()						 
		...												// any additional request options as dictated by the chosen payment method type
	;		
	PaymentForm form = request.getPaymentForm();
...	
```

#### 4.5.4 Specifying available payment methods to present
Send user to PayPage to select from the available payment methods, specifying which payment methods to present to and/or hide. Use the PayPagePayment class methods to customise which payment methods to display. 

```java
...
	PayPagePayment  request = order
    	.usePayPage()						 					
			.excludeCardPaymentMethods()				// see class PayPagePayment for method details
			.excludeDirectPaymentMethods()
			.excludePaymentMethods()
			.includePaymentMethods()
	;		
	PaymentForm form = request.getPaymentForm();
...	
```

Use the WebPay.listPaymentMethods() entrypoint method to find out which payment methods are configured for your specific merchant id.

All defined payment methods are listed in the PAYMENTMETHOD enum.

### 4.6 Examples <a name="i46"></a>

#### 4.6.1 Svea invoice order
An example of a synchronous (invoice) order can be found in the example/invoiceorder folder.

#### 4.6.2 Card order
An example of an asynchronous card order can be found in the example/cardorder folder.

#### 4.6.3 Recurring card order
An example of an recurring card order, both the setup transaction and a recurring payment, can be found in the example/cardorder_recur folder.

## 5. WebPayItem reference <a name="i5"></a>
<!-- WebPayItem class docbloc below, replace @see with apidoc links -->
The WebPayItem class provides entrypoint methods to the different row items that make up an order, as well as the customer identity information items.

An order must contain one or more order rows. You may add invoice fees, shipping fees and discounts to an order.

Note that while it is possible to add multiples of fee and discount rows, the package will group rows according to type before sending them to Svea:

 1. all order rows, in the order they were added using addOrderRow()
 2. any shipping fee rows, in the order they were added using addShippingFee()
 3. any invoice fee rows, in the order they were added using addShippingFee()
 4. any fixed discount rows, in the order they were added using addFixedDiscount()
 5. any relative discount rows, in the order they were added using addRelativeDiscount()
 
Also, for relative discounts, or fixed discounts specified using only setAmountIncVat() or only setAmountExVat() there may be several discount rows added, should the order include more than one different vat rate. It is not recommended to specify more than one relative discount row per order, or  more than one fixed discount specified using only setAmountIncVat() or only setAmountExVat().


See the class WebPayItem class for available order row items.

### 5.1 Specifying item price <a name="i51"></a>
Specify item price using precisely two of these methods in order to specify the item price and tax rate: `setAmountIncVat()`, `setVatPercent()` and `setAmountExVat()`.

The recommended way to specify an item price is by using the `setAmountIncVat()` and `setVatPercent()` methods. This will ensure that the total order amount and vat sums precisely match the amount and vat specified in the order items.

When using `setAmountExVat()` and `setVatPercent()`, the service will work with less accuracy and may accumulate rounding errors, resulting in order totals differing from total of the amount and vat specified in the row items. It is not recommended to specify price using the setAmountExVat() method.

When using `setAmountIncVat() with `setAmountExVat()` to specify an item price, the package converts the price to amount including vat and vat percent, in an effort to maintain maximum accuracy.

### 5.2 WebPayItem.orderRow() <a name="i52"></a>
<!-- WebPayItem.orderRow() docbloc below, replace @see with apidoc links -->
The WebPayItem.orderRow() entrypoint method is used to specify order items like products and services. It is required to have a minimum of one order row in an order.

Specify the item price using precisely two of these methods in order to specify the item price and tax rate: setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().

```java
...
	OrderRowBuilder orderrow = WebPayItem.orderRow()
	    .setAmountIncVat()		// Double	// optional, recommended, use precisely two of the price specification methods
	    .setVatPercent()       	// Double	// optional, recommended, use precisely two of the price specification methods
	    .setAmountExVat()       // Double	// optional, use precisely two of the price specification methods
	    .setQuantity()          // Double   // required
	    .setUnit()              // String	// optional
	    .setName()              // String	// optional, note that invoice & payment plan orders will merge "name" with "description" 
	    .setDescription() 		// String	// optional, note that invoice & payment plan orders will merge "name" with "description" 
	    .setArticleNumber()     // String	// optional
	    .setDiscountPercent()   // double 	// optional
	);
...
```
See the OrderRowBuilder class methods for details on how to specify the item.

### 5.3 WebPayItem.shippingFee() <a name="i53"></a>
<!-- WebPayItem.shippingFee() docbloc below, replace @see with apidoc links -->
The WebPayItem.shippingFee() entrypoint method is used to specify order shipping fee rows. It is not required to have a shipping fee row in an order.

Specify the item price using precisely two of these methods in order to specify the item price and tax rate: setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().

```java
...
     ShippingFeeBuilder shippingFee = WebPayItem.shippingFee()
         ->setAmountIncVat()	// Double 	// optional, recommended, use precisely two of the price specification methods
         ->setVatPercent()      // Double	// optional, recommended, use precisely two of the price specification methods
         ->setAmountExVat()     // Double 	// optional, use precisely two of the price specification methods
         ->setUnit()            // String	// optional
         ->setName()            // String	// optional
         ->setDescription() 	// String	// optional
         ->setShippingId()      // String   // optional
         ->setDiscountPercent() // double 	// optional
     );
...
```
See the ShippingFeeBuilder class methods for details on how to specify the item.

### 5.4 WebPayItem.invoiceFee() <a name="i54"></a>
<!-- WebPayItem.invoiceFee() docbloc below, replace @see with apidoc links -->
The WebPayItem.invoiceFee() entrypoint method is used to specify fees associated with a payment method (i.e. invoice fee). It is not required to have an invoice fee row in an order.

Specify the item price using precisely two of these methods in order to specify the item price and tax rate: setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().

```java
...
    InvoiceFeeBuilder invoiceFee = WebPayItem.invoiceFee()
        ->setAmountIncVat()		// Double 	// optional, recommended, use precisely two of the price specification methods
        ->setVatPercent()       // Double	// optional, recommended, use precisely two of the price specification methods
        ->setAmountExVat()      // Double 	// optional, use precisely two of the price specification methods
        ->setUnit()             // String	// optional
        ->setName()             // String	// optional
        ->setDescription() 		// String	// optional
        ->setDiscountPercent()  // double 	// optional
    );
...
```	
See the InvoiceFeeBuilder class methods for details on how to specify the item.

### 5.5 WebPayItem.fixedDiscount() <a name="i55"></a>
<!-- WebPayItem.fixedDiscount() docbloc below, replace @see with apidoc links -->
Use WebPayItem.fixedDiscount() when the discount or coupon is expressed as a fixed discount amount.

If no vat rate is given, the package will distribute the the discount amount across the different order row vat rates present in the order. This will ensure that the correct discount vat is applied to the order -- if there are several vat rates present in the order, the discount will be split proportionally across the order row vat rates. 

See the tests for examples of the resulting discount rows and exact behaviour when the discount is specified using setAmountIncVat() and setAmountExVat in orders with different vat rates present.

Specify the discount using setAmountIncVat(), setVatPercent() and setAmountExVat(). If two of these three attributes are specified, we honour the amount indicated and the given discount tax rate; if so we recommend using setAmountIncVat() and setVatPercentage().

```java
...
     FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
         .setAmountExVat()   	// Double	// recommended, see info above
         .setAmountIncVat()     // Double	// optional, see info above
         .setVatPercent)        // Double	// optional, see info above
         .setUnit()             // String	// optional
         .setName()             // String	// optional
         .setDescription()      // String	// optional
         .setDiscountId()       // String	// optional
     );
...
```
See the FixedDiscountBuilder class methods for details on how to specify the item.

### 5.6 WebPayItem.relativeDiscount() <a name="i56"></a>
<!-- WebPayItem.relativeDiscount() docbloc below, replace @see with apidoc links -->
Use WebPayItem.relativeDiscount() when the discount or coupon is expressed as a percentage of the total product amount.

The discount will be calculated based on the total sum of all order rows specified using .addOrderRow(), it does not apply to invoice or shipping fees.

The package will distribute the the discount amount across the different order row vat rates present in the order. This will ensure that the correct discount vat is applied to the order -- if there are several vat rates present in the order, the discount will be split across the order row vat rates. 

Specify the discount using RelativeDiscountBuilder methods:

```java
...
     RelativeDiscountBuilder relativeDiscount = WebPayItem.relativeDiscount()
         .setDiscountPercent()  // double	// recommended, see info above
         .setAmountExVat()		// Double	// optional, see info above
         .setUnit()             // String	// optional
         .setName()             // String	// optional
         .setDescription()      // String	// optional
         .setDiscountId()       // String	// optional
     );
...
```
See the RelativeDiscountBuilder class methods for details on how to specify the item.

### 5.7 WebPayItem.individualCustomer() <a name="i57"></a>
<!-- WebPayItem.individualCustomer() docbloc below, replace @see with apidoc links -->
Use WebPayItem.individualCustomer() to add individual customer information to an order.

#### 5.7.1 Using IndividualCustomer when specifying an order 
Note that "required" below as a requirement only when using the invoice or payment plan payment methods, and that the required attributes vary between countries.
For card and direct bank orders, adding customer information to the order is optional, unless you're using getPaymentUrl() to set up a prepared payment.

```java
...
	IndividualCustomer individual = WebPayItem.individualCustomer()
		.setNationalIdNumber()	// String	// invoice, paymentplan: required for individual customers in SE, NO, DK, FI
		.setName()       		// String	// invoice, paymentplan: required (firstname, lastname) for individual customers in NL and DE 
		.setBirthDate()        	// String	// invoice, paymentplan: required for individual customers in NL and DE, use date format yyyymmdd
		.setInitials()         	// String	// invoice, paymentplan: required for individual customers in NL
		.setCoAddress()      	// String	// invoice, paymentplan: optional
		.setStreetAddress()     // String	// invoice, paymentplan: required (street, housenumber) for individual customers in NL and DE 
		.setZipCode)            // String	// invoice, paymentplan: required in NL and DE
		.setLocality()          // String	// invoice, paymentplan: required in NL and DE
		.setPhoneNumber()       // String	// invoice, paymentplan: optional but desirable
		.setEmail()         	// String	// invoice, paymentplan: optional but desirable
		.setIpAddress()       	// String	// invoice, paymentplan: optional but desirable; card: required for getPaymentUrl() orders only		
	;
...
```

See the IndividualCustomer class methods for details on how to specify the item.

#### 5.7.2 A note on IndividualCustomer objects in service request response classes
Various service requests such as the WebPay `.createOrder()` and `.getAddresses()` methods along with the WebPayAdmin `.queryOrder()` et al methods may return an IndividualCustomer object as (part) of the request response. 

Note that not all responses define the same attributes. Also, what attributes are returned may vary between different countries and payment methods. In general, you should inspect the received response object attributes before relying on them for further requests.

If defined, the customer `.getName()` method will contain the amalgated customer firstname and surname as returned by the various credit providers we use in the respective country. Unfortunately, there is no way of knowing the exact format of the amalgated name; i.e. "Joan Doe", "Joan, Doe", "Doe, Joan". 

If defined, the customer firstname and surname are provided by the methods `.getFirstName()` and `.getLastName()`; if not, these methods return null.


### 5.8 WebPayItem.companyCustomer() <a name="i58"></a>
<!-- WebPayItem.companyCustomer() docbloc below, replace @see with apidoc links -->
Use WebPayItem.companyCustomer() to add customer information to an order.

#### 5.8.1 Using CompanyCustomer when specifying an order 
Note that "required" below as a requirement only when using the invoice payment methods, and that the required attributes vary between countries.
(For card and direct bank orders, adding customer information to the order is optional, unless you're using getPaymentUrl() to set up a prepared payment.)

```java
...
	CompanyCustomer company = WebPayItem.companyCustomer()
	    .setNationalIdNumber()  // String	// invoice: required in SE, NO, DK, FI
	    .setCompanyName()  		// String	// invoice: required (companyname) for company customers in NL and DE
	    .setVatNumber()         // String	// invoice: required in NL and DE
	    .setStreetAddress()     // String	// invoice: required in NL and DE
	    .setCoAddress()      	// String	// invoice: optional
	    .setZipCode()           // String	// invoice: required in NL and DE
	    .setLocality()          // String	// invoice: required in NL and DE
	    .setPhoneNumber()       // String	// invoice: optional but desirable
	    .setEmail()         	// String	// invoice: optional but desirable
	    .setIpAddress()       	// String	// invoice: optional but desirable; card: required for getPaymentUrl() orders only
	    .setAddressSelector()   // String	// invoice: optional but recommended; recieved from WebPay.getAddresses() request response
	;
...
```
#### 5.8.2 A note on CompanyCustomer objects in service request response classes
Various service requests such as the WebPay .createOrder() and .getAddresses() methods along with the WebPayAdmin .queryOrder() et al methods may return a CompanyCustomer object as (part) of the request response. 

Note that not all responses define the same attributes. Also, what attributes are returned may vary between different countries and payment methods. In general, you should inspect the received response object attributes before relying on them for further requests.

If defined, the customer `.getName()` method will contain the amalgated customer firstname and surname as returned by the various credit providers we use in the respective country. Unfortunately, there is no way of knowing the exact format of the amalgated name; i.e. "Joan Doe", "Joan, Doe", "Doe, Joan". 

If defined, the customer firstname and surname are provided by the methods `.getFirstName()` and `.getLastName()`; if not, these methods return null.

### 5.9 WebPayItem.numberedOrderRow() <a name="i59"></a>
NumberedOrderRow extends the orderRow class, providing fields used by when i.e. administrating an invoice or payment plan order.
It is returned in the various WebPayAdmin.queryOrder() responses, and used as input data in to methods that adminster individual order rows.

#### 5.9.1 Usage
```java
...
	NumberedOrderRow row = WebPayItem:.numberedOrderRow()
	    // inherited from OrderRow
	   .setAmountIncVat()		// Double
	   .setVatPercent()       	// Double
	   .setAmountExVat()       	// Double
	   .setQuantity()          	// Double
	   .setUnit()              	// String
	   .setName()              	// String
	   .setDescription() 		// String
	   .setArticleNumber()    	// String
	   .setDiscountPercent()   	// double
	    // numberedOrderRow
	   .setCreditInvoiceId()   	// Long
	   .setInvoiceId()         	// Long
	   .setRowNumber()      	// Integer	 
	   .setStatus() 			// ORDERROWSTATUS
	;
...
```

See the NumberedOrderRowclass methods for details.

## 6. WebPay entrypoint method reference <a name="i6"></a>
The WebPay class methods contains the functions needed to create orders and perform payment requests using Svea payment methods. It contains entrypoint methods used to define order contents, send order requests, as well as various support methods needed to do this.

* [6.1 WebPay.createOrder()](http://sveawebpay.github.io/java-integration#i61)
* [6.2 WebPay.deliverOrder()](http://sveawebpay.github.io/java-integration#i62)
* [6.3 WebPay.getAddresses()](http://sveawebpay.github.io/java-integration#i63)
* [6.4 WebPay.getPaymentPlanParams()](http://sveawebpay.github.io/java-integration#i64)

### 6.1 WebPay.createOrder() <a name="i61"></a>
<!-- WebPay.createOrder() docblock below, replace @see with apidoc links -->
Use WebPay.createOrder() to create an order using invoice, payment plan, card, or direct bank payment methods. You may also send the customer to the PayPage, where they may select from all available payment methods.

See the CreateOrderBuilder class for more info on methods used to specify the order builder contents, including order rows items et al, and then specifying which payment method to use, followed by sending the request to Svea
using doRequest, and parsing the response received from Svea.

Invoice and payment plan orders will perform a synchronous payment request, and will return a response object immediately following the doRequest call.

Hosted payment methods like Card, Direct Bank and any payment methods accessed via the PayPage, are asynchronous. Having selecting an asynchronous payment method you generally use a request class method to get a payment form 
object in return. The form is then posted to Svea, where the customer is redirected to the card payment provider service or bank. After the customer completes the payment, a response is sent back to your provided return url, 
where it can be processed and inspected.

Card, Direct Bank, and other hosted methods accessed via PayPage are asynchronous. Asynchronous payment methods provide an html form containing a formatted message to send to Svea, which in turn will send a request response 
to the specified return url, where the response can be parsed using the SveaResponse class. You should also be prepared to receive the request response on the specified alternative callback url which is used, amongst others,
if i.e. the customer does not return to the store after the order payment have been completed.

To create an invoice or partpayment order using useInvoicePayment or usePaymentPlanPayment, you do not need to explicitly specify which payment methods are available. 

When creating a card or direct bank order, you can minimize the number of steps in the checkout process by explicitly specifying i.e. usePaymentMethod(PAYMENTMETHOD.KORTCERT) instead of going through useCardPayment.

Get an order builder instance using the WebPay.deliverOrder entrypoint, then provide more information about the transaction using DeliverOrderBuilder methods: 

When redirecting the customer to the PayPage, you can use methods in PayPagePayment, i.e. excludePaymentMethods, to first specify which available payment methods to show or exclude, followed by the doRequest call.

```java
...
	CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
		.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
		.addFee()         			   		// optional, see WebPayItem for invoice, shipping fee
		.addDiscount()          			// optional, see WebPayItem for fixed, relative discount
		.addCustomerDetails()    			// required for invoice and payment plan payments, see WebPayItem for individual, company customer
		.setCountryCode()               	// required
		.setOrderDate()            			// required for invoice and payment plan payments
		.setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
		.setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
		.setCustomerReference()    			// optional, ignored for card & direct bank orders, max length 30 chars.
    ;
     
     // then select a synchronous payment method (invoice, part payment) request class and send request
     response = orderbuilder.useInvoicePayment().doRequest();    	// returns CreateOrderResponse
     response = orderbuilder.usePaymentPlanPayment().doRequest();	// returns CreateOrderResponse
     
     // or select an asynchronous payment method (card, direct bank et al.) request class
     request = orderbuilder
     	.usePaymentMethod(PAYMENTMETHOD.KORTCERT)	// returns HostedPayment<?>
		.usePayPage()								// returns PayPagePayment
     	.usePayPageCardOnly()						// returns CardPayment
     	.usePayPageDirectBankOnly()					// returns DirectPayment
     ;
     // then perform any additional asynchronous request settings needed and receive request information
     request.
		.setReturnUrl()				// required
     	.setCallbackUrl()			// optional but recommended
     	.setCancelUrl()				// optional, paypage only
     	.setPayPageLanguageCode()	// optional, defaults to english
     	.setSubscriptionType()		// optional, card only, used to setup recurring payments
     	.setSubscriptionId()		// required for card doRecur request
     ;
     form = request.getPaymentForm();	// returns PaymentForm object containing request html form
     url = request.getPaymentUrl();		// returns PaymentUrl object containing url to prepared payment request
     response = request.doRecur();		// performs synchronous request, returns RecurTransactionResponse
...
```

### 6.2 WebPay.deliverOrder() <a name="i62"></a>
<!-- WebPay.deliverOrder() docblock below, replace @see with apidoc links -->
Use the WebPay.deliverOrder() entrypoint when you deliver an order to the customer. Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported.)

The deliver order request should generally be sent to Svea once the ordered items have been sent out, or otherwise delivered, to the customer. 

For invoice and partpayment orders, the deliver order request triggers the invoice being sent out to the customer by Svea. (This assumes that your account
has auto-approval of invoices turned on, please contact Svea if unsure). 

For card orders, the deliver order request confirms the card transaction, which in turn allows nightly batch processing of the transaction by Svea. (Delivering card orders is only needed if your account has auto-confirm turned off, please contact Svea if unsure.)

To deliver an invoice, partpayment or card order in full, you do not need to specify order rows. To partially deliver an order, the recommended way is to
use WebPayAdmin.deliverOrderRows().

For more information on using deliverOrder to partially deliver and/or credit
an order, see 6.2.3 below.
 
Get an order builder instance using the WebPay.deliverOrder entrypoint, then provide more information about the transaction using DeliverOrderBuilder methods: 

```java
...
	 DeliverOrderBuilder request = WebPay.deliverOrder(config)
         .setOrderId()                  // invoice or payment plan only, required
         .setTransactionId()            // card only, optional, alias for setOrderId 
         .setCountryCode()              // required
         .setInvoiceDistributionType()  // invoice only, required
         .setNumberOfCreditDays()       // invoice only, optional
         .setCaptureDate()              // card only, optional
         .addOrderRow()                 // deprecated, optional -- use WebPayAdmin.deliverOrderRows instead
         .setCreditInvoice()            // deprecated, optional -- use WebPayAdmin.creditOrderRows instead
     ;
     // then select the corresponding request class and send request
     response = request.deliverInvoiceOrder().doRequest();       // returns DeliverOrderResponse
     response = request.deliverPaymentPlanOrder().doRequest();   // returns DeliverOrderResponse
     response = request.deliverCardOrder().doRequest();          // returns ConfirmTransactionResponse
...
```

#### 6.2.1 Usage
<!-- DeliverOrderBuilder docblock below -->
DeliverOrderBuilder collects and prepares order data for use in a deliver order request to Svea.

Use setOrderId() to specify the Svea order id, this is the order id returned with the original create order request response. For card orders, you can optionally use setTransactionId() instead.

Use setCountryCode() to specify the country code matching the original create order request.

Use setInvoiceDistributionType() with the DistributionType matching how your account is configured to send out invoices. (Please contact Svea if unsure.)

Use setNumberOfCreditDays() to specify the number of credit days for an invoice.

(Deprecated -- to partially deliver an invoice order, you can specify order rows to deliver using the addOrderRows() method. Use the WebPayAdmin.deliverOrderRows entrypoint instead.)

(Deprecated -- to issue a credit invoice, you can specify credit order rows to deliver using setCreditInvoice() and addOrderRows(). Use the WebPayAdmin.creditOrderRow entrypoint instead.)

To deliver an invoice, partpayment or card order in full, use the WebPay.deliverOrder entrypoint without specifying order rows.

When specifying orderrows, WebPay.deliverOrder is used in a similar way to WebPay.createOrder and makes use of the same order item information. Add order rows that you want delivered and send the request, specified rows will automatically be matched to the rows sent when creating the order.

We recommend storing the createOrder orderRow objects to ensure that deliverOrder order rows match. If an order row that was present in the createOrder request is not present in from the deliverOrder request, the order will be partially delivered, and any left out items will not be invoiced by Svea. You cannot partially deliver payment plan orders, where all un-cancelled order rows will be delivered.

#### 6.2.2 Example
Example to come later. Sorry.

#### 6.2.3 On using WebPay.deliverOrder with order rows
WebPay.deliverOrder may be used to partially deliver, amend or credit an order, by specifying order rows using the DeliverOrderBuilder addOrderRow() method. We recommend using WebPayAdmin.deliverOrderRows to partially deliver an order and WebPayAdmin.creditOrderRows to credit an order.

##### 6.2.3.1 Partial delivery using WebPay.deliverOrder
When using WebPay.deliverOrder to partially deliver an order, care must be taken that the order rows to deliver precisely match the order row specification used in the original WebPay.createOrder request. Unless all order rows in the deliverOrder request exactly match rows in the original createOrder request, unmatched order rows in the original order will be cancelled. See also 6.2.3.2 below.

If on the other hand all deliver order rows match with original order rows, then the original order rows matched by the deliver order rows will be invoiced, with the invoice id being returned in the DeliverOrderResponse. The remaining original order rows will remain undelivered and may be delivered in a subsequent deliverOrder request.

```
Example:
1. cResponse = WebPay.createOrder().addOrderRows(A).addOrderRows(B).addOrderRows(C) ... .doRequest();
2. dResponse = WebPay.deliverOrder().addOrderRows(A) ... .doRequest(); // A matches A
Will result in the order having status
A: delivered	// found on invoice # dResponse.getInvoiceId()
B: undelivered  // may be delivered later
C: undelivered  // may be delivered later
```

##### 6.2.3.2 Amending an order using WebPay.deliverOrder
If you wish to add an order row to an existing order, any original order rows still undelivered will be cancelled to make room for the added order rows within the original order total amount (you may deliver order rows in the same request by adding order rows that exactly match the original order rows).

The exact behaviour is that if there are order rows in the deliver order request that does not match any undelivered original order row, all unmatched and undelivered original order rows are cancelled, and the unmatched deliver order rows are added to the original order as new delivered order rows, given that as the total of all existing delivered rows and the newly added order rows does not exceed the total original order row total amount. This means that the sum of the unmatched (i.e. added) deliver order rows cannot exceed the sum of the cancelled original order rows.

When there are delivered order rows to an amount equal to the original order total amount the order will be closed, preventing further modification. Delivered order rows can only be credited, see also 6.2.3.3 below.

```
Example (cont. from 6.2.3.1):
3. dResponse2 = WebPay.deliverOrder().addOrderRows(D) ... .doRequest(); // D does not match any rows
Will result in the order having status
A: delivered	// found on invoice1; dResponse.getInvoiceId()
B: cancelled
C: cancelled
D: delivered	// found on invoice2; dResponse2.getInvoiceId()
```

##### 6.2.3.3 Crediting a (partially) delivered order using WebPay.deliverOrder
To credit an order use the setCreditInvoice(invoiceId) method when delivering an order. Add an order row made out to the amount to be credited to the deliver order request. A credit invoice with the order rows specified will be issued to the customer.

When crediting a delivered order, you are really crediting an invoice. This means that if you i.e. partially delivered an order, and then need to credit the entire order, you will need to make several credit requests, as a credit invoice amount can't exceed the individual invoice total amount.

The invoice id received will point to the new credit invoice itself, and the original invoice will be be credited at Svea by the specified amount. Note that the original order row status will not change, the as the request operates on the invoice, not the order in itself.

```
Example (cont. from 6.2.3.2):
4. dResponse3 = WebPay.deliverOrder().addOrderRows(E).setCreditInvoice(invoice1) ... .doRequest();
//To credit i.e. 50% of the price for order row A we created a new order row E with half the price of A.
//The credit invoice id is returned in dResponse3.getInvoiceId()
```

### 6.3 WebPay.getAddresses() <a name="i63"></a>
<!-- WebPay.getAddresses() docblock below, replace @see with apidoc links -->
Use the WebPay.getAddresses() entrypoint to fetch a list of addresses associated with a given customer identity. Company addresses additionally has an AddressSelector attribute that uniquely identifies the address. Only applicable 
for SE, NO and DK invoice and part payment orders. Note that in Norway, company customers only are supported.

Get an instance using the WebPayAdmin.getAddresses entrypoint, then provide more information about the customer and send the request using the GetAddresses methods:

Use setCountryCode() to supply the country code that corresponds to the account credentials used for the address lookup. Note that this means that you cannot look up a user in a foreign country, this is a consequence of the fact 
that the invoice and partpayment payment methods don't support foreign orders.

Use setCustomerIdentifier() to provide the exact credentials needed to identify the customer according to country:

	* SE: Personnummer (private individual) or Organisationsnummer (company or other legal entity)
	* NO: Organisasjonsnummer (company or other legal entity)
	* DK: Cpr.nr (private individual) or CVR-nummer (company or other legal entity)

Then use either getIndividualAddresses() or getCompanyAddresses() to get an instance of the GetAddresses request.

The final doRequest() will then send the getAddresses request to Svea and return a GetAddressResponse. Then use methods getIndividualCustomers() or getCompanyCustomers() to get a list of IndividualCustomer or CompanyCustomer addresses.

#### 6.3.1 Request example
```java
	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
   		.setCountryCode()             		// required -- supply the country code that corresponds to the account credentials used
		.setCustomerIdentifier() 			// required -- social security number, company vat number etc. used to identify customer
    	.getIndividualAddresses()           // required -- lookup the address of a private individual customer
    	//.getCompanyCustomer()    			// required -- lookup the address of a company customer
	;

	// then select the corresponding request class and send request
	GetAddressesResponse response = request.doRequest();

	// get the list of customer addresses from the response using either getIndividualCustomers() or getCompanyCustomers()
	ArrayList<IndividualCustomer> addresses = response.getIndividualCustomers();
	ArrayList<CompanyCustomer> addresses = response.getCompanyCustomers(); 
```

#### 6.3.2 Deprecated methods
The following methods are deprecated starting with 1.6.1 of the package:

```java
	//GetAddresses
	.setIndividual()                    // deprecated -- lookup the address of a private individual, set to i.e. social security number)
	.setCompany()                       // deprecated -- lookup the addresses associated with a legal entity (i.e. company)
	.setOrderTypeInvoice()              // deprecated -- supply the method that corresponds to the account credentials used for the address lookup
	.setOrderTypePaymentPlan()          // deprecated -- supply the method that corresponds to the account credentials used for the address lookup
	
	//GetAddressResponse
	.getXXX()							// deprecated -- get value of customer attribute XXX for first associated customer address
```

(Note that if your integration is currently set to use different (test/production) credentials for invoice and payment plan you may need to use the 
deprecated methods setOrderTypeInvoice() or setOrderTypePaymentPlan() to explicity state which ConfigurationProvider credentials to be use in request.)

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 6.4 WebPay.getPaymentPlanParams() <a name="i64"></a>
<!-- WebPay.getPaymentPlanParams() docblock below, replace @see with apidoc links -->
Use getPaymentPlanParams() to fetch all campaigns associated with a given client number before creating the payment plan payment.

```java
...
	// set countrycode and supply client credentials
	GetPaymentPlanParams request = WebPay.getPaymentPlanParams(config)
		.setCountryCode()				// required
	;

	// receive response and get CampaignCodes from response
	PaymentPlanParamsResponse response = request->doRequest();
 	List<CampaignCode> campaignCodes = response.getCampaignCodes(); 
...
```

See also class CampaignCode for individual campaign attributes.

### 6.5 WebPay.paymentPlanPricePerMonth() <a name="i65"></a>
Deprecated, please refer to [section 9.1](http://sveawebpay.github.io/java-integration#i91) on method Helper.paymentPlanPricePerMonth().

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 7. WebPayAdmin entrypoint method reference <a name="i7"></a>
The WebPayAdmin class methods are used to administrate orders after they have been accepted by Svea. It includes functions to update, deliver, cancel and credit orders et.al.

* [7.1 WebPayAdmin.cancelOrder()](http://sveawebpay.github.io/java-integration#i71)
* [7.2 WebPayAdmin.queryOrder()](http://sveawebpay.github.io/java-integration#i72)
* [7.3 WebPayAdmin.cancelOrderRows()](http://sveawebpay.github.io/java-integration#i73)
* [7.4 WebPayAdmin.creditOrderRows()](http://sveawebpay.github.io/java-integration#i74)
* [7.5 WebPayAdmin.addOrderRows()](http://sveawebpay.github.io/java-integration#i75)
* [7.6 WebPayAdmin.updateOrderRows()](http://sveawebpay.github.io/java-integration#i76)
* [7.7 WebPayAdmin.deliverOrderRows()](http://sveawebpay.github.io/java-integration#i77)

### 7.1 WebPayAdmin.cancelOrder() <a name="i71"></a>
<!-- WebPayAdmin.cancelOrder() docblock below, replace @see with apidoc links -->
The WebPayAdmin.cancelOrder() entrypoint method is used to cancel an order with Svea, that has not yet been delivered (invoice, payment plan) or confirmed (card).

Supports Invoice, Payment Plan and Card orders. For Direct Bank orders, use WebPayAdmin.creditOrderRows() instead.

Get an instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send the request using the CancelOrderBuilder methods:

```java
...
    request = WebPayAdmin.cancelOrder(config)
         .setOrderId()		// required, use SveaOrderId recieved with createOrder response
         .setTransactionId	// optional, card or direct bank only, alias for setOrderId 
         .setCountryCode()	// required, use same country code as in createOrder request      
    ;
    // then select the corresponding request class and send request
    response = request.cancelInvoiceOrder().doRequest();		// returns CloseOrderResponse
    response = request.cancelPaymentPlanOrder().doRequest();	// returns CloseOrderResponse
    response = request.cancelCardOrder().doRequest();			// returns AnnulTransactionResponse
...
```

See class CancelOrderBuilder.
See class CloseOrderResult.
See class AnnulTransactionResponse.

### 7.2 WebPayAdmin.queryOrder() <a name="i72"></a>
<!-- WebPayAdmin.queryOrder() docblock below, replace @see with apidoc links -->
The WebPayAdmin.queryOrder entrypoint method is used to get information about an order.

Note that for invoice and payment plan orders, the order rows name and description is merged into the description field in the query response.

Get an instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send the request using the QueryOrderBuilder methods:

```java
...
    request = WebPayAdmin.queryOrder(config)
         .setOrderId()          	// required
         .setTransactionId()	   	// optional, card or direct bank only, alias for setOrderId 
         .setCountryCode()      	// required      
    ;
    // then select the corresponding request class and send request
    response = request.queryInvoiceOrder().doRequest();			// returns GetOrdersResponse
    response = request.queryPaymentPlanOrder().doRequest(); 	// returns GetOrdersResponse
    response = request.queryCardOrder().doRequest();        	// returns QueryTransactionResponse
    response = request.queryDirectBankOrder().doRequest();  	// returns QueryTransactionResponse
...
```

See class QueryOrderBuilder.
See class GetOrdersResponse.
See class QueryTransactionResponse.

#### 7.2.1 QueryOrder Example
Example to come later

### 7.3 WebPayAdmin.cancelOrderRows() <a name="i73"></a>
<!-- WebPayAdmin.cancelOrderRows() docblock below, replace @see with apidoc links -->
The WebPayAdmin.cancelOrderRows entrypoint method is used to cancel rows in an order before it has been delivered.
Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported, see CreditOrderRows instead.)

For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.

For card orders, the request can only be sent once, and if all original order rows are cancelled, the order then 
receives status ANNULLED at Svea.

Get an order builder instance using the WebPayAdmin.cancelOrderRows entrypoint, then provide more information about 
the transaction and send the request using the CancelOrderRowsBuilder methods:

Use setRowToCancel() or setRowsToCancel() to specify the order row(s) to cancel. The order row indexes should 
correspond to those returned by i.e. WebPayAdmin.queryOrder();

For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order rows. 
The original order rows can be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute contains 
the serverside order rows w/indexes. Note that if a card order has been modified (i.e. rows cancelled or credited) 
after the initial order creation, the returned order rows will not be accurate.

```java
...
		CancelOrderRowsBuilder request = WebPayAdmin.cancelOrderRows(config)
	        .setOrderId()          			// required
	        .setTransactionId()	   			// optional, card only, alias for setOrderId 
	        .setCountryCode()      			// required    	
	        .setRowToCancel()	   			// required, index of original order rows you wish to cancel 
	        .addNumberedOrderRow()			// required for card orders, should match original row indexes 
    	;
    	// then select the corresponding request class and send request
    	response = request.cancelInvoiceOrderRows().doRequest();		// returns CancelOrderRowsResponse
    	response = request.cancelPaymentPlanOrderRows().doRequest();	// returns CancelOrderRowsResponse
    	response = request.cancelCardOrderRows().doRequest()			// returns LowerTransactionResponse
...
```
    
See class CancelOrderRowsBuilder.
See class CancelOrderRowsResponse.
See class LowerTransactionResponse.

### 7.4 WebPayAdmin.creditOrderRows() <a name="i74"></a>
<!-- WebPayAdmin.creditOrderRows() docblock below, replace @see with apidoc links -->
The WebPayAdmin.creditOrderRows entrypoint method is used to credit rows in an order after it has been delivered.
Supports invoice, card and direct bank orders. (To credit a payment plan order, please contact Svea customer service.)

If you wish to credit an amount not present in the original order, use addCreditOrderRow() or addCreditOrderRows() 
and supply a new order row for the amount to credit. This is the recommended way to credit a card or direct bank order.

If you wish to credit an invoice order row in full, you can specify the index of the order row to credit using setRowToCredit(). 
The corresponding order row at Svea will then be credited. (For card or direct bank orders you need to first query and then 
supply the corresponding numbered order rows using the addNumberedOrderRows() method.)

Following the request Svea will issue a credit invoice including the original order rows specified using setRowToCredit(), 
as well as any new credit order rows specified using addCreditOrderRow(). For card or direct bank orders, the order row amount
will be credited to the customer. 

Note: when using addCreditOrderRows, you may only use WebPayItem.orderRow with price specified as amountExVat and vatPercent.

Get an order builder instance using the WebPayAdmin.creditOrderRows entrypoint, then provide more information about the 
transaction and send the request using the creditOrderRowsBuilder methods:     

```java
...
    CreditOrderRowsBuilder request = WebPay.creditOrderRows(config)
        .setInvoiceId()                // invoice only, required
        .setInvoiceDistributionType()  // invoice only, required
        .setOrderId()                  // card and direct bank only, required
        .setCountryCode()              // required
        .addCreditOrderRow()           // optional, use to specify a new credit row, i.e. for amounts not present in the original order
        .addCreditOrderRows()          // optional
        .setRowToCredit()              // optional, index of one of the original order row you wish to credit
        .setRowsToCredit()             // optional
        .addNumberedOrderRow()         // card and direct bank only, required with setRowToCredit()
        .addNumberedOrderRows()        // card and direct bank only, optional
    ;
    // then select the corresponding request class and send request
    response = request.creditInvoiceOrderRows().doRequest();    // returns CreditInvoiceRowsResponse
    response = request.creditCardOrderRows().doRequest();       // returns CreditTransactionResponse
    response = request.creditDirectBankOrderRows().doRequest(); // returns CreditTransactionResponse
...
```

See class CreditOrderRowsBuilder.
See class CreditInvoiceRowsResponse.
See class CreditTransactionResponse.

#### 7.4.1 Usage
<!-- CreditOrderRowsBuilder class docblock below -->
The WebPayAdmin.creditOrderRows entrypoint method is used to credit rows in an order after it has been delivered. Supports Invoice, Card and Direct Bank orders. (To credit a Payment Plan order, contact Svea customer service.)

To credit an order row in full, you specify the index of the order row to credit (and for card orders, supply the numbered order row data itself).

If you wish to credit an amount not present in the original order, you need to supply new order row(s) for the credited amount using addCreditOrderRow()
or addCreditOrderRows(). These rows will then be credited in addition to any rows specified using setRow(s)ToCredit below.

Use setInvoiceId() to specify the invoice (delivered order) to credit.

Use setOrderId() to specify the card or direct bank transaction (delivered order) to credit.

Use setCountryCode() to specify the country code matching the original create order request.

Use setRowToCredit() or setRowsToCredit() to specify order rows to credit. The given row numbers must correspond with the the serverside row number.

For card or direct bank orders, it is required to use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the serverside order row data.

You can use the WebPayAdmin.queryOrder() entrypoint to get information about the order, the queryOrder response numberedOrderRows attribute contains the order rows with numbers.
For invoice orders, the serverside order rows is updated after a creditOrderRows request. Note that for Card and Direct bank orders the serverside order rows will not be updated.

Then use either creditInvoiceOrderRows(), creditCardOrderRows() or creditDirectBankOrderRows() to get a request object, which ever matches the payment method used in the original order.

Calling doRequest() on the request object will send the request to Svea and return either a CreditOrderRowsResponse or a CreditTransactionResponse.

#### 7.4.2 Example
Example to come later.

### 7.5 WebPayAdmin.addOrderRows() <a name="i75"></a>
<!-- WebPayAdmin.addOrderRows() docblock below, replace @see with apidoc links -->

The WebPayAdmin.addOrderRows() method is used to add individual order rows to undelivered or partially delivered invoice and payment plan orders. Supports invoice and payment plan orders.

The order row status of the order will be updated at Svea to reflect the added order rows following a successful request. If the new order total amount exceeds the original order total amount, a new credit control is first 
made, which may result in the request being denied. For payment plan orders, the unew order total amount must be within the original order campaign limits, or the request will be denied.

Get an order builder instance using the WebPayAdmin.addOrderRows() entrypoint, then provide more information about the transaction and send the request using the AddOrderRowsBuilder methods:   

Use setCountryCode() to specify the country code matching the original create order request.

Use addUpdateOrderRow() with a new WebPayItem.orderRow() object, add the new order row attributes using the OrderRowBuilder member functions. Notably, the setRowNumber() method specifies which of the original order rows 
to update. That order row will be replaced in full by the new NumberedOrderRow. 

Use addOrderRow() with a new WebPayItem.orderRow() object. Add the new order row attributes using the OrderRowBuilder member functions.

Then use either addInvoiceOrderRows() or addPaymentPlanOrderRows() to get a request object, which ever matches the payment method used in the original order.

Calling doRequest() on the request object will send the request to Svea and return an AddOrderRowsResponse.

```java
...
     AddOrderRowsBuilder request = WebPayAdmin.addOrderRows(config)
         .setOrderId()              // required
         .setCountryCode()          // required
         .addOrderRow()           	// required, OrderRowBuilder containing the new order row data
     ;
     // then select the corresponding request class and send request
     response = request.addInvoiceOrderRows().doRequest();     // returns AddOrderRowsResponse
     response = request.addPaymentPlanOrderRows().doRequest(); // returns AddOrderRowsResponse
...
```

See class AddOrderRowsBuilder.
See class AddOrderRowsResponse.
See class NumberedOrderRows.

### 7.6 WebPayAdmin.updateOrderRows() <a name="i76"></a>
<!-- WebPayAdmin.updateOrderRows() docblock below, replace @see with apidoc links -->

The WebPayAdmin.updateOrderRows() method is used to update individual order rows in non-delivered invoice and payment plan orders. Supports invoice and payment plan orders.

The order row status of the order is updated at Svea to reflect the updated order rows. If the updated rows' order total amount exceeds the original order total amount, an error is returned by the service.

Get an order builder instance using the WebPayAdmin.updateOrderRows() entrypoint, then provide more information about the transaction and send the request using the UpdateOrderRowsBuilder methods:   

Use setCountryCode() to specify the country code matching the original create order request.

Use addUpdateOrderRow() with a new WebPayItem.numberedOrderRow() object. Add the updated order row attributes using the NumberedOrderRowBuilder member functions. Notably, the setRowNumber() method specifies which of the 
original order rows will be replaced, in full, with the new NumberedOrderRow. 

Then use either updateInvoiceOrderRows() or updatePaymentPlanOrderRows() to get a request object, which ever matches the payment method used in the original order.

Calling doRequest() on the request object will send the request to Svea and return an UpdateOrderRowsResponse.

```java
...
    UpdateOrderRowsBuilder request = WebPayAdmin.updateOrderRows(config)
        .setOrderId()                  // required
        .setCountryCode()              // required
        .addUpdateOrderRow()           // required, NumberedOrderRowBuilder w/RowNumber attribute matching row index of original order row
    ;
    // then select the corresponding request class and send request
    response = request.updateInvoiceOrderRows().doRequest();     // returns UpdateOrderRowsResponse
    response = request.updatePaymentPlanOrderRows().doRequest(); // returns UpdateOrderRowsResponse
...
```

See class UpdateOrderRowsBuilder.
See class UpdateOrderRowsResponse.
See class NumberedOrderRows.

### 7.7 WebPayAdmin.deliverOrderRows() <a name="i77"></a>
<!-- WebPayAdmin.deliverOrderRows() docblock below, replace @see with apidoc links -->
The WebPayAdmin.deliverOrderRows entrypoint method is used to deliver individual order rows. Supports invoice and card orders. (To partially deliver PaymentPlan or Direct Bank orders, please contact Svea.)

For Invoice orders, the order row status is updated at Svea following each successful request.

For card orders, an order can only be delivered once, and any non-delivered order rows will be cancelled (i.e. the order amount will be lowered by the sum of the non-delivered order rows). A delivered card order has status 
CONFIRMED at Svea.

Get an order builder instance using the WebPayAdmin.deliverOrderRows entrypoint, then provide more information about the transaction and send the request using
the DeliverOrderRowsBuilder methods:

Use setRowToDeliver() or setRowsToDeliver() to specify the order row(s) to deliver. The order row indexes should correspond to those returned by i.e. WebPayAdmin.queryOrder();

For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order rows. The original order rows can be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute 
contains the serverside order rows w/indexes. Note that if a card order has been modified (i.e. rows cancelled or credited) after the initial order creation, the returned order rows will not be accurate.

```java
...
	DeliverOrderRowsBuilder request = WebPayAdmin.deliverOrderRows(config)
         .setOrderId()          			// required
         .setTransactionId()	   			// optional, card only, alias for setOrderId 
         .setCountryCode()      			// required    	
         .setInvoiceDistributionType()		// required, invoice only
         .setRowToDeliver()	   				// required, index of original order rows you wish to deliver 
         .addNumberedOrderRow()				// required for card orders, should match original row indexes 
	;
	// then select the corresponding request class and send request
 	response = request.deliverInvoiceOrderRows().doRequest();	// returns DeliverOrderRowsResponse
	response = request.deliverCardOrderRows().doRequest()		// returns ConfirmTransactionResponse
...
```

See class DeliverOrderRowsBuilder for method details.

See class DeliverOrderRowsResponse for method details.

See class ConfirmTransactionResponse for method details.

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 8. HostedPaymentResponse and response classes <a name="i8"></a>
### 8.1. Parsing an asynchronous service response <a name="i81"></a>
All synchronous service request responses are immediately structured into response objects by the request method itself.

The asynchronous payment request responses (i.e. card and direct bank payments) need to be handled by listening for the service post back on the specified request return url. The response contains the parameters: *response*, *merchantid*, and *mac*, where the response is a Base64 encoded XML message. Feed the response message through an instance of the SveaResponse class to get a structured object similar to the synchronous service responses.

#### 8.1.1
First, create an instance of SveaResponse, passing it the resulting xml response string along with the a ConfigurationProvider and countryCode, then receive a HostedResponse instance by calling the getResponse() method, passing in the message sent to the return url, the request countrycode and configuration:

```java
...
	HttpServletRequest myUnparsedResponse = theReturnUrlListenerGotAPostBack();

	ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
	String mySecretWord = myConfig.getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE); // countrycode should correspond to the request country code

	HostedPaymentResponse parsedResponse = (HostedPaymentResponse) new SveaResponse(		// SveaResponse is an alias for HostedPaymentResponse
		myUnparsedResponse.getParameter("response"),	// the post "response", i.e. the base64 encoded response message
		mySecretWord									// the secret word for our merchant id 
  	;  
...
```

#### 8.1.2
An example of an asynchronous (card) order can be found under example/cardorder. See LandingPageServlet.java for details on how to handle the service post back.

### 8.2 Response accepted and result code <a name="i82"></a>
All integration package response objects implement the Response interface, providing methods that may be checked to determine the outcome of a request:

	* `.isOrderAccepted()`	-- Boolean, true iff the request was accepted by Svea
	* `.getResultCode()`    -- String, contains numerical value >0 iff there was a problem with the service request
	* `.getErrorMessage()`  -- String, contains a human readable version of the resultcode iff resultcode above is >0

See the respective response classes for further information on response attributes, possible resultcodes etc.

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 9. Additional Developer Resources and notes <a name="i9"></a>
### 9.1 Helper.paymentPlanPricePerMonth() <a name="i91"></a>
This is a helper function provided to calculate the monthly price for the different payment plan options for a given sum. This information may be used when displaying i.e. payment options to the customer by checkout, or to display the lowest amount due per month to display on a product level.

If the ignoreMaxAndMinFlag is set to true, the returned array also contains the theoretical monthly installments for a given amount, even if the campaign may not actually be available to use in a payment request, should the amount fall outside of the actual campaign min/max limit. If the flag is set to false or left out, the values array will not include such amounts, which may cause the values array to be empty.

```java
...
	PaymentPlanParamsResponse paymentPlanParams = 
    	WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .doRequest();
        
	List<Map<String, String>> response = Helper.paymentPlanPricePerMonth(200.0, paymentPlanParams, true);        

	String firstCampaign = response.get(0).get("campaignCode");							// i.e. [campaignCode] => 310012
	String firstCampaignDescription = response.get(0).get("description");			// i.e. [description] => "Dela upp betalningen pÂ 12 mÂnader (r‰ntefritt)"
	String pricePerMonthForFirstCampaign = response.get(0).get("campaignCode");	// i.e. [pricePerMonth] => 201.66666666667
...
```

### 9.2 Inspect prepareRequest(), validateOrder() methods <a name="i92"></a>
During module development or debugging, the `.prepareRequest()` method may be of use as an alternative to `.doRequest()` as the final step in the createOrder process. 

Generally, a call to `.prepareRequest()` will do everything that `.doRequest()` does, but it does not send the SOAP request to Svea, instead you may inspect the returned result for any problems.

Also, `.validateOrder()` validates that all required attributes are present in an order object, given the specific combination of country and payment method. It returns an array containing any discovered errors.

[<< To index](http://sveawebpay.github.io/java-integration#index)

## 10. Frequently Asked Questions <a name="i10"></a>
### 10.1 Supported currencies <a name="i101"></a>
**Q**: What currencies does each payment method support?

**A**:
*Invoice and part payment*
For invoice and direct bank payment methods, the assumed currency is tied to the merchant account (client id), where each account in turn is tied to a specific country. This is why you as the merchant need to specify a country code in the order, and must supply the amount in the corresponding currency (i.e. an invoice order with .setCountryCode(COUNTRYCODE.SE) is always assumed to be made out in SEK).

*Credit card and direct bank transfer*
For credit card orders, Svea accepts any currency when specifying the order, and pass the currency and amount on the card Acquirer (i.e. Certitrade) where the end user enters their account credentials.

The acquirer in turn asks (via the credit card company) the end user's Issuing bank (i.e. the bank that provides the end user their card) if the transaction is accepted. If so this information is passed on to the merchant via the Acquirer and Svea, and this is the end of the story as far as the end user is concerned.

The merchant, i.e. your web shop, then receives money from their Acquiring bank. This is usually done nightly, when the acquiring bank (via the credit card company) receives a list of confirmed card transactions for the merchant in question, and pays the merchant accordingly.

*Acquiring bank support*
The key point is that the merchant must have an agreement with their acquiring bank as to which currencies they accept. Svea has no way of knowing this, so it is up to the merchant to supply the correct currency in the original request.

*tl;dr*
For invoice and part payment, the order amount is assumed to be made out in the country currency. For credit card and direct bank transfer, we honour the specified currency and amount, but you should only specify currencies that you have agreed upon with your acquiring bank.

### 10.2 Other payment method credentials <a name="i102"></a>
**Q**: What credentials do I need to make use of i.e. PayPal as a payment method through the PayPage?

**A**: When you sign up with Svea you will be provided with a merchant id which is used for all hosted payment methods. For a merchant id, one or more payment methods may be enabled, such as credit cards, direct bank payments using various banks, PayPal etc. 

To enable a new payment method, your merchant will need to be configured with various credentials as requested by Svea. Please ask your Svea integration manager for more information on what exact credentials are needed.

## 11. Example servlets <a name="i11"></a>
The provided examples show how to use the Svea java integration package to specify an order and send a payment request to Svea.

### 11.1 Running the examples <a name="i111"></a>
We assume that you are running a local installation of Tomcat 7 or later on localhost port 8080. 
Build and deploy the examples to localhost using ant, see build.xml and edit build.properties with your tomcat installation path: 

```
$ pwd
/c/projects/java-integration/java

$ cd example/invoiceorder

$ echo "remember to edit build.properties with your tomcat installation path (i.e. where we should deploy the example .war file)"

$ ant deploy_invoiceorder
Buildfile: c:\projects\java-integration\java\example\invoiceorder\build.xml

clean:
     [echo] Cleaning the build

init:
     [echo] Creating the build directory
    [mkdir] Created dir: c:\projects\java-integration\java\example\invoiceorder\build\WEB-INF\classes
    [mkdir] Created dir: c:\projects\java-integration\java\example\invoiceorder\build\WEB-INF\lib
    [mkdir] Created dir: c:\projects\java-integration\java\example\invoiceorder\dist

compile:
     [echo] Compile the source files
    [javac] Compiling 1 source file to c:\projects\java-integration\java\example\invoiceorder\build\WEB-INF\classes

copy:
     [copy] Copying 1 file to c:\projects\java-integration\java\example\invoiceorder\build\WEB-INF
     [copy] Copying 2 files to c:\projects\java-integration\java\example\invoiceorder\build
     [copy] Copying 1 file to c:\projects\java-integration\java\example\invoiceorder\build\WEB-INF\lib

war:
     [echo] Building the war file
      [war] Building war: c:\projects\java-integration\java\example\invoiceorder\dist\InvoiceOrder.war

deploy_invoiceorder:
     [echo] Deploying .war to local Tomcat
     [copy] Copying 1 file to C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps

BUILD SUCCESSFUL
Total time: 1 second
$
```
(You may also build the examples using the main package ant target "examples".)

You should now be able to access the example by going to http://localhost:8080/InvoiceOrder

The web.xml file contains the servlet routing information for the InvoiceOrder application. 
When you land on the index.jsp file it redirects you to /invoiceorder, which in turn passes the request to InvoiceOrderServlet as stated in web.xml.
The backend InvoiceOrderServlet builds an order and sends a payment request to Svea. After the service responds, you're redirected to invoiceorder.jsp.
The frontend invoiceorder.jsp file then presents the response result.

### 11.2 Svea invoice order <a name="i112"></a>
An example of a synchronous (invoice) order can be found in the example/invoiceorder folder.

### 11.3 Card order <a name="i113"></a>
An example of an asynchronous card order can be found in the example/cardorder folder.

[<< To top](http://sveawebpay.github.io/java-integration#index)
