package se.sveaekonomi.webpay.integration.util.constant;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum INVOICETYPE {
	INVOICESE("SVEAINVOICESE", COUNTRYCODE.SE),	
	INVOICE_SE("SVEAINVOICEEU_SE", COUNTRYCODE.SE),
	INVOICE_NO("SVEAINVOICEEU_NO", COUNTRYCODE.NO),
	INVOICE_DK("SVEAINVOICEEU_DK", COUNTRYCODE.DK),
	INVOICE_FI("SVEAINVOICEEU_FI", COUNTRYCODE.FI),
	INVOICE_NL("SVEAINVOICEEU_NL", COUNTRYCODE.NL),
	INVOICE_DE("SVEAINVOICEEU_DE", COUNTRYCODE.DE);
	
	public static final EnumSet<INVOICETYPE> ALL_INVOICETYPES = EnumSet.allOf(INVOICETYPE.class);
	private String value;
	private COUNTRYCODE countryCode;
	
	INVOICETYPE(String value, COUNTRYCODE countryCode) {
		this.value = value;
		this.countryCode = countryCode;
	}
	
	public String getValue() {
		return value;
	}
	
	public List<String> getAllInvoiceValues() {
		List<String> allValues = new ArrayList<String>();
	
		for (INVOICETYPE it : INVOICETYPE.ALL_INVOICETYPES) {
			allValues.add(it.getValue());
		}
		return allValues;
	}
	
	public COUNTRYCODE getCountryCode() {
		return this.countryCode;
	}
	
	public EnumSet<INVOICETYPE> getAllInvoiceTypes() {
		return INVOICETYPE.ALL_INVOICETYPES;
	}
}
