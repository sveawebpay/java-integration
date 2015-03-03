package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.adminservice.CreditOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.CreditTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public class CreditOrderRowsBuilder extends OrderBuilder<CreditOrderRowsBuilder>{ 

    private ConfigurationProvider config;
    private COUNTRYCODE countryCode;
    private PAYMENTTYPE orderType;
    
	private Long orderId;
	private Long invoiceId;
	private DISTRIBUTIONTYPE invoiceDistributionType;
    
    private ArrayList<Integer> rowIndexesToCredit;
	private ArrayList<NumberedOrderRowBuilder> addedCreditOrderRows;
	@SuppressWarnings("rawtypes")
	private ArrayList<OrderRowBuilder> newCreditOrderRows;

	public Long getOrderId() {
		return orderId;
	}
	public CreditOrderRowsBuilder setOrderId(Long orderId) {
		this.orderId = orderId;
		return this;
	}

	public CreditOrderRowsBuilder setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}
	public Long getInvoiceId() {
		return this.invoiceId;
	}	
	public DISTRIBUTIONTYPE getInvoiceDistributionType() {
		return invoiceDistributionType;
	}
	public CreditOrderRowsBuilder setInvoiceDistributionType(DISTRIBUTIONTYPE invoiceDistributionType) {
		this.invoiceDistributionType = invoiceDistributionType;
		return this;
	}
	public ArrayList<Integer> getRowsToCredit() {
		return rowIndexesToCredit;
	}
	public CreditOrderRowsBuilder setRowToCredit( Integer rowIndexToCredit ) {
		this.rowIndexesToCredit.add(rowIndexToCredit);
		return this;
	}
	public CreditOrderRowsBuilder setRowsToCredit(ArrayList<Integer> rowIndexesToCredit) {
		this.rowIndexesToCredit.addAll(rowIndexesToCredit);
		return this;
	}
	public ArrayList<OrderRowBuilder> getNewCreditOrderRows() {
		return newCreditOrderRows;
	}
	public void setNewCreditOrderRows(ArrayList<OrderRowBuilder> newCreditOrderRows) {
		this.newCreditOrderRows = newCreditOrderRows;
	}
	public CreditOrderRowsBuilder addCreditOrderRow(@SuppressWarnings("rawtypes") OrderRowBuilder customAmountRow) {
		this.newCreditOrderRows.add(customAmountRow);
		return this;
	}	
	public CreditOrderRowsBuilder addCreditOrderRows(@SuppressWarnings("rawtypes") ArrayList<OrderRowBuilder> customAmountRows) {
		this.newCreditOrderRows.addAll(customAmountRows);
		return this;
	}
	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return addedCreditOrderRows;
	}
	public CreditOrderRowsBuilder addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.addedCreditOrderRows.addAll(numberedOrderRows);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public CreditOrderRowsBuilder( ConfigurationProvider config ) {
		this.config = config;
		this.rowIndexesToCredit = new ArrayList<Integer>();
		this.addedCreditOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		this.newCreditOrderRows = new ArrayList<OrderRowBuilder>();
	}

	@Override
	public ConfigurationProvider getConfig() {
		return config;
	}
	public CreditOrderRowsBuilder setConfig(ConfigurationProvider config) {
		this.config = config;
		return this;
	}
	@Override
	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}
	@Override
	public CreditOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	
	/**
	 * optional, card only -- alias for setOrderId
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 */
    public CreditOrderRowsBuilder setTransactionId( String transactionId) {        
        return setOrderId( Long.valueOf(transactionId) );
    }   
	

	public CreditTransactionRequest creditCardOrderRows() {
		this.orderType = PAYMENTTYPE.HOSTED_ADMIN;
	
    	// validate request and throw exception if validation fails
        String errors = validateCreditCardOrderRows(); 
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
		
		double creditedOrderTotal = calculateCreditAmount();			
		
		CreditTransactionRequest creditTransactionRequest = new CreditTransactionRequest( this.getConfig() );
		creditTransactionRequest.setCountryCode( this.getCountryCode() );
		creditTransactionRequest.setTransactionId( String.valueOf(this.getOrderId()) );
		creditTransactionRequest.setCreditAmount((int)MathUtil.bankersRound(creditedOrderTotal) * 100);
		
		return creditTransactionRequest;				
	}
	
	// validates required attributes
    public String validateCreditCardOrderRows() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        
        // need either row indexes or new credit rows to calculate amount to credit
        if( this.rowIndexesToCredit.size() == 0 && this.newCreditOrderRows.size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditCardOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n";
    	}
        
        // iff specified row indexes, need to pass in the order rows as well for orders
        if( this.rowIndexesToCredit.size() > 0 && this.addedCreditOrderRows.size() == 0 ) {
        	errors += "MISSING VALUE - numberedOrderRows is required for creditCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n";
    	}

        return errors;  
    }	
    
    public CreditTransactionRequest creditDirectBankOrderRows() {
		this.orderType = PAYMENTTYPE.HOSTED_ADMIN;
    	
    	// validate request and throw exception if validation fails
        String errors = validateCreditDirectBankOrderRows(); 
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
		
		// calculate credited order rows total, incvat row sum over creditedOrderRows + newOrderRows
		double creditedOrderTotal = calculateCreditAmount();			
		
		CreditTransactionRequest creditTransactionRequest = new CreditTransactionRequest( this.getConfig() );
		creditTransactionRequest.setCountryCode( this.getCountryCode() );
		creditTransactionRequest.setTransactionId( String.valueOf(this.getOrderId()) );
		creditTransactionRequest.setCreditAmount((int)MathUtil.bankersRound(creditedOrderTotal) * 100);
		
		return creditTransactionRequest;				
	}
	
	// validates required attributes
    public String validateCreditDirectBankOrderRows() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        
        // need either row indexes or new credit rows to calculate amount to credit
        if( this.rowIndexesToCredit.size() == 0 && this.newCreditOrderRows.size() == 0 ) {
        	errors += "MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditDirectBankOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n";
    	}
        
        // iff specified row indexes, need to pass in the order rows as well for orders
        if( this.rowIndexesToCredit.size() > 0 && this.addedCreditOrderRows.size() == 0 ) {
        	errors += "MISSING VALUE - numberedOrderRows is required for creditDirectBankOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n";
    	}

        return errors;  
    }	
    
	private double calculateCreditAmount() {
		double creditedOrderTotal = 0.0;
		for( Integer rowIndex : new HashSet<Integer>(rowIndexesToCredit) ) {
			NumberedOrderRowBuilder creditedRow = addedCreditOrderRows.get(rowIndex-1);	// -1 as NumberedOrderRows is one-indexed
			creditedOrderTotal +=  creditedRow.getAmountExVat() * (1+creditedRow.getVatPercent()/100.0) * creditedRow.getQuantity();
		}			

		for( @SuppressWarnings("rawtypes") OrderRowBuilder newCreditRow : newCreditOrderRows ) {
			creditedOrderTotal +=  newCreditRow.getAmountExVat() * (1+newCreditRow.getVatPercent()/100.0) * newCreditRow.getQuantity();
		}
		return creditedOrderTotal;
	}

	public CreditOrderRowsRequest creditInvoiceOrderRows() {
		this.orderType = PAYMENTTYPE.INVOICE;
		return new CreditOrderRowsRequest(this);
	}

	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	} 	
}
