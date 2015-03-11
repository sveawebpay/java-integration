package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.adminservice.UpdateOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public class UpdateOrderRowsBuilder extends OrderBuilder<UpdateOrderRowsBuilder> {
	
	private PAYMENTTYPE orderType;

	private Long orderId;
	private ArrayList<NumberedOrderRowBuilder> updateOrderRows;

	
	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	}	

	public Long getOrderId() {
		return orderId;
	}
	public UpdateOrderRowsBuilder setOrderId(Long orderId) {
		this.orderId = orderId;
		return this;
	}
	public ArrayList<NumberedOrderRowBuilder> getUpdateOrderRows() {
		return updateOrderRows;
	}
	public UpdateOrderRowsBuilder addUpdateOrderRow(NumberedOrderRowBuilder updateOrderRow) {
		this.updateOrderRows.add(updateOrderRow);
		return this;
	}	
	public UpdateOrderRowsBuilder addUpdateOrderRows(ArrayList<NumberedOrderRowBuilder> updateOrderRows) {
		this.updateOrderRows.addAll(updateOrderRows);
		return this;
	}
	
	
	public UpdateOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.updateOrderRows = new ArrayList<NumberedOrderRowBuilder>();
	}
	
	public UpdateOrderRowsRequest updateInvoiceOrderRows() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		return new UpdateOrderRowsRequest(this);
	}
	
	public UpdateOrderRowsRequest updatePaymentPlanOrderRows() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		return new UpdateOrderRowsRequest(this);
	}
}