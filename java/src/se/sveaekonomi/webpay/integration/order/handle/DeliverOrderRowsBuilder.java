package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import com.gargoylesoftware.htmlunit.html.xpath.LowerCaseFunction;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */

public class DeliverOrderRowsBuilder {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

    private ArrayList<Integer> rowIndexesToDeliver;
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
	private ArrayList<OrderRowBuilder> newOrderRows;
    private long orderId;

	public DeliverOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.rowIndexesToDeliver = new ArrayList<Integer>();
		this.numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		this.newOrderRows = new ArrayList<OrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public void setConfig(ConfigurationProvider config) {
		this.config = config;
	}

	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
	}

	public ArrayList<Integer> getRowsToDeliver() {
		return rowIndexesToDeliver;
	}

	public void setRowsToDeliver(ArrayList<Integer> rowIndexesToDeliver) {
		this.rowIndexesToDeliver = rowIndexesToDeliver;
	}

	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return numberedOrderRows;
	}

	public void addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows = numberedOrderRows;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public LowerTransactionRequest deliverCardOrderRows() {
		
		
		validateDeliverCardOrderRows();
		
		// calculate original order rows total, incvat row sum over numberedOrderRows
		double originalOrderTotal = 0.0;
		for( NumberedOrderRowBuilder originalRow : numberedOrderRows ) {
			originalOrderTotal += originalRow.getAmountExVat() * (1+originalRow.getVatPercent()/100.0); 	// TODO change to method, backport to php
		}
		
		// calculate delivered order rows total, incvat row sum over deliveredOrderRows + newOrderRows
		double deliveredOrderTotal = 0.0;
		for( Integer rowIndex : new HashSet<Integer>(rowIndexesToDeliver) ) {
			NumberedOrderRowBuilder deliveredRow = numberedOrderRows.get(rowIndex);
			deliveredOrderTotal +=  deliveredRow.getAmountExVat() * (1+deliveredRow.getVatPercent()/100.0);
		}		
		for( OrderRowBuilder newRow : newOrderRows ) {
			deliveredOrderTotal += newRow.getAmountExVat() * (1+newRow.getVatPercent()/100.0);
		}
		
		double amountToLowerOrderBy = originalOrderTotal - deliveredOrderTotal;
		amountToLowerOrderBy *=100; // request uses minor currency

		LowerTransactionRequest lowerTransactionRequest = new LowerTransactionRequest( this.config );
		lowerTransactionRequest.setCountryCode( this.countryCode );
		lowerTransactionRequest.setTransactionId( this.orderId );
		lowerTransactionRequest.setAmountToLower( amountToLowerOrderBy );
		lowerTransactionRequest.setAlsoDoConfirm( true );
				
	}
	
	
	
}