package se.sveaekonomi.webpay.integration.webservice.getaddresses;

import java.net.URL;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.Config;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaGetAddresses;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class GetAddresses {
    
    private String ssn;
    private String companyId;
    private String countryCode;
    private String orderType;   
    private final SveaConfig conf = new SveaConfig();
    private Config configMode;
    
    public GetAddresses(Config config) {
    	this.configMode = config;
    }
    
    public String getIndividual() {
        return ssn;
    }
    
    /**
     * Required if customer is Individual
     * @param type ssn
     * Sweden: Personnummer,
     * Norway: Personalnumber,
     * Denmark: CPR
     * @return GetAddresses
     */
    public GetAddresses setIndividual(String ssn) {
        this.ssn = ssn;
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
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public GetAddresses setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    public GetAddresses setOrderTypePaymentPlan() {
        this.orderType = "PaymentPlan";
        return this;        
    }
    
    public GetAddresses setOrderTypeInvoice() {
        this.orderType = "Invoice";
        return this;        
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    
    /**
     * Note! This function may change in future updates.
     * @param userName
     * @param password
     * @param clientNumber
     * @return
     */
    public GetAddresses setPasswordBasedAuthorization(String userName, String password, int clientNumber) {
        conf.setPasswordBasedAuthorization(userName, password, clientNumber, orderType);    
        return this;
    }
    
    private SveaAuth getStoreAuthorization() {
        return conf.getAuthorizationForWebServicePayments("invoice");
    }
    
    private SveaRequest<SveaGetAddresses> prepareRequest() {
        SveaGetAddresses sveaAddress = new SveaGetAddresses();
        sveaAddress.Auth = getStoreAuthorization();
        sveaAddress.IsCompany = (companyId != null ? true : false);
        sveaAddress.CountryCode = countryCode;
        sveaAddress.SecurityNumber = ssn;

        SveaRequest<SveaGetAddresses> request = new SveaRequest<SveaGetAddresses>();
        request.request = sveaAddress;

        return request;
    }
    
    public GetAddressesResponse doRequest() throws Exception {
        SveaRequest<SveaGetAddresses> request = prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml;
        
        try {
            xml = xmlBuilder.getGetAddressesXml(request.request);
        } catch (Exception e) {
            throw e;
        }
        
        URL url = configMode.getWebserviceUrl();
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("GetAddresses", xml);
        NodeList soapResponse = soapBuilder.createGetAddressesEuRequest(soapMessage, url.toString());
        GetAddressesResponse response = new GetAddressesResponse(soapResponse);
        return response;
    }
   
}
