package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.adminservice.AddOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */

public class AddOrderRowsBuilder extends OrderBuilder<AddOrderRowsBuilder> {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

	private ArrayList<OrderRowBuilder> addedOrderRows;
    private Long orderId;
	private PAYMENTTYPE orderType;

	public AddOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.addedOrderRows = new ArrayList<OrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public AddOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}

	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	public AddOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public Long getOrderId() {
		return orderId;
	}

	public AddOrderRowsBuilder setOrderId(Long orderId) {
		this.orderId = orderId;
		return this;
	}		
	
	public AddOrderRowsBuilder addOrderRow(OrderRowBuilder addedOrderRow) {
		this.addedOrderRows.add(addedOrderRow);
		return this;
	}	

	public AddOrderRowsBuilder addOrderRows(ArrayList<OrderRowBuilder> addedOrderRows) {
		this.addedOrderRows.addAll(addedOrderRows);
		return this;
	}

	public ArrayList<OrderRowBuilder> getOrderRows() {
		return addedOrderRows;
	}

	public AddOrderRowsRequest addInvoiceOrderRows() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		return new AddOrderRowsRequest(this);
	}
	
	public AddOrderRowsRequest addPaymentPlanOrderRows() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		return new AddOrderRowsRequest(this);
	}

	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	}
	
}