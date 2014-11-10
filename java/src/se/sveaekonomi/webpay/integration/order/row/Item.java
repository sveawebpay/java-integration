package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;

/**
 * @deprecated -- use class WebPayItem methods instead.
 * @author klar-sar
 */
public class Item {
    
    public static OrderRowBuilder orderRow() {
        return WebPayItem.orderRow();
   }
    
   public static IndividualCustomer individualCustomer() {
       return WebPayItem.individualCustomer();
   }
   
   public static CompanyCustomer companyCustomer() {
       return WebPayItem.companyCustomer();
   }
   
   public static ShippingFeeBuilder shippingFee() {
       return WebPayItem.shippingFee();
   }
   
   public static InvoiceFeeBuilder invoiceFee() {
       return WebPayItem.invoiceFee();
   }
   
   public static FixedDiscountBuilder fixedDiscount() {
       return WebPayItem.fixedDiscount();
   }
   
   public static RelativeDiscountBuilder relativeDiscount() {
       return WebPayItem.relativeDiscount();
   }
}
