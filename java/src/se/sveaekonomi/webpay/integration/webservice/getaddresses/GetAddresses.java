package se.sveaekonomi.webpay.integration.webservice.getaddresses;

import java.net.URL;

import javax.xml.bind.ValidationException;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaGetAddresses;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

/**
 * Applicable for SE, NO and DK.
 * If customer has multiple addresses or just to show the address which
 * the invoice/product is to be delivered. It returns an GetAddressesResponse object containing all associated addresses for a specific 
 * SecurityNumber. 
 * Each address gets an "AddressSelector" - has to signify the address. This can
 * be used when creating order to have the invoice be sent to the specified address. 
 */
public class GetAddresses {
    
    private String nationalNumber;
    private String companyId;
    private COUNTRYCODE countryCode;
    private String orderType;
    private ConfigurationProvider config;
    
    public GetAddresses(ConfigurationProvider config) {
        this.config = config;
    }
    
    public String getIndividual() {
        return nationalNumber;
    }
    
    /**
     * Required if customer is Individual
     * @param type nationalNumber
     * Sweden: Personnummer,
     * Norway: Personalnumber,
     * Denmark: CPR
     * @return GetAddresses
     */
    public GetAddresses setIndividual(String nationalNumber) {
        this.nationalNumber = nationalNumber;
        return this;
    }
    
    public String getCompanyId() {
        return companyId;
    }
    
    /**
     * Required if customer is Company
     * @param companyId
     * Sweden: Organisationsnummer,
     * Norway: Vat number,
     * Denmark: CVR
     * @returnGetAddresses
     */
    public GetAddresses setCompany(String companyId) {
        this.companyId = companyId;
        return this;
    }
    
    /**
     * Required
     * @return countryCode
     */
    public COUNTRYCODE getCountryCode() {
        return countryCode;
    }
    
    public GetAddresses setCountryCode(COUNTRYCODE countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    /**
     * Required for PaymentPlan type
     * @return GetAddresses
     */
    public GetAddresses setOrderTypePaymentPlan() {
        this.orderType = "PaymentPlan";
        return this;
    }
    
    /**
     * Required for Invoice type
     * @return GetAddresses
     */
    public GetAddresses setOrderTypeInvoice() {
        this.orderType = "Invoice";
        return this;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    private SveaAuth getStoreAuthorization() {
         SveaAuth auth = new SveaAuth();
         PAYMENTTYPE type = (orderType.equals("Invoice") ? PAYMENTTYPE.INVOICE : PAYMENTTYPE.PAYMENTPLAN);
         auth.Username = config.getUsername(type, countryCode);
         auth.Password = config.getPassword(type, countryCode);
         auth.ClientNumber = config.getClientNumber(type, countryCode);
         return auth;
    }
    
    public String validateRequest() {
        String errors ="";
        if (countryCode == null)
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        if (orderType==null)
            errors += "MISSING VALUE - orderType is required, use one of: setOrderTypePaymentPlan() or setOrderTypeInvoice().\n";
        if (this.nationalNumber==null && this.companyId==null)
            errors += "MISSING VALUE - either nationalNumber or companyId is required. Use: setCompany(...) or setIndividual(...).\n";
        return errors;
    }
    
    private SveaRequest<SveaGetAddresses> prepareRequest() {
        String errors = "";
        errors = validateRequest();
        
        if (errors.length() > 0) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
        
        SveaGetAddresses sveaAddress = new SveaGetAddresses();
        sveaAddress.Auth = getStoreAuthorization();
        sveaAddress.IsCompany = (companyId != null ? true : false);
        sveaAddress.CountryCode = countryCode.toString();
        sveaAddress.SecurityNumber = companyId != null ? companyId : nationalNumber;
        
        SveaRequest<SveaGetAddresses> request = new SveaRequest<SveaGetAddresses>();
        request.request = sveaAddress;
        
        return request;
    }
    
    public GetAddressesResponse doRequest() {
        SveaRequest<SveaGetAddresses> request = prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml = xmlBuilder.getGetAddressesXml(request.request);
        
        URL url = config.getEndPoint(orderType.equals("Invoice") ? PAYMENTTYPE.INVOICE : PAYMENTTYPE.PAYMENTPLAN);
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("GetAddresses", xml);
        NodeList soapResponse = soapBuilder.createGetAddressesEuRequest(soapMessage, url.toString());
        GetAddressesResponse response = new GetAddressesResponse(soapResponse);
        return response;
    }
}
