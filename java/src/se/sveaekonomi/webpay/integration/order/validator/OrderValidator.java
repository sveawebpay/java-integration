package se.sveaekonomi.webpay.integration.order.validator;


import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;

public abstract class OrderValidator {
    protected String errors = "";
    
    public abstract String validate(CreateOrderBuilder order);
    
    protected void validateRequiredFieldsForOrder(CreateOrderBuilder order) {
        String errorMessage = "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n";
        try {             
            if (order.getOrderRows().size() == 0)
                this.errors += errorMessage;            
        } catch (NullPointerException e) {
            this.errors += errorMessage;
        }        
    }
    
    protected void validateOrderRow(CreateOrderBuilder order) {   
        try {
            for (OrderRowBuilder orderRow : order.getOrderRows()) {
                if(orderRow.getQuantity() <= 0)
                    errors += "MISSING VALUE - Quantity is required in Item object. Use function Item.setQuantity().\n";
                
                if(orderRow.getAmountExVat() <= 0 && orderRow.getVatPercent() <= 0 && orderRow.getAmountIncVat() <= 0)
                    errors += "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two functions of: setAmountExVat(), setAmountIncVat or setVatPercent().\n";
                else if (orderRow.getAmountExVat() > 0 && orderRow.getVatPercent() <= 0 && orderRow.getAmountIncVat() <= 0)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
                else if (orderRow.getAmountExVat() <= 0 && orderRow.getVatPercent() <= 0 && orderRow.getAmountIncVat() > 0)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
                else if (orderRow.getAmountExVat() <= 0 && orderRow.getVatPercent() > 0 && orderRow.getAmountIncVat() <= 0)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";                             
            }
        } catch (NullPointerException e) {
            this.errors += "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use functions setAmountExVat(), setQuantity() and setVatPercent().\n";            
        }
    }
}
