package se.sveaekonomi.webpay.integration.webservice.payment;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.ValidationException;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.WebServiceOrderValidator;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebserviceRowFormatter;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrderInformation;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCustomerIdentity;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaIdentity;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public abstract class WebServicePayment {

    protected CreateOrderBuilder createOrderBuilder;
    protected PAYMENTTYPE orderType;
    public SveaCreateOrderInformation orderInformation;
    
    public WebServicePayment(CreateOrderBuilder orderBuilder) {
        this.createOrderBuilder = orderBuilder;
        orderInformation = new SveaCreateOrderInformation();
    }
    
    protected abstract SveaCreateOrderInformation setOrderType();
    
    private SveaAuth getPasswordBasedAuthorization() {
        SveaAuth auth = new SveaAuth();
        auth.Username = this.createOrderBuilder.getConfig().getUsername(this.orderType, this.createOrderBuilder.getCountryCode());
        auth.Password = this.createOrderBuilder.getConfig().getPassword(this.orderType, this.createOrderBuilder.getCountryCode());
        auth.ClientNumber = this.createOrderBuilder.getConfig().getClientNumber(this.orderType, this.createOrderBuilder.getCountryCode());
        return auth;
    }
    
    public String getXML() {
        SveaRequest<SveaCreateOrder> request = this.prepareRequest();
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        
        String xml = xmlBuilder.getCreateOrderEuXml((SveaCreateOrder) request.request);
        
        return xml;
    }
    
    public String validateOrder() {
        try {
            WebServiceOrderValidator validator = new WebServiceOrderValidator();
            return validator.validate(this.createOrderBuilder);
        } catch (NullPointerException e) {
            return "NullPointer in validation WebServiceOrderValidator";
        }
    }
    
    /**
     * Rebuild order with soap package to be in right format for SveaWebPay Europe Web service API
     * 
     * @return SveaRequest
     */
    public SveaRequest<SveaCreateOrder> prepareRequest() {
        String errors = "";
        errors = validateOrder();
        
        if (errors.length() > 0) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors)) ;
        }
        
        SveaCreateOrder sveaOrder = new SveaCreateOrder();
        sveaOrder.Auth = this.getPasswordBasedAuthorization();
        
        // make order rows and put in CreateOrderInformation
        orderInformation = this.formatOrderInformationWithOrderRows();
        orderInformation.CustomerIdentity = this.formatCustomerIdentity();
        orderInformation.ClientOrderNumber = this.createOrderBuilder.getClientOrderNumber();
        orderInformation.OrderDate = this.createOrderBuilder.getOrderDate();
        orderInformation.CustomerReference = this.createOrderBuilder.getCustomerReference();
        sveaOrder.CreateOrderInformation = this.setOrderType();
        
        SveaRequest<SveaCreateOrder> object = new SveaRequest<SveaCreateOrder>();
        object.request = sveaOrder;
        
        return object;
    }
    
    public CreateOrderResponse doRequest() {
        SveaRequest<SveaCreateOrder> request = this.prepareRequest();
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml = "";
        
        xml = xmlBuilder.getCreateOrderEuXml((SveaCreateOrder) request.request);
        
        URL url = this.createOrderBuilder.getConfig().getEndPoint(this.orderType);
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
        NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url.toString());
        CreateOrderResponse response = new CreateOrderResponse(soapResponse);
        
        return response;
    }
    
    public SveaCustomerIdentity formatCustomerIdentity() {
        boolean isCompany = false;
        String companyId = "";
        
        if (this.createOrderBuilder.getIsCompanyIdentity()) {
            isCompany = true;
            companyId = (this.createOrderBuilder.getCompanyCustomer().getNationalIdNumber()!=null) 
                    ? this.createOrderBuilder.getCompanyCustomer().getNationalIdNumber()
                    : this.createOrderBuilder.getCompanyCustomer().getVatNumber();
        }
        
        // For European countries Individual/Company - identity required
        SveaIdentity euIdentity = null;
        String type = "";
        
        if (this.createOrderBuilder.getCountryCode() != COUNTRYCODE.SE
                && this.createOrderBuilder.getCountryCode() != COUNTRYCODE.NO
                && this.createOrderBuilder.getCountryCode() != COUNTRYCODE.FI
                && this.createOrderBuilder.getCountryCode() != COUNTRYCODE.DK) {
            euIdentity = new SveaIdentity(isCompany);
            
            if (isCompany) {
                euIdentity.CompanyVatNumber = companyId;
            } else {
                euIdentity.FirstName = createOrderBuilder.getIndividualCustomer().getFirstName();
                euIdentity.LastName = createOrderBuilder.getIndividualCustomer().getLastName();
                if (this.createOrderBuilder.getCountryCode() == COUNTRYCODE.NL)
                    euIdentity.Initials = createOrderBuilder.getIndividualCustomer().getInitials();
                euIdentity.BirthDate = createOrderBuilder.getIndividualCustomer().getBirthDate();
            }
            
            type = isCompany ? "CompanyIdentity" : "IndividualIdentity";
        }
        
        SveaCustomerIdentity customerIdentity = new SveaCustomerIdentity(euIdentity, type);
        // For the Nordic countries NationalIdNumber is required
        
        if (this.createOrderBuilder.getCountryCode() == COUNTRYCODE.SE
                || this.createOrderBuilder.getCountryCode() == COUNTRYCODE.NO
                || this.createOrderBuilder.getCountryCode() == COUNTRYCODE.FI
                || this.createOrderBuilder.getCountryCode() == COUNTRYCODE.DK) {
            // set companyVat
            customerIdentity.NationalIdNumber = (String) (isCompany ? String.valueOf(this.createOrderBuilder.getCompanyCustomer().getNationalIdNumber())
                    : String.valueOf(createOrderBuilder.getIndividualCustomer().getNationalIdNumber()));
        }
        
        if (isCompany) {
            customerIdentity.FullName = this.createOrderBuilder.getCompanyCustomer().getCompanyName()!=null ? this.createOrderBuilder.getCompanyCustomer().getCompanyName() : "";
        } else {
            customerIdentity.FullName = this.createOrderBuilder.getIndividualCustomer().getFirstName()!=null && this.createOrderBuilder.getIndividualCustomer().getLastName()!=null 
                    ? this.createOrderBuilder.getIndividualCustomer().getFirstName() + " " + this.createOrderBuilder.getIndividualCustomer().getLastName() : "";
        }
        
        customerIdentity.PhoneNumber = createOrderBuilder.getCustomerIdentity().getPhoneNumber() != null 
                ? String.valueOf(createOrderBuilder.getCustomerIdentity().getPhoneNumber()) : "";
                
        customerIdentity.Street = createOrderBuilder.getCustomerIdentity().getStreetAddress() != null 
                ? createOrderBuilder.getCustomerIdentity().getStreetAddress() : "";
                
        customerIdentity.HouseNumber = createOrderBuilder.getCustomerIdentity().getHouseNumber() != null 
                ? String.valueOf(createOrderBuilder.getCustomerIdentity().getHouseNumber()) : "";
                
        customerIdentity.CoAddress = createOrderBuilder.getCustomerIdentity().getCoAddress() != null 
                ? createOrderBuilder.getCustomerIdentity().getCoAddress() : "";
                
        customerIdentity.ZipCode = createOrderBuilder.getCustomerIdentity().getZipCode() != null 
                ? String.valueOf(createOrderBuilder.getCustomerIdentity().getZipCode()) : "";
                
        customerIdentity.Locality = createOrderBuilder.getCustomerIdentity().getLocality() != null 
                ? createOrderBuilder.getCustomerIdentity().getLocality() : "";
                
        customerIdentity.Email = createOrderBuilder.getCustomerIdentity().getEmail() != null
                ? createOrderBuilder.getCustomerIdentity().getEmail() : "";
                
        customerIdentity.IpAddress = createOrderBuilder.getCustomerIdentity().getIpAddress() != null 
                ? createOrderBuilder.getCustomerIdentity().getIpAddress() : "";
        
        customerIdentity.CustomerType = (isCompany ? "Company" : "Individual");
        
        customerIdentity.CountryCode = this.createOrderBuilder.getCountryCode();
        
        return customerIdentity;
    }
    
    public SveaCreateOrderInformation formatOrderInformationWithOrderRows() {
        String campaignCode = (this.createOrderBuilder.getCampaignCode() != null) ? this.createOrderBuilder.getCampaignCode() : "";
        boolean sendAutomaticGiroPaymentForm = (this.createOrderBuilder.getSendAutomaticGiroPaymentForm() != null) ? this.createOrderBuilder.getSendAutomaticGiroPaymentForm() : false;
        orderInformation = new SveaCreateOrderInformation(campaignCode, sendAutomaticGiroPaymentForm);
        
        WebserviceRowFormatter formatter = new WebserviceRowFormatter(this.createOrderBuilder);
        ArrayList<SveaOrderRow> formattedOrderRows = formatter.formatRows();
        
        for (SveaOrderRow formattedOrderRow : formattedOrderRows) {
            orderInformation.addOrderRow(formattedOrderRow);
        }
        
        return orderInformation;
    }
}
