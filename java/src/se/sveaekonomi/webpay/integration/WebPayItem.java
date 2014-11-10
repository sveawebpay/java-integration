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

	// TODO check this with test
    /**
     * The WebPayItem.orderRow() entrypoint method is used to specify order items like products and services. 
     * It is required to have a minimum of one order row in an order.
     * 
     * Specify the item price using precisely two of these methods in order to specify the item price and tax rate: 
     * setAmountExVat(), setAmountIncVat() and setVatPercent(). We recommend using setAmountExVat() and setVatPercentage().
     * 
	 *	
     *      OrderRowBuilder orderrow = WebPayItem.orderRow()
     *          .setAmountExVat(100.00)        // optional, recommended, use precisely two of the price specification methods
     *          .setVatPercent(25)             // optional, recommended, use precisely two of the price specification methods
     *          .setAmountIncVat(125.00)       // optional, use precisely two of the price specification methods
     *          .setQuantity(2)                // required
     *          .setUnit("pcs.")               // optional
     *          .setName('name')               // optional, invoice & payment plan orders will merge "name" with "description" 
     *          .setDescription("description") // optional, invoice & payment plan orders will merge "name" with "description" 
     *          .setArticleNumber("1")         // optional
     *          .setDiscountPercent(0)         // optional
     *      );
     * 
     * @return OrderRowBuilder
     */	
	public static OrderRowBuilder orderRow() {
		return new OrderRowBuilder();
	}
	
	// TODO check this with test
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

	// TODO document
	public static IndividualCustomer individualCustomer() {
		return new IndividualCustomer();
	}

	// TODO document
	public static CompanyCustomer companyCustomer() {
		return new CompanyCustomer();
	}

	// TODO document
	public static ShippingFeeBuilder shippingFee() {
		return new ShippingFeeBuilder();
	}

	// TODO document	
	public static InvoiceFeeBuilder invoiceFee() {
		return new InvoiceFeeBuilder();
	}

	// TODO document
	public static FixedDiscountBuilder fixedDiscount() {
		return new FixedDiscountBuilder();
	}

	// TODO document
	public static RelativeDiscountBuilder relativeDiscount() {
		return new RelativeDiscountBuilder();
	}
}
