package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.LowerTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */

public class CancelOrderRowsBuilder extends OrderBuilder<CancelOrderRowsBuilder> {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

    private ArrayList<Integer> rowIndexesToCancel;
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
    private String orderId;

	public CancelOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.rowIndexesToCancel = new ArrayList<Integer>();
		this.numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public CancelOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}

	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	public CancelOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public ArrayList<Integer> getRowsToCancel() {
		return rowIndexesToCancel;
	}

	public CancelOrderRowsBuilder setRowToCancel( int rowIndexToCancel ) {
		this.rowIndexesToCancel.add(rowIndexToCancel);
		return this;
	}

	public CancelOrderRowsBuilder setRowsToCancel(ArrayList<Integer> rowIndexesToCancel) {
		this.rowIndexesToCancel.addAll(rowIndexesToCancel);
		return this;
	}

	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return numberedOrderRows;
	}

	public CancelOrderRowsBuilder addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows.addAll(numberedOrderRows);
		return this;
	}

	public String getOrderId() {
		return orderId;
	}

	public CancelOrderRowsBuilder setOrderId(String orderId) {
		this.orderId = orderId;
		return this;
	}
	
	/**
	 * optional, card only -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return DeliverOrderRowsBuilder
	 */
    public CancelOrderRowsBuilder setTransactionId( String transactionId) {        
        return setOrderId( transactionId );
    }   
	

	public LowerTransactionRequest cancelCardOrderRows() {
	
    	// validate request and throw exception if validation fails
        String errors = validateCancelCardOrderRows(); 
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
				
		// calculate cancelled order rows total, incvat row sum over cancelledOrderRows + newOrderRows
		double cancelledOrderTotal = 0.0;
		for( Integer rowIndex : new HashSet<Integer>(rowIndexesToCancel) ) {
			NumberedOrderRowBuilder cancelledRow = numberedOrderRows.get(rowIndex-1);	// -1 as NumberedOrderRows is one-indexed
			cancelledOrderTotal +=  cancelledRow.getAmountExVat() * (1+cancelledRow.getVatPercent()/100.0) * cancelledRow.getQuantity();
		}			
		
		double amountToLowerOrderBy = cancelledOrderTotal;
				
		LowerTransactionRequest lowerTransactionRequest = new LowerTransactionRequest( this.getConfig() );
		lowerTransactionRequest.setCountryCode( this.getCountryCode() );
		lowerTransactionRequest.setTransactionId( this.getOrderId() );
		lowerTransactionRequest.setAmountToLower( (int)MathUtil.bankersRound(amountToLowerOrderBy) * 100 ); // request uses minor currency );
		
		return lowerTransactionRequest;				
	}
	
	// validates required attributes
    public String validateCancelCardOrderRows() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        
        if( this.rowIndexesToCancel.size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToCancel is required for cancelCardOrderRows(). Use methods setRowToCancel() or setRowsToCancel().\n";
    	}
        
        if( this.numberedOrderRows.size() == 0 ) {
        	errors += "MISSING VALUE - numberedOrderRows is required for cancelCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n";
    	}

        return errors;  
    }
}