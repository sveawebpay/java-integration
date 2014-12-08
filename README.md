# Java Integration Package API for Svea WebPay

Version 1.5.0

| Branch                            | Build status                               |
|---------------------------------- |------------------------------------------- |
| master (latest release)           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=master)](https://travis-ci.org/sveawebpay/java-integration) |
| develop                           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=develop)](https://travis-ci.org/sveawebpay/java-integration) |

## Index
* [1. Introduction](https://github.com/sveawebpay/java-integration/tree/master#1-introduction)
* [2. Build](https://github.com/sveawebpay/java-integration/tree/master#2-build)
* [3. Configuration](https://github.com/sveawebpay/java-integration/tree/master#3-configuration)
* [4. CreateOrder](https://github.com/sveawebpay/java-integration/tree/master#4-createorder)
    * [Specify order](https://github.com/sveawebpay/java-integration/tree/master#41-specify-order)
    * [Customer identity](https://github.com/sveawebpay/java-integration/tree/master#42-customer-identity)
    * [Other values](https://github.com/sveawebpay/java-integration/tree/master#43-other-values)
    * [Choose payment](https://github.com/sveawebpay/java-integration/tree/master#44-choose-payment)
* [5. GetPaymentPlanParams](https://github.com/sveawebpay/java-integration/tree/master#5-getpaymentplanparams)
    * [PaymentPlanPricePerMonth](https://github.com/sveawebpay/java-integration/tree/master#51-paymentplanpricepermonth)
* [6. GetAddresses](https://github.com/sveawebpay/java-integration/tree/master#6-getaddresses)
* [7. DeliverOrder](https://github.com/sveawebpay/java-integration/tree/master#7-deliverorder)
    * [Deliver Invoice order](https://github.com/sveawebpay/java-integration/tree/master#71-deliver-invoice-order)
    * [Other values](https://github.com/sveawebpay/java-integration/tree/master#72-other-values)
* [8. Credit Invoice](https://github.com/sveawebpay/java-integration/tree/master#8-credit-invoice)
* [9. CloseOrder](https://github.com/sveawebpay/java-integration/tree/master#9-closeorder)
* [10. Response handler](https://github.com/sveawebpay/java-integration/tree/master#10-response-handler)
* [APPENDIX](https://github.com/sveawebpay/java-integration/tree/master#appendix)


## 1. Introduction                                                             
This integration package is built for developers to simplify the integration of Svea WebPay services. 
Using this package will make your implementation sustainable and unaffected by changes
in our payment system. Just make sure to update the package regularly.

The API is built as a *Fluent API*, ie. you can use *method chaining* when implementing it in your code.

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 2. Build                                                             

To build a jar file, use the ant build file located at `java/build.xml`. Use the command
```
ant clean-jar
``` 
to build, unit test, and create the `sveawebpay.jar` file (in the `target/jar` directory), or preferrably 
```
ant clean-integrationtest
``` 
that will build the jar and also run the integration tests on the created jar. 


Other public targets can be found in the build.xml file.


[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 3. Configuration 

The Configuration needed to be set differs of how many different payment methods and countries you have in the same installation. 
The authorization values are recieved from Svea Ekonomi when creating an account. If no configuration is done, default settings from SveaConfig.getDefaultConfig() can be used.

**To configure Svea authorization:**
Create a class (eg. one for testing values, one for production) that implements the ConfigurationProvider Interface. Let the implemented methods 
return the authorization values asked for. 
Later when starting a WebPay action in your integration file, put an instance of your class as parameter to the constructor.

*NOTE:* This solution may change in future updates! 

Step 1:
```java

package se.sveaekonomi.webpay.integration.config;
import java.net.URL;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class MyConfigTest implements ConfigurationProvider {
	
	/**
	 * Constants for the end point url found in the class SveaConfig
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @return
	 */
	@Override
	public URL getEndPoint(PAYMENTTYPE type) {
		if(PAYMENTTYPE.HOSTED == type)
			return SveaConfig.getTestPayPageUrl();		
		return SveaConfig.getTestWebserviceUrl();					
	}	
	
	/**
	 * Get the return value from your database or likewise
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @param country code
	 * @return user name
	 */
	@Override
	public String getUsername(PAYMENTTYPE type, COUNTRYCODE country) {		
		return myUserName;
	}

	/**
	 * Get the return value from your database or likewise
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @param country code
	 * @return password
	 */
	@Override
	public String getPassword(PAYMENTTYPE type, COUNTRYCODE country) {		
		return myPassword;
	}

	/**
	 * Get the return value from your database or likewise
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @param country code
	 * @return client number
	 */
	@Override
	public int getClientNumber(PAYMENTTYPE type, COUNTRYCODE country) {													
		return myClientNumber;
	}

	/**
	 * Get the return value from your database or likewise
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @param country code
	 * @return merchant id
	 */
	@Override
	public String getMerchantId(PAYMENTTYPE type, COUNTRYCODE country) {		
		return myMerchantId;
	}

	/**
	 * Get the return value from your database or likewise
	 * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
	 * @param country code
	 * @return secret word
	 */
	@Override
	public String getSecretWord(PAYMENTTYPE type, COUNTRYCODE country) {
		return mySecretWord;
	}	
}

```

Step 2: Put an instance of your configuration object as a parameter to the request.

```java
	//Create a class including test authorization
	MyConfigTest confTest = new MyConfigTest();
	//Create a class including production authorization
	MyConfigProd confProd = new MyConfigProd();
	
	//Create your CreateOrder object with selected and continue building your order. Se next steps.
	CreateOrderResponse response = WebPay.createOrder(confTest)
	.....
	
```

A complete example:  

```java
		// import Svea WebPay java integration package
		import se.sveaekonomi.webpay.integration.*;

		// get configuration object holding the Svea service login credentials
		ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
				
		// We assume that you've collected the following information about the order in your shop: 
		// The shop cart contains one item "Billy" which cost 700,99 kr excluding vat (25%).
		// When selecting to pay using the invoice payment method, the customer has also provided their social security number, which is required for invoice orders.

		// Begin the order creation process by creating an order builder object using the WebPay.createOrder() method:
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
		
		// Act on service request response
		if( myResponse.isOrderAccepted() ) 
		{ 
			System.out.println( "Order payment request accepted by Svea." ); 
		}
		else 
		{
			System.out.println( "Order payment request failed. Svea error: " + myResponse.getResultCode() + " " + myResponse.getErrorMessage() ); 
		}
```  
  
  
  
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 4. createOrder                                                            
Creates an order and performs payment for all payment forms. Invoice and payment plan will perform 
a synchronous payment and return a response. 
Other hosted payments, like card, direct bank and payments from the *PayPage*
on the other hand are asynchronous. They will return an html form with formatted message to send from your store.
For every new payment type implementation, you follow the steps from the beginning and chose your payment type preffered in the end:
Build order -> choose payment type -> doRequest/getPaymentForm

```java
CreateOrderResponse response = WebPay.createOrder(myConfig)		//See Configuration chapt.3
//For all products and other items
.addOrderRow(WebPayItem.orderRow()...)
//If shipping fee
.addFee(WebPayItem.shippingFee()...)
//If invoice with invoice fee
.addFee(WebPayItem.invoiceFee()...)
//If discount or coupon with fixed amount
.addDiscount(WebPayItem.fixedDiscount()...)
//If discount or coupon with percent discount
.addDiscount(WebPayItem.relativeDiscount()...)
//Individual customer values
.addCustomerDetails(WebPayItem.individualCustomer()...)
//Company customer values
.addCustomerDetails(WebPayItem.companyCustomer()...)
//Other values
.setCountryCode(COUNTRYCODE.SE)
.setOrderDate("2012-12-12")
.setCustomerReference("33")
.setClientOrderNumber("nr26")
.setCurrency("SEK")

//Continue by choosing one of the following paths
//Continue as a card payment
.usePayPageCardOnly() 
	...
	.getPaymentForm();
//Continue as a direct bank payment		
.usePayPageDirectBankOnly()
	...
	.getPaymentForm();
//Continue as a PayPage payment
.usePayPage()
	...
	.getPaymentForm();
//Continue as a PayPage payment
.usePaymentMethod(PAYMENTMETHOD.DBSEBSE) //see APPENDIX for Constants
	...
	.getPaymentForm();
//Continue as an invoice payment
.useInvoicePayment()
	...
	.doRequest();
//Continue as a payment plan payment
.usePaymentPlanPayment("campaigncode", false)
	...
	.doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

	
### 4.1 Specify item price                                                    
Continue by adding values for products and other. You can add order row, fee and discount. 
See types of WebPayItem objects in examples in 4.1.2--4.1.5 and 4.3.1--4.3.2.

Specify item price using precisely two of these methods in order to specify the item price and tax rate: setAmountIncVat(), setVatPercent() and setAmountExVat().

The recommended way to specify an item price is by using the setAmountIncVat() and setVatPercent() methods. This will ensure that the total order amount and vat sums precisely match the amount and vat specified in the order items.

When using setAmountExVat() and setVatPercent(), the service will work with less accuracy and may accumulate rounding errors, resulting in order totals differing from total of the amount and vat specified in the row items. It is not recommended to specify price using the setAmountExVat() method.

When using setAmountIncVat() with setAmountExVat() to specify an item price, the package converts the price to amount including vat and vat percent, in an effort to maintain maximum accuracy.


```java
.addOrderRow(WebPayItem.orderRow(). ...)

//or

List<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>(); //or use another preferrable List object
orderRows.add(WebPayItem.orderRow(). ...)
...
createOrder.addOrderRows(orderRows);
```
	
#### 4.1.1 OrderRow
All products and other items. It�s required to have a minimum of one order row.
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat() and setVatPercent().**
```java
.addOrderRow(WebPayItem.orderRow()     
	.setQuantity(2)                        //Required
	.setAmountExVat(100.00)                //Optional, see info above
	.setAmountIncVat(125.00)               //Optional, see info above
	.setVatPercent(25.00)                  //Optional, see info above
	.setArticleNumber("1")                 //Optional
	.setDescription("Specification")       //Optional
	.setName("Prod")                       //Optional
	.setUnit("st")                         //Optional              
	.setDiscountPercent(0))                //Optional    
```

#### 4.1.2 ShippingFee
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat()and setVatPercent().**
```java
.addFee(WebPayItem.shippingFee()
	.setAmountExVat(50)                    //Optional, see info above
	.setAmountIncVat(62.50)                //Optional, see info above
	.setVatPercent(25.00)                  //Optional, see info above
	.setShippingId("33")                   //Optional
	.setName("shipping")                   //Optional
	.setDescription("Specification")       //Optional
	.setUnit("st")                         //Optional             
	.setDiscountPercent(0))                //Optional
```
#### 4.1.3 InvoiceFee
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat() and setVatPercent().**
```java
.addFee(WebPayItem.invoiceFee()
	.setAmountExVat(50)                    //Optional, see info above
	.setAmountIncVat(62.50)                //Optional, see info above
	.setVatPercent(25.00)       	       //Optional, see info above
	.setName("Svea fee")                   //Optional
	.setDescription("Fee for invoice")     //Optional		
	.setUnit("st")                         //Optional
	.setDiscountPercent(0))                //Optional    
```
#### 4.1.4 Fixed Discount
When discount or coupon is a fixed amount on total product amount.
```java
.addDiscount(WebPayItem.fixedDiscount()                
	.setAmountIncVat(100.00)               //Required
	.setDiscountId("1")                    //Optional        
	.setUnit("st")                         //Optional
	.setDescription("FixedDiscount")       //Optional
	.setName("Fixed"))                     //Optional    
```
#### 4.1.5 Relative Discount
When discount or coupon is a percentage on total product amount.
```java
.addDiscount(WebPayItem.relativeDiscount()
	.setDiscountPercent(50)                //Required
	.setDiscountId("1")                    //Optional      
	.setUnit("st")                         //Optional
	.setName("Relative")                   //Optional
	.setDescription("RelativeDiscount"))   //Optional    
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 4.2 Customer Identity   
Customer identity is required for invoice and payment plan orders. Required values varies 
depending on country and customer type. For SE, NO, DK and FI national id number is required. 

If the customer email and/or ip address are available, they should be added to the customer identity object.

####4.2.1 Options for individual customers
```java
.addCustomerDetails(WebPayItem.individualCustomer()
    .setNationalIdNumber("194605092222")	//Required for individual customers in SE, NO, DK and FI
    .setBirthDate(1923, 12, 12)        		//Required for individual customers in NL and DE
    .setName("Tess", "Testson")        		//Required for individual customers in NL and DE
    .setInitials("SB")                 		//Required for individual customers in NL
    .setStreetAddress("Gatan", 23)     		//Required in NL and DE
    .setCoAddress("c/o Eriksson")      		//Optional
    .setZipCode(9999)                  		//Required in NL and DE
    .setLocality("Stan")               		//Required in NL and DE
    .setPhoneNumber("999999"))           	//Optional
    .setEmail("test@svea.com")         		//Required if available
    .setIpAddress("123.123.123")       		//Required if available
```

####4.2.2 Options for company customers
```java
.addCustomerDetails(WebPayItem.companyCustomer()
    .setNationalIdNumber("2345234")			//Required for company customers in SE, NO, DK, FI
    .setVatNumber("NL2345234")				//Required for NL and DE
    .setCompanyName("TestCompagniet")) 		//Required for Eu countries like NL and DE
	.setAddressSelector("7fd7768")          //Optional. Recieved from getAddresses		
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

###4.3 Other values  
```java
.setCountryCode(COUNTRYCODE.SE)         //Required
.setCurrency("SEK")                     //Required for card payment, direct payment and PayPage payment.
.setClientOrderNumber("nr26")           //Required for card payment, direct payment, PaymentMethod payment and PayPage payments. Must be uniqe.
.setOrderDate("2012-12-12")             //Required for synchronous payments
.setCustomerReference("33")             //Optional
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

###4.4 Choose payment 
End process by choosing the payment method you desire.

Invoice and payment plan will perform a synchronous payment and return an object as response. 

Other payments(card, direct bank and payments from the *PayPage*) on the other hand are asynchronous. They will return an html form with formatted message to send from your store.
The response is then returned to the return url you have specified in function *setReturnUrl()*. The response may also be sent to the url specified with *setCallbackUrl()* in case the customer doesn't return to the store after the transaction has concluded at the bank/card payment page.

If you pass the xml response to an instance of SveaResponse, you will receive a formatted response object as well. 

#### Which payment method to choose?
Invoice and/or payment plan payments.
>The preferable way is to use [`.useInvoicePayment()`](https://github.com/sveawebpay/java-integration/tree/master#455-invoicepayment) and
>[`.usePaymentPlanPayment(...)`](https://github.com/sveawebpay/java-integration/tree/master#456-paymentplanpayment).
>These payments are synchronous and will give you an instant response.

Card and/or direct bank payments
>Go by *PayPage* by using [`.usePayPageCardOnly()`](https://github.com/sveawebpay/java-integration/tree/master#451-paypage-with-card-payment-options)
>and [`.usePayPageDirectBankOnly()`](https://github.com/sveawebpay/java-integration/tree/master#452-paypage-with-direct-bank-payment-options). 
>If you only for example only have one specific bank payment, go direct to that specific bank payment by using
>[`.usePaymentMethod(PaymentMethod)`](https://github.com/sveawebpay/java-integration/tree/master#454-paymentmethod-specified)

Using all payments.
>The most effective way is to use [`.useInvoicePayment()`](https://github.com/sveawebpay/java-integration/tree/master#455-invoicepayment) 
>and [`.usePaymentPlanPayment(...)`](https://github.com/sveawebpay/java-integration/tree/master#456-paymentplanpayment) for the synchronous payments,
>and use the *PayPage* for the asynchronous requests by using [`.usePayPageCardOnly()`](https://github.com/sveawebpay/java-integration/tree/master#451-paypage-with-card-payment-options) 
>and [`.usePayPageDirectBankOnly()`](https://github.com/sveawebpay/java-integration/tree/master#452-paypage-with-direct-bank-payment-options).

Using more than one payment and want them gathered on one place.
>Go by PayPage and choose show all your payments here, or modify to exclude or include one or more payments. Use [`.usePayPage()`]
>(https://github.com/sveawebpay/java-integration/tree/master#453-paypagepayment) where you can custom your own *PayPage*.
Note that Invoice and Payment plan payments will return an asynchronous response from here.


#### Asynchronous payments - Hosted solutions
Build order and recieve a *PaymentForm* object. Send the *PaymentForm* parameters: *merchantid*, *xmlMessageBase64* and *mac* by POST with *url*. The *PaymentForm* object also contains a complete html form as string 
and the html form element as array.

```html
    <form name="paymentForm" id="paymentForm" method="post" action=" + url +">
        <input type="hidden" name="merchantid" value=" + merchantId + " />
        <input type="hidden" name="message" value=" + xmlMessageBase64 + " />
        <input type="hidden" name="mac" value=" + mac + "/>
        <noscript><p>Javascript is inactivated.</p></noscript>
        <input type="submit" name="submit" value="Submit" />
    </form>
```

.setReturnUrl() When a hosted payment transaction completes (regardless of outcome, i.e. accepted or denied), the payment service will answer with a response xml message sent to the return url specified.

.setCallbackUrl() In case the hosted payment transaction completes, but the service is unable to return a response to the return url, the payment service will retry several times using the callback url as a fallback, if specified. This may happen if i.e. the user closes the browser before the payment service redirects back to the shop.

.setCancelUrl() In case the hosted payment service is cancelled by the user, the payment service will redirect back to the cancel url. Unless a return url is specified, no cancel button will be presented at the payment service.

####4.4.1 PayPage with card payment options
*PayPage* with availible card payments only.

#####4.4.1.1 Request

```java
PaymentForm form = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
	.setArticleNumber("1")
	.setName("Prod")
	.setDescription("Specification")
	.setQuantity(2)
	.setUnit("st")
	.setAmountExVat(100.00)
	.setVatPercent(25.00)
	.setDiscountPercent(0))
		
.setCountryCode(COUNTRYCODE.SE)							//Required
.setClientOrderNumber("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.usePayPageCardOnly()
	.setReturnUrl("http://myurl.se")                   	//Required
	.setCallbackUrl("http://myurl.se")                   	//Optional
	.setCancelUrl("http://myurl.se")                   	//Optional
	.getPaymentForm();

```
#####4.4.1.2 Return
The values of *xmlMessageBase64*, *merchantid* and *mac* are to be sent as xml to SveaWebPay.
Function getPaymentForm() returns object type *PaymentForm* with accessible members:

| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |
| getUrl()				| String     | Url to preselected server, test or production.|     
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
            

####4.4.2 PayPage with direct bank payment options
*PayPage* with available direct bank payments only.
                
#####4.4.2.1 Request

```java
PaymentForm form = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
	.setArticleNumber("1")
	.setName("Prod")
	.setDescription("Specification")
	.setQuantity(2)
	.setUnit("st")
	.setAmountExVat(100.00)
	.setVatPercent(25.00)
	.setDiscountPercent(0))
		
.setCountryCode(COUNTRYCODE.SE)						   //Required
.setCustomerReference("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.usePayPageDirectBankOnly()	
	.setReturnUrl("http://myurl.se")                   //Required		
	.setCancelUrl("http://myurl.se")                   //Optional
	.getPaymentForm();
```
#####4.4.2.2 Return
Returns object type PaymentForm:
           
| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |   
| getUrl()				| String     | Url to preselected server, test or production.|   
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
 
 
####4.4.3 PayPagePayment
*PayPage* with all available payments. You can also custom the *PayPage* by using one of the methods for *PayPagePayments*:
setPaymentMethod, includePaymentMethods, excludeCardPaymentMethods or excludeDirectPaymentMethods.
                
#####4.4.3.1 Request
```java
PaymentForm form = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
	.setArticleNumber("1")
	.setName("Prod")
	.setDescription("Specification")
	.setQuantity(2)
	.setUnit("st")
	.setAmountExVat(100.00)
	.setVatPercent(25.00)
	.setDiscountPercent(0))   
	
.setCountryCode(COUNTRYCODE.SE)							//Required
.setCustomerReference("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.usePayPage()
	.setReturnUrl("http://myurl.se")                   	//Required	
	.setCancelUrl("http://myurl.se")                   	//Optional
	.setPayPagePayment(LANGUAGECODE.sv)					//Optional, English is default. LANGUAGECODE, see APPENDIX
	.getPaymentForm();
```               

######4.4.3.1.1 Exclude specific payment methods
Optional if you want to include specific payment methods for *PayPage*.
```java   
.usePayPage()
	.setReturnUrl("http://myurl.se")                                            //Required
	.setCancelUrl("http://myurl.se")                                            //Optional
	.excludePaymentMethods(PAYMENTMETHOD.DBSEBSE, PAYMENTMETHOD.SVEAINVOICE_SE)	//Optional
	.getPaymentForm();
```
######4.4.3.1.2 Include specific payment methods
Optional if you want to include specific payment methods for *PayPage*.
```java   
.usePayPage()
	.setReturnUrl("http://myurl.se")                                            //Required
	.includePaymentMethods(PAYMENTMETHOD.DBSEBSE, PAYMENTMETHOD.SVEAINVOICE_SE)	//Optional
	.getPaymentForm();
```

######4.4.3.1.3 Exclude Card payments
Optional if you want to exclude all card payment methods from *PayPage*.
```java
.usePayPage()
	.setReturnUrl("http://myurl.se")                   //Required
	.excludeCardPaymentMethods()                       //Optional
	.getPaymentForm();
```

######4.4.3.1.4 Exclude Direct payments
Optional if you want to exclude all direct bank payments methods from *PayPage*.
```java  
.usePayPage()
    .setReturnUrl("http://myurl.se")                   //Required
    .excludeDirectPaymentMethods()                     //Optional
    .getPaymentForm();
```
#####4.4.3.6 Return
Returns object type *PaymentForm*:
                
| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |  
| getUrl()				| String     | Url to preselected server, test or production.|    
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
 

#### 4.4.4 PaymentMethod specified
Go direct to specified payment method without the step *PayPage*.

##### 4.4.4.1 Request
Set your store authorization here.
```java
PaymentForm form = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
	.setArticleNumber("1")
	.setName("Prod")
	.setDescription("Specification")
	.setQuantity(2)
	.setUnit("st")
	.setAmountExVat(100.00)
	.setVatPercent(25)
	.setDiscountPercent(0))                  
		
.setCountryCode(COUNTRYCODE.SE)							//Required
.setClientOrderNumber("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.usePaymentMethod(PAYMENTMETHOD.KORTCERT)             	//PAYMENTMETHOD see APPENDIX
	.setReturnUrl("http://myurl.se")                  	//Required
	.setCancelUrl("http://myurl.se")                  	//Optional
	.getPaymentForm();

```
##### 4.4.4.2 Return
The values of *xmlMessageBase64*, *merchantid* and *mac* are to be sent as xml to SveaWebPay.
Function getPaymentForm() returns Object type PaymentForm with accessible members:

| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |    
| getUrl()				| String     | Url to preselected server, test or production.|  
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
            

#### Synchronous solutions - Invoice and PaymentPlan
       
#### 4.4.5 InvoicePayment
Perform an invoice payment. This payment form will perform a synchronous payment and return a response.
Returns *CreateOrderResponse* object. Set your store authorization here.
```java
CreateOrderResponse response = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
.setArticleNumber("1")
.setName("Prod")
.setDescription("Specification")
.setQuantity(2)
.setUnit("st")
.setAmountExVat(100.00)
.setVatPercent(25.00)
.setDiscountPercent(0))

.setCountryCode(COUNTRYCODE.SE)						//Required
.setCustomerReference("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.useInvoicePayment()
	.doRequest();
```
#### 4.4.6 PaymentPlanPayment
Perform *PaymentPlanPayment*. This payment form will perform a synchronous payment and return a response.
Returns a *CreateOrderResponse* object. Preceded by WebPay.getPaymentPlanParams(...).
Set your store authorization here.
Param: Campaign code recieved from getPaymentPlanParams().
```java
CreateOrderResponse response = WebPay.createOrder()
.addOrderRow(WebPayItem.orderRow()
	.setArticleNumber("1")
	.setName("Prod")
	.setDescription("Specification")
	.setQuantity(2)
	.setUnit("st")
	.setAmountExVat(100.00)
	.setVatPercent(25.00)
	.setDiscountPercent(0))   
	
.setCountryCode(COUNTRYCODE.SE)						//Required
.setCustomerReference("33")
.setOrderDate("2012-12-12")
.setCurrency("SEK")
.usePaymentPlanPayment("camp1", false)              //Parameter1: campaign code recieved from getPaymentPlanParams
													//Paremeter2: True if Automatic autogiro form will be sent with the first notification		   
   .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 5. getPaymentPlanParams   
Use this function to retrieve campaign codes for possible payment plan options. Use prior to create payment plan payment.
Returns *PaymentPlanParamsResponse* object. Set your store authorization here.

```java
CreateOrderResponse response = WebPay.getPaymentPlanParams()	
	.setCountryCode(COUNTRYCODE.SE)					//Required	
	.doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 5.1 paymentPlanPricePerMonth
Use this function to calculate the prices per month of the payment plan campaigns received from getPaymentPlanParams.
*paymentPlanParams* is the response object from getPaymentPlanParams.
```java
   List<Map<String, String>> pricePerMonth = WebPay.paymentPlanPricePerMonth(amount, paymentPlanParams);
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 6. getAddresses 
Returns *getAddressesResponse* object with an *AddressSelector* for the associated addresses for a specific security number. 
Can be used when creating an order. Only applicable for SE, NO and DK. In Norway, only getAddresses of companies is supported.
Set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 6.1 Order type 
```java
    .setOrderTypeInvoice()         //Required if this is an invoice order
or
    .setOrderTypePaymentPlan()     //Required if this is a payment plan order
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 6.2 Customer type 
```java
    .setIndividual("194605092222") //Required if this is an individual customer
or
    .setCompany("companyId")       //Required if this is a company customer
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 6.3                                                                      	
```java
GetAddressesResponse response = WebPay.getAddresses(myConfig)		//see more about Configuration chapt.3       	
	.setCountryCode(COUNTRYCODE.SE)                                 //Required
	.setOrderTypeInvoice()                                          //See 6.1   	
	.setIndividual("194605092222")                                  //See 6.2   
	.doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 7. deliverOrder                                                           
Use the WebPay.deliverOrder request to deliver to the customer invoices for fulfilled orders.
Svea will invoice the customer upon receiving the deliverOrder request.
A deliverOrder request may also be used to partly deliver an order on Invoice orders.
Add rows that you want delivered. The rows will automatically be matched with the rows that was sent when creating the order.
When Svea receives the deliverOrder request the status on the previous created order is set to *delivered*.
The deliverOrder functionallity is only applicable to invoice and payment plan payment method payments.

Returns *DeliverOrderResult* object. Set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 7.1 Deliver Invoice order                                                       
This works more or less like WebPay.createOrder above, and makes use of the same order item information.
Add the corresponding order id and the order rows that you want delivered before making the deliverOrder request.
The specified rows will automatically be matched with the previous rows that was sent when creating the order.
We recommend storing the order row data to ensure that matching orderrows can be recreated in the deliverOrder request.

If an item is left out from the deliverOrder request that was present in the createOrder request, a new invoice will be created as the order is assumed to be partially fulfilled.
Any left out items should not be delivered physically, as they will not be invoiced when the deliverOrder request is sent.

```java
	DeliverOrderResponse response = Webpay.deliverOrder()
    .addOrderRow(
        Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0)
        )
        .setOrderId(1234) //Recieved from CreateOrder request
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .deliverInvoiceOrder()
            .doRequest();
```

You can add OrderRow, Fee and Discount. Choose the right Item as parameter.
You can use the **.add** functions with an Item or list of Items as parameters.

```java
.addOrderRow(WebPayItem.orderRow(). ...)

//or

List<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>(); //or use another preferrable List object
orderRows.add(WebPayItem.orderRow(). ...)
...
deliverOrder.addOrderRows(orderRows);
```

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

#### 7.1.1 OrderRow
All products and other items. It is required to have a minimum of one row.
```java
.addOrderRow(WebPayItem.orderRow()
   .setArticleNumber("1")               //Optional
   .setName("Prod")                 	//Optional
   .setDescription("Specification")	    //Optional
   .setQuantity(2)              	    //Required
   .setUnit("st")                      	//Optional
   .setAmountExVat(100.00)  	        //Required
   .setVatPercent(25.00)    	        //Required
   .setDiscountPercent(0))             	//Optional
```

#### 7.1.2 ShippingFee
```java
.addFee(WebPayItem.shippingFee()
	.setAmountExVat(50)               	//Required
	.setVatPercent(25.00)              	//Required
	.setShippingId("33")               	//Optional
	.setName("shipping")               	//Optional
	.setDescription("Specification")   	//Optional        
	.setUnit("st")                     	//Optional        
	.setDiscountPercent(0))
```
#### 7.1.3 InvoiceFee
```java
.addFee(WebPayItem.invoiceFee()
	.setAmountExVat(50)            		//Required
	.setVatPercent(25.00)          		//Required
	.setName("Svea fee")           		//Optional
	.setDescription("invoice fee") 		//Optional       
	.setUnit("st")                 		//Optional
	.setDiscountPercent(0))        		//Optional
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 7.2 Other values  
Required is the order id received when creating the order. Required for invoice orders are *InvoiceDistributionType*. 
If invoice order is credit invoice use setCreditInvoice(invoiceId) and setNumberOfCreditDays(creditDaysAsInt)
```java
    .setOrderId(orderId)                   				//Required. Received when creating order.
	.setCountryCode(COUNTRYCODE.SE)		   				//Required
    .setNumberOfCreditDays(1)              				//Use for invoice orders.
    .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)  //Use for invoice orders. DISTRIBUTIONTYPE see APPENDIX
    .setCreditInvoice()                    				//Use for invoice orders, if this should be a credit invoice.   
```

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 8. Credit Invoice
When you want to credit an invoice. The order must first be delivered. When doing [DeliverOrder](https://github.com/sveawebpay/java-integration/tree/master#7-deliverorder)
you will recieve an *InvoiceId* in the Response. To credit the invoice you follow the steps as in [7. DeliverOrder](https://github.com/sveawebpay/java-integration/tree/master#7-deliverorder)
 but you add the call `.setCreditInvoice(invoiceId)`:

```java
	DeliverOrderResponse response = Webpay.deliverOrder()
    .addOrderRow(
        Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0)
        )
        .setOrderId(1234) //Recieved from CreateOrder request
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        //Credit invoice flag. Note that you first must deliver the order and recieve an InvoiceId, then do the deliver request again but with this call:
        .setCreditInvoice(4321) //Use for invoice orders, if this should be a credit invoice. Params: InvoiceId recieved from when doing deliverOrder
        .deliverInvoiceOrder()
            .doRequest();
```

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 9. closeOrder                                                             
Use when you want to cancel an undelivered order. Valid only for invoice and payment plan orders. 
Required is the order id received when creating the order. Set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### 9.1 Close by payment type                                                
```java
    .closeInvoiceOrder()
or
    .closePaymentPlanOrder()
```

```java
CloseOrderResponse  =  WebPay.closeOrder(
	.setOrderId(orderId)						//Required, received when creating an order.
	.closeInvoiceOrder()		
		.setCountryCode(COUNTRYCODE.SE)			//Required		
		.doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## 10. Response handler                                                       
All synchronous responses are handled through *SveaResponse* and structured into objects.
Asynchronous responses recieved after sending the values *merchantid* and *xmlMessageBase64* to
hosted solutions can also be processed through the *SveaResponse* class.

The response from server will be sent to the *returnUrl* with POST or GET. The response contains the parameters: 
*response* and *merchantid*.
Class *SveaResponse* will return an object structured similar to the synchronous answer.

Params: 
* The POST or GET message Base64 encoded
* Your *secret word*. 
```java
	SveaRespons respObject = new SveaResponse(responseXmlBase64, mac, secretWord); 
```

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

## APPENDIX 

### PaymentMethods
Enumeration, used in *usePaymentMethod(PAYMENTMETHOD paymentMethod)*, *.usePayPage()*, 
*.includePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods)*, *.includePaymentMethods()*, 
*.excludePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods)*,
*.excludeDirectPaymentMethods()* and *.excludeCardPaymentMethods()*.

| Payment method                   | Description                                   |
|----------------------------------|-----------------------------------------------|
| PAYMENTMETHOD.BANKAXESS	       | Direct bank payment, Norway.       		   | 
| PAYMENTMETHOD.NORDEA_SE	       | Direct bank payment, Nordea, Sweden.          | 
| PAYMENTMETHOD.SEB_SE	           | Direct bank payment, private, SEB, Sweden.    |
| PAYMENTMETHOD.SEBFTG_SE 	       | Direct bank payment, company, SEB, Sweden.    |
| PAYMENTMETHOD.SHB_SE	           | Direct bank payment, Handelsbanken, Sweden.   |
| PAYMENTMETHOD.SWEDBANK_SE	       | Direct bank payment, Swedbank, Sweden.        |
| PAYMENTMETHOD.KORTCERT           | Card payments, Certitrade.                    |
| PAYMENTMETHOD.PAYPAL             | Paypal                                        |
| PAYMENTMETHOD.SKRILL             | Card payment with Dankort, Skrill.            |
| PAYMENTMETHOD.INVOICE			   | Invoice by PayPage.                		   |
| PAYMENTMETHOD.PAYMENTPLAN        | PaymentPlan by PayPage.			           |


[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### CountryCode
Enumeration, used in .setCountryCode(...). Using ISO 3166-1 standard. 

| CountryCode						| Country					|
|-----------------------------------|---------------------------|
| COUNTRYCODE.DE					| Germany					|
| COUNTRYCODE.DK					| Denmark 					|
| COUNTRYCODE.FI					| Finland					|
| COUNTRYCODE.NL					| Netherlands				|
| COUNTRYCODE.NO					| Norway					|
| COUNTRYCODE.SE					| Sweden					|

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### LanguageCode
Enumeration, used in .setPayPageLanguage(...). Using ISO 639-1 standard. 

| LanguageCode						| Language name				|
|-----------------------------------|---------------------------|
| LANGUAGECODE.da					| Danish					|
| LANGUAGECODE.de					| German					|
| LANGUAGECODE.en					| English					|
| LANGUAGECODE.es					| Spanish					|
| LANGUAGECODE.fr					| French					|
| LANGUAGECODE.fi					| Finnish					|
| LANGUAGECODE.it					| Italian					|
| LANGUAGECODE.nl					| Dutch						|
| LANGUAGECODE.no					| Norwegian					|
| LANGUAGECODE.sv					| Swedish					|

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### Currency 
Enumeration, used in .setCurrency(...). Using ISO 4217 standard.

| CurrencyCode						| Currency name				|
|-----------------------------------|---------------------------|
| CURRENCY.DKK						| Danish krone				|
| CURRENCY.EUR						| Euro						|
| CURRENCY.NOK						| Norwegian krone			|
| CURRENCY.SEK						| Swedish krona				|

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)

### Invoice Distribution Type 
Enumeration, used in .setInvoiceDistributionType(...).

| DistributionType					| Description				|
|-----------------------------------|---------------------------|
| Post								| Invoice is sent by mail	|
| Email								| Invoice is sent by e-mail	|

[<< To top](https://github.com/sveawebpay/java-integration/tree/master#java-integration-package-api-for-sveawebpay)


=============
Below documentation is to be regarded as a work in progress:

# Java integration package release 1.6.0
## Administration of hosted service (i.e. card and direct bank payment methods) orders:

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

   



































## x.x Examples
The provided examples show how to use the Svea java integration package to specify an order and send a payment request to Svea.

### x.x.1 Running the examples
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

### x.x.2 Svea invoice order
An example of a synchronous (invoice) order can be found in the example/invoiceorder folder.

###x.x.3 Card order
An example of an asynchronous card order can be found in the example/cardorder folder.





