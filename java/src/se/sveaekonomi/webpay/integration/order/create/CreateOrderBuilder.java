package se.sveaekonomi.webpay.integration.order.create;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.payment.CardPayment;
import se.sveaekonomi.webpay.integration.hosted.payment.DirectPayment;
import se.sveaekonomi.webpay.integration.hosted.payment.PayPagePayment;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.validator.OrderValidator;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;
import se.sveaekonomi.webpay.integration.webservice.payment.PaymentPlanPayment;

public class CreateOrderBuilder extends OrderBuilder<CreateOrderBuilder> {
        
    private OrderValidator validator;
   
    public final SveaConfig config = new SveaConfig();

    private String clientOrderNumber;
    private String customerReference;
    private String orderDate;
    
    private COUNTRYCODE countryCode;
    private String currency;
    private String addressSelector;
    private String campaignCode;
    private Boolean sendAutomaticGiroPaymentForm;
     
    public CustomerIdentity<?> customerIdentity;    
    
    public CreateOrderBuilder() {
    }     
    
    public OrderValidator getValidator() {
        return validator;
    }
    
    public CreateOrderBuilder setValidator(OrderValidator validator) {
        this.validator = validator;
        return this;
    }    
    
    public String getClientOrderNumber() {
        return clientOrderNumber;
    }
    
    public CreateOrderBuilder setClientOrderNumber(String clientOrderNumber) {
        this.clientOrderNumber = clientOrderNumber;
        return this;
    }
    
    public String getCustomerReference() {
        return customerReference;
    }
    
    public CreateOrderBuilder setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
        return this;
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public CreateOrderBuilder setOrderDate(String orderDate) {
        this.orderDate = orderDate;
        return this;
    }
    
    public COUNTRYCODE getCountryCode() {
        return countryCode;
    }
    
    public CreateOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    public String getCurrency() {
    	if(currency == null)
    		return null;
    	else 
    		return currency.toString();
    }
    
    public CreateOrderBuilder setCurrency(CURRENCY currency) {
        this.currency = currency.toString();
        return this;
    }
    
    public String getAddressSelector() {
        return addressSelector;
    }
    
    public CreateOrderBuilder setAddressSelector(String addressSelector) {
        this.addressSelector = addressSelector;
        return this;
    }
    
    public String getCampaignCode() {
        return campaignCode;
    }
    
    public CreateOrderBuilder setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
        return this;
    }
    
    public Boolean getSendAutomaticGiroPaymentForm() {
        return sendAutomaticGiroPaymentForm;
    }
    
    public CreateOrderBuilder setSendAutomaticGiroPaymentForm(Boolean sendAutomaticGiroPaymentForm) {
        this.sendAutomaticGiroPaymentForm = sendAutomaticGiroPaymentForm;
        return this;
    }    
    
    /**
     * Start creating card payment via Pay Page. Returns Payment form to integrate in shop.
     * @return HostedPayment
     */
    public CardPayment usePayPageCardOnly() throws ValidationException {
        return new CardPayment(this);
    }
    
    /**
     * Start creating direct bank payment via Pay Page. Returns Payment form to integrate in shop.
     * @return HostedPayment
     */
    public DirectPayment usePayPageDirectBankOnly() throws ValidationException {
        return new DirectPayment(this);
    }
    
    /**
     * Start creating payment through Pay Page. You will be able to customize the Pay Page.
     * Returns Payment form to integrate in shop.
     * @return PayPagePayment
     */
    
    public PayPagePayment usePayPage() throws ValidationException {
        return new PayPagePayment(this);
    }
    
    /**
     * Start creating payment with a specific payment method. This function will directly direct to the specified payment method.
     * Payment methods are found in appendix in our documentation.
     * Returns Payment form to integrate in shop.
     * @param type paymentMethod
     * @return PaymentMethodPayment
     */
    public PaymentMethodPayment usePaymentMethod(PAYMENTMETHOD paymentMethod) {
        return new PaymentMethodPayment(this, paymentMethod);
    }
    
    public InvoicePayment useInvoicePayment() throws ValidationException {
        return new InvoicePayment((CreateOrderBuilder)this);
    }
    
    public PaymentPlanPayment usePaymentPlanPayment(String campaignCode) throws ValidationException {
        return this.usePaymentPlanPayment(campaignCode, false);        
    }
    
    public PaymentPlanPayment usePaymentPlanPayment(String campaignCode, Boolean sendAutomaticGiroPaymentForm) {
        this.campaignCode = campaignCode;
        this.sendAutomaticGiroPaymentForm = sendAutomaticGiroPaymentForm;
        return new PaymentPlanPayment(this);
    }
    
    public CreateOrderBuilder addCustomerDetails(CustomerIdentity<?> customerIdentity){
        this.customerIdentity = customerIdentity;
        return this;
    }
    
    public boolean getIsCompanyIdentity() {
        if(customerIdentity instanceof CompanyCustomer)
            return true;
        else
            return false;
    }
    
    public CompanyCustomer getCompanyCustomer() {
        return (CompanyCustomer)this.customerIdentity;
    }
    
    public IndividualCustomer getIndividualCustomer() {
        return (IndividualCustomer)this.customerIdentity;
    }
    
    public CustomerIdentity<?> getCustomerIdentity() {
        return this.customerIdentity;
    }
    
    public CreateOrderBuilder build() throws ValidationException {
        validator.validate(this);
        return this;
    }
    
    public <T> T run(CreateBuilderCommand<T> runner) {
        return runner.run(this);
    } 
}
