package se.sveaekonomi.webpay.integration.adminservice;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;

/**
 * DeliverOrdersRequest handles requests to Svea Admin WebService DeliverOrders method
 * 
 * @author Kristian Grossman-Madsen
 */
public class DeliverOrdersRequest implements Requestable {
	
	private String action;
	public DeliverOrderBuilder builder;

    public DeliverOrdersRequest(DeliverOrderBuilder orderBuilder) {
		this.action = "DeliverOrders";
		this.builder = orderBuilder;
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
        
        if (builder.getInvoiceDistributionType() == null) {
            errors += "MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n";
    	}   
        
        if (builder.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode().\n";
        }     		
        if ( !errors.equals("")) {
            throw new ValidationException(errors);
        }
	}

    // TODO
	public String prepareRequest() {
		return "foo";
	};    
    
	// TODO
	public DeliverOrdersResponse doRequest() {
		return new DeliverOrdersResponse();
	};
}
