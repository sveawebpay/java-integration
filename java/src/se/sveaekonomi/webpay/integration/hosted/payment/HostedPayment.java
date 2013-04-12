package se.sveaekonomi.webpay.integration.hosted.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.hosted.helper.HostedRowFormatter;
import se.sveaekonomi.webpay.integration.hosted.helper.HostedXmlBuilder;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HostedOrderValidator;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;


/*******************************************************************************
 * Description of HostedPayment: Parent to CardPayment, DirectPayment, PayPagePayment 
 * and PaymentMethodPayment classes. Prepares an order and creates a payment form
 * to integrate on web page. Uses XmlBuilder to turn formatted order into xml format.
 * 
 * @author klar-sar
 * *****************************************************************************/
public abstract class HostedPayment {
    
    protected CreateOrderBuilder createOrderBuilder;
    protected ArrayList<HostedOrderRowBuilder> rowBuilder;
    protected List<String>  excludedPaymentMethods;
    private Long amount;
    private Long vat;
    protected String returnUrl;
    protected String cancelUrl;
    protected ExcludePayments excluded;
    protected String languageCode = LANGUAGECODE.en.toString();

    public HostedPayment(CreateOrderBuilder createOrderBuilder) {
        this.createOrderBuilder = createOrderBuilder;
        rowBuilder = new ArrayList<HostedOrderRowBuilder>();
        excluded = new ExcludePayments();
        excludedPaymentMethods = new ArrayList<String>();
        returnUrl = "";
    }
    
    public CreateOrderBuilder getCreateOrderBuilder() {
        return createOrderBuilder;
    }
    
    public ArrayList<HostedOrderRowBuilder> getRowBuilder() {
        return rowBuilder;
    }
    
    public List<String> getExcludedPaymentMethods() {
        return excludedPaymentMethods;
    }
    
    public Long getAmount() {
        return amount;
    }

    public Long getVat() {
        return vat;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    /**
     * Required
     * @param returnUrl
     * @return HostedPayment
     */
    public HostedPayment setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }
    
    public String getCancelUrl() {
        return cancelUrl;
    }
    
    public HostedPayment setCancelUrl(String returnUrl) {
        this.cancelUrl = returnUrl;
        return this;
    }

    public HostedPayment setPayPageLanguageCode(LANGUAGECODE languageCode) {
    	this.languageCode = languageCode.toString();
    	return this;
    }
    
    public String getPayPageLanguageCode() {
        return languageCode;
    }
    
    public String validateOrder() {
       
    /*    String errors = "";	
       if(this.returnUrl=="")
    	   errors += "MISSING VALUE - Return url is required, setReturnUrl(...).\n";*/
        HostedOrderValidator validator = new HostedOrderValidator();
        String errors = validator.validate(this.createOrderBuilder);
        return errors;
       
    }
    
    public void calculateRequestValues() throws ValidationException {
    	String errors = "";
        errors = validateOrder();
        if(!errors.equals(""))
            throw new ValidationException(errors);
    	
        HostedRowFormatter formatter = new HostedRowFormatter();
        
        rowBuilder = formatter.formatRows(createOrderBuilder);
        amount = formatter.formatTotalAmount(rowBuilder);
        vat = formatter.formatTotalVat(rowBuilder);
        configureExcludedPaymentMethods();
    }
    
    public PaymentForm getPaymentForm() throws Exception {
        calculateRequestValues();
        HostedXmlBuilder xmlBuilder = new HostedXmlBuilder();
        String xml = "";
        
        try {
            xml = xmlBuilder.getXml(this);
        } catch (Exception e) {
            throw e;
        }
        
        PaymentForm form = new PaymentForm();        
        form.setXmlMessage(xml);          

        form.setMerchantId(createOrderBuilder.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));
        form.setSecretWord(createOrderBuilder.getConfig().getSecret(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));
        if(this.createOrderBuilder.getCountryCode() != null)
            form.setSubmitMessage(this.createOrderBuilder.getCountryCode());
        else 
            form.setSubmitMessage(COUNTRYCODE.SE);

        form.setPayPageUrl(createOrderBuilder.getConfig().getEndPoint(PAYMENTTYPE.HOSTED));
        
        form.setForm();
        form.setHtmlFields();
        
        return form;
    }
    
    protected abstract HostedPayment configureExcludedPaymentMethods();
    
    public abstract XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) throws Exception;    
    
    protected void writeSimpleElement(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        if (value != null) {
            xmlw.writeStartElement(name);
            xmlw.writeCharacters(value);
            xmlw.writeEndElement();
        }
    }
}
