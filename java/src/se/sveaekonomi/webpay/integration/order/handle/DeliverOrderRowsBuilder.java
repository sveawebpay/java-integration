package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.LowerTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */

public class DeliverOrderRowsBuilder extends OrderBuilder<DeliverOrderRowsBuilder> {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

    private ArrayList<Integer> rowIndexesToDeliver;
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
	private ArrayList<OrderRowBuilder> newOrderRows;
    private String orderId;

	public DeliverOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.rowIndexesToDeliver = new ArrayList<Integer>();
		this.numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		this.newOrderRows = new ArrayList<OrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public DeliverOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}

	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	public DeliverOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public ArrayList<Integer> getRowsToDeliver() {
		return rowIndexesToDeliver;
	}

	public DeliverOrderRowsBuilder setRowToDeliver( int rowIndexToDeliver ) {
		this.rowIndexesToDeliver.add(rowIndexToDeliver);
		return this;
	}

	public DeliverOrderRowsBuilder setRowsToDeliver(ArrayList<Integer> rowIndexesToDeliver) {
		this.rowIndexesToDeliver = rowIndexesToDeliver;
		return this;
	}

	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return numberedOrderRows;
	}

	public DeliverOrderRowsBuilder addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows = numberedOrderRows;
		return this;
	}

	public String getOrderId() {
		return orderId;
	}

	public DeliverOrderRowsBuilder setOrderId(String orderId) {
		this.orderId = orderId;
		return this;
	}
	
	/**
	 * optional, card only -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return DeliverOrderRowsBuilder
	 */
    public DeliverOrderRowsBuilder setTransactionId( String transactionId) {        
        return setOrderId( transactionId );
    }   
	
	public LowerTransactionRequest deliverCardOrderRows() {
	
		//validateDeliverCardOrderRows();
		
		// calculate original order rows total, incvat row sum over numberedOrderRows
		double originalOrderTotal = 0.0;
		for( NumberedOrderRowBuilder originalRow : numberedOrderRows ) {
			originalOrderTotal += originalRow.getAmountExVat() * (1+originalRow.getVatPercent()/100.0); 	// TODO change to method, backport to php
		}
		
		// calculate delivered order rows total, incvat row sum over deliveredOrderRows + newOrderRows
		double deliveredOrderTotal = 0.0;
		for( Integer rowIndex : new HashSet<Integer>(rowIndexesToDeliver) ) {
			NumberedOrderRowBuilder deliveredRow = numberedOrderRows.get(rowIndex-1);	// -1 as NumberedOrderRows is one-indexed
			deliveredOrderTotal +=  deliveredRow.getAmountExVat() * (1+deliveredRow.getVatPercent()/100.0);
		}		
		for( OrderRowBuilder newRow : newOrderRows ) {
			deliveredOrderTotal += newRow.getAmountExVat() * (1+newRow.getVatPercent()/100.0);
		}
		
		double amountToLowerOrderBy = originalOrderTotal - deliveredOrderTotal;
				
		LowerTransactionRequest lowerTransactionRequest = new LowerTransactionRequest( this.getConfig() );
		lowerTransactionRequest.setCountryCode( this.getCountryCode() );
		lowerTransactionRequest.setTransactionId( this.getOrderId() );
		lowerTransactionRequest.setAmountToLower( (int)MathUtil.bankersRound(amountToLowerOrderBy) * 100 ); // request uses minor currency );
		lowerTransactionRequest.setAlsoDoConfirm( true );
		
		return( lowerTransactionRequest );				
	}
}