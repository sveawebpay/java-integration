package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.adminservice.AddOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public class AddOrderRowsBuilder extends OrderBuilder<AddOrderRowsBuilder> {

	private PAYMENTTYPE orderType;

	@SuppressWarnings("rawtypes")
	private ArrayList<OrderRowBuilder> addedOrderRows;
    private Long orderId;


	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	}
    
	public Long getOrderId() {
		return orderId;
	}
	public AddOrderRowsBuilder setOrderId(Long orderId) {
		this.orderId = orderId;
		return this;
	}		
	public ArrayList<OrderRowBuilder> getOrderRows() {
		return addedOrderRows;
	}
	public AddOrderRowsBuilder addOrderRow(OrderRowBuilder addedOrderRow) {
		this.addedOrderRows.add(addedOrderRow);
		return this;
	}	
	public AddOrderRowsBuilder addOrderRows(ArrayList<OrderRowBuilder> addedOrderRows) {
		this.addedOrderRows.addAll(addedOrderRows);
		return this;
	}

	
	public AddOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.addedOrderRows = new ArrayList<OrderRowBuilder>();
	}
	
	public AddOrderRowsRequest addInvoiceOrderRows() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		return new AddOrderRowsRequest(this);
	}
	
	public AddOrderRowsRequest addPaymentPlanOrderRows() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		return new AddOrderRowsRequest(this);
	}	
}