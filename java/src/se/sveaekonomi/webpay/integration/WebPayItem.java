package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;

/**
 * Use to specify types of customer, fees, discounts or row.
 * 
 * @author Kristian Grossman-Madsen
 */
public class WebPayItem {

    /**
     * The WebPayItem.orderRow() entrypoint method is used to specify order items like products and services. 
     * It is required to have a minimum of one order row in an order.
     * 
     * Specify the item price using precisely two of these methods in order to specify the item price and tax rate: 
     * setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().
     * 
     *     OrderRowBuilder orderrow = WebPayItem.orderRow()
     *         .setAmountIncVat()		// Double	// optional, recommended, use precisely two of the price specification methods
     *         .setVatPercent()       	// Double	// optional, recommended, use precisely two of the price specification methods
     *         .setAmountExVat()       	// Double	// optional, use precisely two of the price specification methods
     *         .setQuantity()           // Double   // required
     *         .setUnit()               // String	// optional
     *         .setName()               // String	// optional, note that invoice & payment plan orders will merge "name" with "description" 
     *         .setDescription() 		// String	// optional, note that invoice & payment plan orders will merge "name" with "description" 
     *         .setArticleNumber()     	// String	// optional
     *         .setDiscountPercent()    // double 	// optional
     *     );
     */	
	public static OrderRowBuilder orderRow() {
		return new OrderRowBuilder();
	}
	
    /**
     * The WebPayItem.shippingFee() entrypoint method is used to specify order shipping fee rows.
     * It is not required to have a shipping fee row in an order.
     * 
     * Specify the item price using precisely two of these methods in order to specify the item price and tax rate: 
     * setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().
     * 
     *     ShippingFeeBuilder shippingFee = WebPayItem::shippingFee()
     *         ->setAmountIncVat()		// Double 	// optional, recommended, use precisely two of the price specification methods
     *         ->setVatPercent()        // Double	// optional, recommended, use precisely two of the price specification methods
     *         ->setAmountExVat()       // Double 	// optional, use precisely two of the price specification methods
     *         ->setUnit()              // String	// optional
     *         ->setName()              // String	// optional
     *         ->setDescription() 		// String	// optional
     *         ->setShippingId()        // String   // optional
     *         ->setDiscountPercent()   // double 	// optional
     *     );  
	 */	
	public static ShippingFeeBuilder shippingFee() {
		return new ShippingFeeBuilder();
	}

    /**
     * The WebPayItem.invoiceFee() entrypoint method is used to specify fees associated with a payment method (i.e. invoice fee). It is not required to have an invoice fee row in an order.
     * 
     * Specify the item price using precisely two of these methods in order to specify the item price and tax rate: setAmountIncVat(), setVatPercent() and setAmountExVat(). We recommend using setAmountIncVat() and setVatPercentage().
     * 
     *     InvoiceFeeBuilder invoiceFee = WebPayItem.invoiceFee()
     *         ->setAmountIncVat()		// Double 	// optional, recommended, use precisely two of the price specification methods
     *         ->setVatPercent()       	// Double	// optional, recommended, use precisely two of the price specification methods
     *         ->setAmountExVat()      	// Double 	// optional, use precisely two of the price specification methods
     *         ->setUnit()             	// String	// optional
     *         ->setName()             	// String	// optional
     *         ->setDescription() 		// String	// optional
     *         ->setDiscountPercent()  	// double 	// optional
     *     );
	 */	
	public static InvoiceFeeBuilder invoiceFee() {
		return new InvoiceFeeBuilder();
	}

	/**
	 * Use WebPayItem.fixedDiscount() when the discount or coupon is expressed as a fixed discount amount.
     * 
	 * If no vat rate is given, the package will distribute the the discount amount across the different order row vat rates present in the order. This will ensure that the correct discount vat is applied to the order -- if there are several vat rates present in the order, the discount will be split proportionally across the order row vat rates.
	 *  
	 * See the tests for examples of the resulting discount rows and exact behaviour when the discount is specified using setAmountIncVat() and setAmountExVat in orders with different vat rates present.
     * 
	 * Specify the discount using setAmountIncVat(), setVatPercent() and setAmountExVat().  If two of these three attributes are specified, we honour the amount indicated and the given discount tax rate; if so we recommend using setAmountIncVat() and setVatPercentage().
     * 
	 *      FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
	 *          .setAmountIncVat()   	// Double	// recommended, see info above
	 *          .setAmountExVat()     	// Double	// optional, see info above
	 *          .setVatPercent)        	// Double	// optional, see info above
	 *          .setUnit()             	// String	// optional
	 *          .setName()             	// String	// optional
	 *          .setDescription()      	// String	// optional
	 *          .setDiscountId()       	// String	// optional
	 *      );
	 */
	public static FixedDiscountBuilder fixedDiscount() {
		return new FixedDiscountBuilder();
	}

	/**	
	 * Use WebPayItem.relativeDiscount() when the discount or coupon is expressed as a percentage of the total product amount.
     * 
	 * The discount will be calculated based on the total sum of all order rows specified using .addOrderRow(), it does not apply to invoice or shipping fees.
     * 
	 * The package will distribute the the discount amount across the different order row vat rates present in the order. This will ensure that the correct discount vat is applied to the order -- if there are several vat rates present in the order, the discount will be split across the order row vat rates. 
     * 
	 * Specify the discount using RelativeDiscountBuilder methods:
     * 
	 *      RelativeDiscountBuilder relativeDiscount = WebPayItem.relativeDiscount()
	 *          .setDiscountPercent()  	// double	// recommended, see info above
	 *          .setAmountExVat()		// Double	// optional, see info above
	 *          .setUnit()             	// String	// optional
	 *          .setName()             	// String	// optional
	 *          .setDescription()      	// String	// optional
	 *          .setDiscountId()       	// String	// optional
	 *      );
	 */
	public static RelativeDiscountBuilder relativeDiscount() {
		return new RelativeDiscountBuilder();
	}
	
	/**
	 * Use WebPayItem.individualCustomer() to add individual customer information to an order.
	 * 
	 * Note that "required" below as a requirement only when using the invoice or payment plan payment methods, and that the required attributes vary between countries.
	 * (For card and direct bank orders, adding customer information to the order is optional, unless you're using getPaymentUrl() to set up a prepared payment.)
     * 
	 * 	    IndividualCustomer individual = WebPayItem.individualCustomer()
	 * 	    	.setNationalIdNumber()	// String	// invoice, paymentplan: required for individual customers in SE, NO, DK, FI
	 * 	    	.setName()       		// String	// invoice, paymentplan: required (firstname, lastname) for individual customers in NL and DE 
	 * 	    	.setBirthDate()        	// String	// invoice, paymentplan: required for individual customers in NL and DE
	 * 	    	.setInitials()         	// String	// invoice, paymentplan: required for individual customers in NL
	 * 	    	.setCoAddress()      	// String	// invoice, paymentplan: optional
	 * 	    	.setStreetAddress()     // String	// invoice, paymentplan: required (street, housenumber) for individual customers in NL and DE 
	 * 	    	.setZipCode)            // String	// invoice, paymentplan: required in NL and DE
	 * 	    	.setLocality()          // String	// invoice, paymentplan: required in NL and DE
	 * 	    	.setPhoneNumber()       // String	// invoice, paymentplan: optional but desirable
	 * 	    	.setEmail()         	// String	// invoice, paymentplan: optional but desirable
	 * 	    	.setIpAddress()       	// String	// invoice, paymentplan: optional but desirable; card: required for getPaymentUrl() orders only		
	 * 	    ;
	 */
	public static IndividualCustomer individualCustomer() {
		return new IndividualCustomer();
	}

	/**
	 * Use WebPayItem.companyCustomer() to add customer information to an order.
	 * 
	 * Note that "required" below as a requirement only when using the invoice payment methods, and that the required attributes vary between countries.
	 * (For card and direct bank orders, adding customer information to the order is optional, unless you're using getPaymentUrl() to set up a prepared payment.)
	 * 
	 *		CompanyCustomer company = WebPayItem.companyCustomer()
	 * 		    .setNationalIdNumber()  // String	// invoice: required in SE, NO, DK, FI
	 *	 	    .setCompanyName()  		// String	// invoice: required (companyname) for company customers in NL and DE
	 * 	    	.setVatNumber()         // String	// invoice: required in NL and DE
	 * 		    .setStreetAddress()     // String	// invoice: required in NL and DE
	 *	 	    .setCoAddress()      	// String	// invoice: optional
	 * 	    	.setZipCode()           // String	// invoice: required in NL and DE
	 * 		    .setLocality()          // String	// invoice: required in NL and DE
	 *	 	    .setPhoneNumber()       // String	// invoice: optional but desirable
	 * 	    	.setEmail()         	// String	// invoice: optional but desirable
	 * 		    .setIpAddress()       	// String	// invoice: optional but desirable; card: required for getPaymentUrl() orders only
	 *	 	    .setAddressSelector()   // String	// invoice: optional but recommended; recieved from WebPay.getAddresses() request response
	 * 		;
	*/
	public static CompanyCustomer companyCustomer() {
		return new CompanyCustomer();
	}
	
	
    /**
     * This is an extension of the orderRow class, used in the WebPayAdmin.queryOrder() response and methods that adminster individual order rows.
     * 
     * NumberedOrderRow is returned with the WebPayAdmin.queryOrder() responses.
     * Also used when supplying NumberedOrderRow items for the various WebPayAdmin 
     * order row administration functions.
     * 
     * 		NumberedOrderRow myNumberedOrderRow = WebPayItem.numberedOrderRow()
	 *      	//inherited from OrderRow
	 *      	.setAmountExVat()               	// recommended to specify price using AmountExVat & VatPercent
	 *      	.setVatPercent()                    // recommended to specify price using AmountExVat & VatPercent
	 *      	.setAmountIncVat()              	// optional, need to use two out of three of the price specification methods
	 *      	.setQuantity()                      // required
	 *      	.setUnit()                         	// optional
	 *      	.setName()                      	// optional
	 *      	.setDescription()       			// optional
	 *      	.setArticleNumber()                 // optional
	 *      	.setDiscountPercent()               // optional
	 *      	//unique to numberedOrderRow
	 *      	.setCreditInvoiceId()         		// optional
	 *      	.setInvoiceId()                     // optional
	 *      	.setRowNumber()                     // optional    
	 *      	.setStatus() 						// optional, an ORDERROWSTATUS
     * 
     * @return NumberedOrderRow
     */
	public static NumberedOrderRowBuilder numberedOrderRow() {
		return new NumberedOrderRowBuilder();
	}



}
