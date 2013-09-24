package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;

public class HandleOrderValidator {

    private String errors = "";
    
    public String validate(DeliverOrderBuilder order) {
        errors = "";
        validateCountry(order);
        validateOrderType(order);
        validateOrderId(order);
        validateInvoiceDetails(order);
        validateOrderRows(order);
        return errors;
    }
    
    private void validateCountry(DeliverOrderBuilder order) {
        if (order.getCountryCode() == null) {
            this.errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
    }
    
    private void validateOrderType(DeliverOrderBuilder order) {
        if (order.getOrderType() == null) {
            this.errors += "MISSING VALUE - OrderType is missing for DeliverOrder, use setOrderType().\n";
        }
    }
    
    private void validateOrderId(DeliverOrderBuilder order) {
        if (order.getOrderId() <= 0) {
            this.errors += "MISSING VALUE - setOrderId is required.\n";
        }
    }
    
    private void validateInvoiceDetails(DeliverOrderBuilder order) {
        if (order.getOrderId() > 0 &&
                order.getOrderType().equals("Invoice") &&
                order.getInvoiceDistributionType() == null) {
            this.errors += "MISSING VALUE - setInvoiceDistributionType is requred for deliverInvoiceOrder.\n";
        }
    }
    
    private void validateOrderRows(DeliverOrderBuilder order) {
        if (order.getOrderType() == "Invoice" &&
                order.getOrderRows().isEmpty() &&
                order.getShippingFeeRows().isEmpty() &&
                order.getInvoiceFeeRows().isEmpty()) {
            this.errors += "MISSING VALUE - No order or fee has been included. Use addOrder(...) or addFee(...).\n";
        }
    }
}
