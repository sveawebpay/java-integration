# Java Integration Package API for Svea WebPay

Version 2.0.0

| Branch                            | Build status                               |
|---------------------------------- |------------------------------------------- |
| master (latest release)           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=master)](https://travis-ci.org/sveawebpay/java-integration) |
| develop                           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=develop)](https://travis-ci.org/sveawebpay/java-integration) |


## 11 Examples
The provided examples show how to use the Svea java integration package to specify an order and send a payment request to Svea.

### 11.1 Running the examples
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

### 11.2 Svea invoice order
An example of a synchronous (invoice) order can be found in the example/invoiceorder folder.

### 11.3 Card order
An example of an asynchronous card order can be found in the example/cardorder folder.



=============
(Below documentation is to be regarded as a work in progress)

# Administration of hosted service (i.e. card and direct bank payment methods) orders

## Java integration package release 1.6.0

### WebPayAdmin.cancelOrder entrypoint


The WebPayAdmin.cancelOrder() entrypoint method is used to cancel an order with Svea, 
that has not yet been delivered (invoice, payment plan) or confirmed (card).

Supports Invoice, Payment Plan and Card orders. For Direct Bank orders, use WebPayAdmin.creditOrderRows() instead.
 
Get an instance using the WebPayAdmin.queryOrder entrypoint, then provide more information about the order and send the 
request using the CancelOrderBuilder methods:

...
    request = WebPayAdmin.cancelOrder(config)
         .setOrderId()			// required, use SveaOrderId recieved with createOrder response
         .setTransactionId()	// optional, card or direct bank only, alias for setOrderId 
         .setCountryCode()		// required, use same country code as in createOrder request      
    ;
    // then select the corresponding request class and send request
    response = request.cancelInvoiceOrder().doRequest();		// returns CloseOrderResponse
    response = request.cancelPaymentPlanOrder().doRequest();	// returns CloseOrderResponse
    response = request.cancelCardOrder().doRequest();			// returns AnnulTransactionResponse
...

## WebPay.deliverOrder entrypoint

Use the WebPay.deliverOrder() entrypoint when you deliver an order to the customer. 
Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported.)

The deliver order request should generally be sent to Svea once the ordered 
items have been sent out, or otherwise delivered, to the customer. 

For invoice and partpayment orders, the deliver order request triggers the 
invoice being sent out to the customer by Svea. (This assumes that your account
has auto-approval of invoices turned on, please contact Svea if unsure). 

For card orders, the deliver order request confirms the card transaction, 
which in turn allows nightly batch processing of the transaction by Svea.  
(Delivering card orders is only needed if your account has auto-confirm
turned off, please contact Svea if unsure.)

To deliver an invoice, partpayment or card order in full, you do not need to 
specify order rows. To partially deliver an order, the recommended way is to
use WebPayAdmin.deliverOrderRows().

For more information on using deliverOrder to partially deliver and/or credit
an order, see XXXXX below.
 
Get an order builder instance using the WebPay.deliverOrder entrypoint, then
provide more information about the transaction using DeliverOrderBuilder methods: 

...
     request = WebPay.deliverOrder(config)
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

XXXXX
#### xxxxx On using WebPay.deliverOrder with order rows
WebPay.deliverOrder may be used to partially deliver, amend or credit an order, by specifying order rows using the DeliverOrderBuilder addOrderRow() method. We recommend using WebPayAdmin.deliverOrderRows to partially deliver an order and WebPayAdmin.creditOrderRows to credit an order.

##### xxxxx.1 Partial delivery using WebPay.deliverOrder
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

##### xxxxx.2 Amending an order using WebPay.deliverOrder
If you wish to add an order row to an existing order, any original order rows still undelivered will be cancelled to make room for the added order rows within the original order total amount (you may deliver order rows in the same request by adding order rows that exactly match the original order rows).

The exact behaviour is that if there are order rows in the deliver order request that does not match any undelivered original order row, all unmatched and undelivered original order rows are cancelled, and the unmatched deliver order rows are added to the original order as new delivered order rows, given that as the total of all existing delivered rows and the newly added order rows does not exceed the total original order row total amount. This means that the sum of the unmatched (i.e. added) deliver order rows cannot exceed the sum of the cancelled original order rows.

When there are delivered order rows to an amount equal to the original order total amount the order will be closed, preventing further modification. Delivered order rows can only be credited, see also 6.2.3.3 below.

```
Example (cont. from xxxxx.1):
3. dResponse2 = WebPay.deliverOrder().addOrderRows(D) ... .doRequest(); // D does not match any rows
Will result in the order having status
A: delivered	// found on invoice1; dResponse.getInvoiceId()
B: cancelled
C: cancelled
D: delivered	// found on invoice2; dResponse2.getInvoiceId()
```

##### xxxxx.3 Crediting a (partially) delivered order using WebPay.deliverOrder
To credit an order use the setCreditInvoice(invoiceId) method when delivering an order. Add an order row made out to the amount to be credited to the deliver order request. A credit invoice with the order rows specified will be issued to the customer.

When crediting a delivered order, you are really crediting an invoice. This means that if you i.e. partially delivered an order, and then need to credit the entire order, you will need to make several crerequests, as a credit invoice amount can't exceed the individual invoice total amount.

The invoice id received will point to the new credit invoice itself, and the original invoice will be be credited at Svea by the specified amount. Note that the original order row status will not change, the as the request operates on the invoice, not the order in itself.

```
Example (cont. from xxxxx.2):
4. dResponse3 = WebPay.deliverOrder().addOrderRows(E).setCreditInvoice(invoice1) ... .doRequest();
//To credit i.e. 50% of the price for order row A we created a new order row E with half the price of A.
//The credit invoice id is returned in dResponse3->getInvoiceId()
```


## WebPayAdmin.deliverOrderRows entrypoint

The WebPayAdmin.deliverOrderRows entrypoint method is used to deliver individual order rows. 
1.6.0: Supports card orders. To deliver invoice order rows, use WebPay.deliverOrder with specified order rows.

For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.

For card orders, an order can only be delivered once, and any non-delivered order rows will be cancelled (i.e. 
the order amount will be lowered by the sum of the non-delivered order rows). A delivered card order has status 
CONFIRMED at Svea.

Get an order builder instance using the WebPayAdmin.deliverOrderRows entrypoint,
then provide more information about the transaction and send the request using
the DeliverOrderRowsBuilder methods:

Use setRowToDeliver() or setRowsToDeliver() to specify the order row(s) to deliver. The order row indexes should 
correspond to those returned by i.e. WebPayAdmin.queryOrder();

For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order 
rows. The original order rows can be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute 
contains the serverside order rows w/indexes. Note that if a card order has been modified (i.e. rows cancelled 
or credited) after the initial order creation, the returned order rows will not be accurate.

...
		request = WebPayAdmin.deliverOrderRows(config)
			.setOrderId()          		// required
			.setTransactionId()	   		// optional, card only, alias for setOrderId 
			.setCountryCode()      		// required    	
			.setInvoiceDistributionType()	// required, invoice only
			.setRowToDeliver()	   			// required, index of original order rows you wish to deliver 
			.addNumberedOrderRow()			// required for card orders, should match original row indexes 
		;
		// then select the corresponding request class and send request
		response = request.deliverCardOrderRows().doRequest()	// returns ConfirmTransactionResponse
...


## WebPayAdmin.cancelOrderRows entrypoint


The WebPayAdmin.cancelOrderRows entrypoint method is used to cancel rows in an order before it has been delivered.
1.6.0: Supports card orders.

For Invoice and Payment Plan orders, the order row status is updated at Svea following each successful request.

For card orders, the request can only be sent once, and if all original order rows are cancelled, the order then receives status ANNULLED at Svea.

Get an order builder instance using the WebPayAdmin.cancelOrderRows entrypoint, then provide more information about the transaction and send the 
request using the CancelOrderRowsBuilder methods:

Use setRowToCancel() or setRowsToCancel() to specify the order row(s) to cancel. The order row indexes should correspond to those returned by 
i.e. WebPayAdmin.queryOrder();

For card orders, use addNumberedOrderRow() or addNumberedOrderRows() to pass in a copy of the original order rows. The original order rows can 
be retrieved using WebPayAdmin.queryOrder(); the numberedOrderRows attribute contains the serverside order rows w/indexes. Note that if a card 
order has been modified (i.e. rows cancelled or credited) after the initial order creation, the returned order rows will not be accurate.

...
		request = WebPayAdmin.cancelOrderRows(config)
         .setOrderId()          			// required
         .setTransactionId()	   			// optional, card only, alias for setOrderId 
         .setCountryCode()      			// required    	
         .setRowToCancel()	   			// required, index of original order rows you wish to cancel 
         .addNumberedOrderRow()			// required for card orders, should match original row indexes 
    	;
    	// then select the corresponding request class and send request
    	response = request.deliverCardOrderRows().doRequest()	// returns LowerTransactionResponse
...


## WebPayAdmin.creditOrderRows entrypoint


The WebPayAdmin.creditOrderRows entrypoint method is used to credit rows in an order after it has been delivered.
1.6.0: Supports card and direct bank orders.

...
    request = WebPay.creditOrder(config)
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
    response = request.creditCardOrderRows().doRequest();       // returns CreditTransactionResponse
    response = request.creditDirectBankOrderRows().doRequest(); // returns CreditTransactionResponse
...



