package se.sveaekonomi.webpay.integration.order.validator;

import java.util.HashMap;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;

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
        	// check order rows
        	for (OrderRowBuilder row : order.getOrderRows()) {
        		
                if (row.getQuantity() == null || row.getQuantity() <= 0)
                    errors += "MISSING VALUE - Quantity is required in Item object. Use Item.setQuantity().\n";
                if (row.getAmountExVat() == null && row.getVatPercent() == null && row.getAmountIncVat() == null)
                    errors += "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two of: setAmountExVat(), setAmountIncVat or setVatPercent().\n";
                else if (row.getAmountExVat() != null && row.getVatPercent() == null && row.getAmountIncVat() == null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
                else if (row.getAmountExVat() == null && row.getVatPercent() == null && row.getAmountIncVat() != null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
                else if (row.getAmountExVat() == null && row.getVatPercent() != null && row.getAmountIncVat() == null)
                    errors += "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";
        	}            	
        } catch (NullPointerException e) {
            this.errors += "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use setAmountExVat(), setQuantity() and setVatPercent().\n";            
        }
    }


}
