package se.sveaekonomi.webpay.integration.adminservice;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;

public class CancelOrderRowsRequest {

	private String action;
	private CancelOrderRowsBuilder builder;
		
	public CancelOrderRowsRequest( CancelOrderRowsBuilder builder) {
		this.action = "CancelOrderRows";
		this.builder = builder;
	}	
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @throws ValidationException
	 */
	public void validateOrder() throws ValidationException {
    	String errors = "";
        if (builder.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}     
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }           
        if( builder.getRowsToCancel().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToDeliver is required for deliverInvoiceOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n";
    	}
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}	
	
}
