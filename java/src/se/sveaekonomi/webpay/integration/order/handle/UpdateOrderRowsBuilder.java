package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.adminservice.CancelOrderRowsRequest;
import se.sveaekonomi.webpay.integration.adminservice.UpdateOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.LowerTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;

/**
 * @author Kristian Grossman-Madsen
 */

public class UpdateOrderRowsBuilder extends OrderBuilder<UpdateOrderRowsBuilder> {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

	private ArrayList<NumberedOrderRowBuilder> updateOrderRows;
    private String orderId;
	private PAYMENTTYPE orderType;

	public UpdateOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.updateOrderRows = new ArrayList<NumberedOrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public UpdateOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}

	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	public UpdateOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public String getOrderId() {
		return orderId;
	}

	public UpdateOrderRowsBuilder setOrderId(String orderId) {
		this.orderId = orderId;
		return this;
	}		
	
	public UpdateOrderRowsBuilder addUpdateOrderRow(NumberedOrderRowBuilder updateOrderRow) {
		this.updateOrderRows.add(updateOrderRow);
		return this;
	}	

	public UpdateOrderRowsBuilder addUpdateOrderRows(ArrayList<NumberedOrderRowBuilder> updateOrderRows) {
		this.updateOrderRows.addAll(updateOrderRows);
		return this;
	}

	public ArrayList<NumberedOrderRowBuilder> getUpdateOrderRows() {
		return updateOrderRows;
	}

	public UpdateOrderRowsRequest updateInvoiceOrderRows() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		return new UpdateOrderRowsRequest(this);
	}
	
	public UpdateOrderRowsRequest updatePaymentPlanOrderRows() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		return new UpdateOrderRowsRequest(this);
	}

	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	}
	
}