package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;


public class Item {
    
    public static OrderRowBuilder orderRow(){
        return new OrderRowBuilder();
   }
    
   public static IndividualCustomer individualCustomer() {
       return new IndividualCustomer();
   }
   
   public static CompanyCustomer companyCustomer() {
       return new CompanyCustomer();
   }
   
   public static ShippingFeeBuilder shippingFee(){
       return new ShippingFeeBuilder();
   }
   
   public static InvoiceFeeBuilder invoiceFee(){
       return new InvoiceFeeBuilder();
   }
   
   public static FixedDiscountBuilder fixedDiscount(){
       return new FixedDiscountBuilder();
   }
   
   public static RelativeDiscountBuilder relativeDiscount(){
       return new RelativeDiscountBuilder();
   }
}
