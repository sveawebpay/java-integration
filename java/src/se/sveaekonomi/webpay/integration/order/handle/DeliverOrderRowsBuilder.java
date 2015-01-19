package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.adminservice.DeliverOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */

public class DeliverOrderRowsBuilder extends OrderBuilder<DeliverOrderRowsBuilder> {
	
    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;

    private ArrayList<Integer> rowIndexesToDeliver;
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
    private String orderId;
    private String captureDate;
    private DISTRIBUTIONTYPE invoiceDistributionType;
    protected PAYMENTTYPE orderType;
    
	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	} 
    
	public DISTRIBUTIONTYPE getInvoiceDistributionType() {
		return invoiceDistributionType;
	}

	public DeliverOrderRowsBuilder setInvoiceDistributionType(DISTRIBUTIONTYPE invoiceDistributionType) {
		this.invoiceDistributionType = invoiceDistributionType;
		return this;
	}

	public DeliverOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.rowIndexesToDeliver = new ArrayList<Integer>();
		this.numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
	}

	public ConfigurationProvider getConfig() {
		return config;
	}

	public DeliverOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}

	public COUNTRYCODE getCountryCode() {
		return this.countryCode;
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
		this.rowIndexesToDeliver.addAll(rowIndexesToDeliver);
		return this;
	}

	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return numberedOrderRows;
	}

	public DeliverOrderRowsBuilder addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows.addAll(numberedOrderRows);
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
	
	/**
	 * card only, optional
	 * @param date -- date on format YYYY-MM-DD (similar to ISO8601 date)
	 * @return DeliverOrderBuilder
	 */
	public DeliverOrderRowsBuilder setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
		return this;
	}
	
    public String getCaptureDate() {
        return captureDate;
    }
    
	public ArrayList<Integer> getRowIndexesToDeliver() {
		return rowIndexesToDeliver;
	}

	public void setRowIndexesToDeliver(ArrayList<Integer> rowIndexesToDeliver) {
		this.rowIndexesToDeliver = rowIndexesToDeliver;
	}

    public DeliverOrderRowsRequest deliverInvoiceOrderRows() {
    	this.orderType = PAYMENTTYPE.INVOICE;
        return new DeliverOrderRowsRequest(this);
    }
 
    // TODO move setting hosted admin request attributes to request class, pass in entire builder object
    // then move validation to request class
	public ConfirmTransactionRequest deliverCardOrderRows() {
	
    	// validate request and throw exception if validation fails
        String errors = validateDeliverCardOrderRows(); 
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
					
        // if no captureDate set, use today's date as default.
        if( this.getCaptureDate() == null ) {
        	this.setCaptureDate( String.format("%tF", new Date()) ); //'t' => time, 'F' => ISO 8601 complete date formatted as "%tY-%tm-%td"
        }
		
		// calculate original order rows total, incvat row sum over numberedOrderRows
		double originalOrderTotal = 0.0;
		for( NumberedOrderRowBuilder originalRow : numberedOrderRows ) {
			originalOrderTotal += originalRow.getAmountExVat() * (1+originalRow.getVatPercent()/100.0) * originalRow.getQuantity(); // TODO change to method, backport to php
		}
		
		// calculate delivered order rows total, incvat row sum over deliveredOrderRows
		double deliveredOrderTotal = 0.0;
		for( Integer rowIndex : new HashSet<Integer>(rowIndexesToDeliver) ) {
			NumberedOrderRowBuilder deliveredRow = numberedOrderRows.get(rowIndex-1);	// -1 as NumberedOrderRows is one-indexed
			deliveredOrderTotal +=  deliveredRow.getAmountExVat() * (1+deliveredRow.getVatPercent()/100.0) * deliveredRow.getQuantity();
		}			
		
		double amountToLowerOrderBy = originalOrderTotal - deliveredOrderTotal;
				
		ConfirmTransactionRequest confirmTransactionRequest = new ConfirmTransactionRequest( this.getConfig() );
		confirmTransactionRequest.setCaptureDate(this.getCaptureDate());
		confirmTransactionRequest.setCountryCode( this.getCountryCode() );
		confirmTransactionRequest.setTransactionId( this.getOrderId() );
		confirmTransactionRequest.setAlsoDoLowerAmount( (int)MathUtil.bankersRound(amountToLowerOrderBy) * 100 ); // request uses minor currency );
		
		return confirmTransactionRequest;				
	}
	
    public String validateDeliverCardOrderRows() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        
        if( this.getRowIndexesToDeliver().size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToDeliver is required for deliverCardOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n";
    	}
        
        if( this.getNumberedOrderRows().size() == 0 ) {
        	errors += "MISSING VALUE - numberedOrderRows is required for deliverCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n";
    	}

        return errors;  
    }

    
}