package se.sveaekonomi.webpay.integration.hosted.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;

public class PayPagePayment extends HostedPayment {

	protected String paymentMethod;
	protected List<String> includedPaymentMethods;

	public PayPagePayment(CreateOrderBuilder orderBuilder) {
		super(orderBuilder);
		includedPaymentMethods = new ArrayList<String>();
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public PayPagePayment setPaymentMethod(PAYMENTMETHOD paymentMethod) {
		if(paymentMethod.equals(PAYMENTMETHOD.INVOICE))
			this.paymentMethod = getInvoiceType(true);
		else if(paymentMethod.equals(PAYMENTMETHOD.PAYMENTPLAN))
			this.paymentMethod = getPaymentPlanType(true);
		else
			this.paymentMethod = paymentMethod.getValue();
		return this;
	}

	public List<String> getIncludedPaymentMethods() {
		return includedPaymentMethods;
	}

	/*public HostedPayment setIncludedPaymentMethods(
			List<PAYMENTMETHOD> paymentMethods) {
		this.includedPaymentMethods.addAll(paymentMethods);
		includePaymentMethods();
		return this;
	}*/

	/**
	 * Only used in CardPayment and DirectPayment
	 */
	protected HostedPayment configureExcludedPaymentMethods() {
		return this;
	}

	public PayPagePayment excludeCardPaymentMethods() {
		excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());	
		
		return this;
	}

	public PayPagePayment excludeDirectPaymentMethods() {
		excludedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SEB_SE.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SHB_SE.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE.getValue());
		return this;
	}

	public PayPagePayment excludePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods) {
		addCollectionToExcludePaymentMethodsList(paymentMethods);
		return this;
	}
	
	private void addCollectionToExcludePaymentMethodsList(
		Collection<PAYMENTMETHOD> paymentMethods) {
		for (PAYMENTMETHOD pm : paymentMethods) {
			
			if (pm.equals(PAYMENTMETHOD.INVOICE)) {
				excludedPaymentMethods.addAll(INVOICETYPE.INVOICE_SE.getAllInvoiceValues());
			}
			else if(pm.equals(PAYMENTMETHOD.PAYMENTPLAN)) {
				excludedPaymentMethods.addAll(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getAllPaymentPlanValues());								  				
			}
			else
				excludedPaymentMethods.add(pm.getValue());
		}
	}

	public PayPagePayment excludePaymentMethods() {
		List<PAYMENTMETHOD> emptyList = new ArrayList<PAYMENTMETHOD>(); 
		return excludePaymentMethods(emptyList);
	}
	
	public PayPagePayment includePaymentMethods() {
		List<PAYMENTMETHOD> emptyList = new ArrayList<PAYMENTMETHOD>(); 
		return includePaymentMethods(emptyList);
	}
	
	public PayPagePayment includePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods) {
		addCollectionToIncludedPaymentMethodsList(paymentMethods);
	
		// Exclude all payment methods		
		ExcludePayments excluded = new ExcludePayments();
		excludedPaymentMethods = excluded.excludeInvoicesAndPaymentPlan();

		excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());
		excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL.getValue());
		excludeDirectPaymentMethods();

		// Remove the included methods from the excluded payment methods
		for (String pm : includedPaymentMethods) {
			excludedPaymentMethods.remove(pm);
		}

		return this;
	}
	
	private String getPaymentPlanType(Boolean isInclude) {
		for (PAYMENTPLANTYPE ppt : PAYMENTPLANTYPE.ALL_PAYMENTPLANTYPES){
			//never include old flow to include list - won´t show in paypage
			if(isInclude &&
					createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE) && 
					ppt.equals(PAYMENTPLANTYPE.PAYMENTPLANSE))
				continue;
			//always include from old flow to exclude list - won´t show in paypage
			else if(!isInclude &&
					createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE) && 
					ppt.equals(PAYMENTPLANTYPE.PAYMENTPLANSE))
				excludedPaymentMethods.add(ppt.getValue());	
			//include only Payment plan for current country 
			else if(ppt.getCountryCode().equals(createOrderBuilder.getCountryCode()))	
				return ppt.getValue();				  	
		}
		return "";
	}
	
	private String getInvoiceType(Boolean isInclude) {
		for (INVOICETYPE it : INVOICETYPE.ALL_INVOICETYPES) {
			//never include old flow to include list
			if(isInclude &&
					createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE) && 
					it.equals(INVOICETYPE.INVOICESE))
				continue;
			//always include old flow to exclude list
			else if(!isInclude &&
					createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE) && 
					it.equals(INVOICETYPE.INVOICESE))
				excludedPaymentMethods.add(it.getValue());
			else if(it.getCountryCode().equals(createOrderBuilder.getCountryCode()))
				return it.getValue();
		}
		return "";
	}
	
	private void addCollectionToIncludedPaymentMethodsList(Collection<PAYMENTMETHOD> paymentMethods) {
		
		for (PAYMENTMETHOD pm : paymentMethods) {
			
			if (pm.equals(PAYMENTMETHOD.INVOICE)) {  
				includedPaymentMethods.add(getInvoiceType(true));			
			}
			else if (pm.equals(PAYMENTMETHOD.PAYMENTPLAN)) {  
				includedPaymentMethods.add(getPaymentPlanType(true));				
			}
			else
				includedPaymentMethods.add(pm.getValue());
		}		
	}

	public PayPagePayment setPayPageLanguage(LANGUAGECODE languageCode) {
		this.languageCode = languageCode.toString();
		
		return this;
	}

	public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw)
			throws Exception {
		if (paymentMethod!=null) {
			writeSimpleElement(xmlw, "paymentmethod", paymentMethod);
		}

		return xmlw;
	}
}
