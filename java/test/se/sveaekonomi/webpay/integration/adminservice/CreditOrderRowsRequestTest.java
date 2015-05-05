package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.bind.ValidationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.adminservice.CreditOrderRowsRequest;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.CreditTransactionRequest;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CreditOrderRowsRequestTest {

	/// validation
	// invoice
	@Test 
    public void test_validates_all_required_methods_for_creditOrderRows_creditInvoiceOrderRows_with_setRowToCredit() {
    	
        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setInvoiceId( 999999L )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
            .setRowToCredit(1)
		;
        
		// validate the order and throws SveaWebPayException on validation failure
		try {
			CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
			fail("Unexpected ValidationException.");
		}	 				
    }
	@Test 
    public void test_validates_all_required_methods_for_creditOrderRows_creditInvoiceOrderRows_with_addCreditOrderRow() {
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;

        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setInvoiceId( 999999L )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
    		.addCreditOrderRow(customAmountRow)            
		;
        
		// validate the order and throws SveaWebPayException on validation failure
		try {
			CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
			fail("Expected ValidationException missing.");
		}	 				
    }   
	@Test
	public void test_validates_missing_required_methods_for_creditOrderRows_creditInvoiceOrderRows() {

	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
			//.setInvoiceId( 999999L )
			//.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
	        //.setCountryCode( COUNTRYCODE.SE )
	        //.setRowToCredit(1)
	    	//.addCreditOrderRow(customAmountRow)            
		;

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
			request.validateOrder();
			// fail if validation passes
	        fail();	
        }
		catch (ValidationException e){			
			assertEquals( 
        		"MISSING VALUE - InvoiceId is required, use setInvoiceId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n" +
        		"MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n" +
        		"MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditDirectBankOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n",
    			e.getMessage()
    		);
        }	
	}	

	// card	
	// .creditCardOrderRow validation
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditCardOrderRows_with_setRowToCredit() {
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	.addNumberedOrderRows( rows )
		    //.addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditCardOrderRows_with_addRowToCredit() {
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
		    .addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditCardOrderRows_with_setRowToCredit() { 	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	//.setOrderId( 123456L )
	    	//.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
    	;
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();
		}
		catch (SveaWebPayException e){							
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditCardOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n",
        		e.getCause().getMessage()
    		);			
        }
    }
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditCardOrderRows_with_setRowToCredit_but_missing_NumberedOrderRows() { 				
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
	    	.addCreditOrderRow( customAmountRow )
    	;
		
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){							
	        assertEquals(
        		"MISSING VALUE - numberedOrderRows is required for creditCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n",
    			e.getCause().getMessage()
    		);			
        }
    }

	// .creditDirectBankOrderRow validation
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditDirectBankOrderRows_with_setRowToCredit() {
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	.addNumberedOrderRows( rows )
		    //.addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditDirectBankOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditDirectBankOrderRows_with_addRowToCredit() {
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
		    .addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditDirectBankOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditDirectBankOrderRows_with_setRowToCredit() { 	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	//.setOrderId( 123456L )
	    	//.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
    	;
		try {
			CreditTransactionRequest request = builder.creditDirectBankOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){							
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditDirectBankOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n",
        		e.getCause().getMessage()
    		);			
        }
    }	
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditDirectBankOrderRows_with_setRowToCredit_but_missing_NumberedOrderRows() { 				
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( 123456L )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
	    	.addCreditOrderRow( customAmountRow )
    	;
		
		try {
			CreditTransactionRequest request = builder.creditDirectBankOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){							
	        assertEquals(
        		"MISSING VALUE - numberedOrderRows is required for creditDirectBankOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n",
    			e.getCause().getMessage()
    		);			
        }
    }

    /// characterizing unit tests for INTG-551
	@Test
    public void test_creditOrderRows_handles_creditOrderRows_specified_using_exvat_and_vatpercent() {
    	
    	CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    			.setInvoiceId( 123456789L )
    			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
    			.setCountryCode( COUNTRYCODE.SE )
    			.addCreditOrderRow(
    					WebPayItem.orderRow()
    						.setAmountExVat(10.00)
    						.setVatPercent(25)
    						.setQuantity(1D)
				)		
		;
    	CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
    	try {
			SOAPMessage soapRequest = request.prepareRequest();
			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			try {
				soapRequest.writeTo(outstream);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String outstring = outstream.toString();			
			
			// 10.0 @ 25% w/PriceIncludingVat: false
			String exprectedcreditrow = "(?:.*)(<dat1:OrderRow><dat1:ArticleNumber/><dat1:Description/><dat1:DiscountPercent>0.0</dat1:DiscountPercent><dat1:NumberOfUnits>1.0</dat1:NumberOfUnits><dat1:PriceIncludingVat>false</dat1:PriceIncludingVat><dat1:PricePerUnit>10.0</dat1:PricePerUnit><dat1:Unit>null</dat1:Unit><dat1:VatPercent>25.0</dat1:VatPercent></dat1:OrderRow>)(?:.*)";
			assertTrue( outstring.matches(exprectedcreditrow) );        	
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}    	
    }
	
	@Test
    public void test_creditOrderRows_handles_creditOrderRows_specified_using_incvat_and_vatpercent() {
    	
    	CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    			.setInvoiceId( 123456789L )
    			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
    			.setCountryCode( COUNTRYCODE.SE )
    			.addCreditOrderRow(
    					WebPayItem.orderRow()
    						.setAmountIncVat(12.50)
    						.setVatPercent(25)
    						.setQuantity(1D)
				)		
		;
    	CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
    	try {
			SOAPMessage soapRequest = request.prepareRequest();
			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			try {
				soapRequest.writeTo(outstream);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String outstring = outstream.toString();			
			
			// 12.5 @ 25% w/PriceIncludingVat: true
			String exprectedcreditrow = "(?:.*)(<dat1:OrderRow><dat1:ArticleNumber/><dat1:Description/><dat1:DiscountPercent>0.0</dat1:DiscountPercent><dat1:NumberOfUnits>1.0</dat1:NumberOfUnits><dat1:PriceIncludingVat>true</dat1:PriceIncludingVat><dat1:PricePerUnit>12.5</dat1:PricePerUnit><dat1:Unit>null</dat1:Unit><dat1:VatPercent>25.0</dat1:VatPercent></dat1:OrderRow>)(?:.*)";
			assertTrue( outstring.matches(exprectedcreditrow) );        	
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}    	
    }	  
    
	@Test
    public void test_creditOrderRows_handles_creditOrderRows_specified_using_incvat_and_exvat() {       
    	
    	CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    			.setInvoiceId( 123456789L )
    			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
    			.setCountryCode( COUNTRYCODE.SE )
    			.addCreditOrderRow(
    					WebPayItem.orderRow()
    						.setAmountIncVat(12.50)
    						.setAmountExVat(10.00)
    						.setQuantity(1D)
				)		
		;
    	CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
    	try {
			SOAPMessage soapRequest = request.prepareRequest();
			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			try {
				soapRequest.writeTo(outstream);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String outstring = outstream.toString();			
			
			// 12.5 @ 25% w/PriceIncludingVat: true
			String exprectedcreditrow = "(?:.*)(<dat1:OrderRow><dat1:ArticleNumber/><dat1:Description/><dat1:DiscountPercent>0.0</dat1:DiscountPercent><dat1:NumberOfUnits>1.0</dat1:NumberOfUnits><dat1:PriceIncludingVat>true</dat1:PriceIncludingVat><dat1:PricePerUnit>12.5</dat1:PricePerUnit><dat1:Unit>null</dat1:Unit><dat1:VatPercent>25.0</dat1:VatPercent></dat1:OrderRow>)(?:.*)";
			assertTrue( outstring.matches(exprectedcreditrow) );        	
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}    	
    }	
}
