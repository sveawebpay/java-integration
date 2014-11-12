package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;

public abstract class OrderValidator {

    protected String errors = "";
    
    public abstract String validate(CreateOrderBuilder order);
    
    protected void validateRequiredFieldsForOrder(CreateOrderBuilder order) {
        String errorMessage = "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n";
        try {
            if (order.getOrderRows().size() == 0)
                this.errors += errorMessage;
        } catch (NullPointerException e) {
            this.errors += errorMessage;
        }
    }
    
    protected void validateOrderRow(CreateOrderBuilder order) {
        try {
        	
        	boolean exvat_and_vatpercent_orderrow_found = false;
        	boolean incvat_and_vatpercent_orderrow_found = false;
        	boolean incvat_and_exvat_orderrow_found = false;
        	
        	for (OrderRowBuilder orderRow : order.getOrderRows()) {
                
                if (orderRow.getAmountExVat() != null && orderRow.getVatPercent() != null && orderRow.getAmountIncVat() == null) {
                	exvat_and_vatpercent_orderrow_found = true;
                }
                if (orderRow.getAmountExVat() == null && orderRow.getVatPercent() != null && orderRow.getAmountIncVat() != null) {
                	incvat_and_vatpercent_orderrow_found = true;
                }
                if (orderRow.getAmountExVat() != null && orderRow.getVatPercent() == null && orderRow.getAmountIncVat() != null) {
                	incvat_and_exvat_orderrow_found = true;
                }
        		
                if (orderRow.getQuantity() == null || orderRow.getQuantity() <= 0)
                    errors += "MISSING VALUE - Quantity is required in Item object. Use Item.setQuantity().\n";
                if (orderRow.getAmountExVat() == null && orderRow.getVatPercent() == null && orderRow.getAmountIncVat() == null)
                    errors += "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two of: setAmountExVat(), setAmountIncVat or setVatPercent().\n";
                else if (orderRow.getAmountExVat() != null && orderRow.getVatPercent() == null && orderRow.getAmountIncVat() == null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
                else if (orderRow.getAmountExVat() == null && orderRow.getVatPercent() == null && orderRow.getAmountIncVat() != null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
                else if (orderRow.getAmountExVat() == null && orderRow.getVatPercent() != null && orderRow.getAmountIncVat() == null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";                
            }
        	
        	int no_of_price_specifications_methods_in_order = 
        			(exvat_and_vatpercent_orderrow_found ? 1 : 0) + (incvat_and_vatpercent_orderrow_found ? 1 : 0) + (incvat_and_exvat_orderrow_found ? 1 : 0);
        	
        	if( no_of_price_specifications_methods_in_order > 1 ) {
        		this.errors += "INCOMPATIBLE ORDER ROW PRICE SPECIFICATION - all order rows must have their price specified using the same two methods.\n";
        	}
        	
        } catch (NullPointerException e) {
            this.errors += "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use setAmountExVat(), setQuantity() and setVatPercent().\n";            
        }
    }
}
