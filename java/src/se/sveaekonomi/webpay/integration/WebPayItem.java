package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;

/**
 * Use to specify types of customer, fees, discounts or row.
 * @author klar-sar, Kristian Grossman-Madsen
 */
public class WebPayItem {
    
    public static OrderRowBuilder orderRow() {
        return new OrderRowBuilder();
   }
    
   public static IndividualCustomer individualCustomer() {
       return new IndividualCustomer();
   }
   
   public static CompanyCustomer companyCustomer() {
       return new CompanyCustomer();
   }
   
   public static ShippingFeeBuilder shippingFee() {
       return new ShippingFeeBuilder();
   }
   
   public static InvoiceFeeBuilder invoiceFee() {
       return new InvoiceFeeBuilder();
   }
   
   public static FixedDiscountBuilder fixedDiscount() {
       return new FixedDiscountBuilder();
   }
   
   public static RelativeDiscountBuilder relativeDiscount() {
       return new RelativeDiscountBuilder();
   }
}
