package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import java.net.URL;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaGetPaymentPlanParams;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class GetPaymentPlanParams {
       
    private final SveaConfig conf = new SveaConfig();
    private ConfigurationProvider config;
    
    public GetPaymentPlanParams(ConfigurationProvider config) {
    	this.config = config;
    }
    
    /*public URL getPayPageUrl() {
    	return this.configMode.getPayPageUrl();
    }
    
    public URL getWebserviceUrl() {
    	return this.configMode.getWebserviceUrl();
    }*/
    
    /**
     * Note! This function may change in future updates.
     * @param userName
     * @param password
     * @param clientNumber
     * @return
     */
 /*   public GetPaymentPlanParams setPasswordBasedAuthorization(String userName, String password, int clientNumber) {
        conf.setPasswordBasedAuthorization(userName, password, clientNumber, "PaymentPlan"); 
        return this;
    }*/
    
    protected SveaAuth getStoreAuthorization() {
        return conf.getAuthorizationForWebServicePayments("PaymentPlan");
    }
    
    private SveaRequest<SveaGetPaymentPlanParams> prepareRequest() {
        SveaGetPaymentPlanParams params = new SveaGetPaymentPlanParams();
        params.Auth = getStoreAuthorization();
        SveaRequest<SveaGetPaymentPlanParams> request = new SveaRequest<SveaGetPaymentPlanParams>();
        request.request = params;

        return request;
    }
    
    public PaymentPlanParamsResponse doRequest() throws Exception {
        SveaRequest<SveaGetPaymentPlanParams> request = prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml = xmlBuilder.getGetPaymentPlanParamsXml(request.request);
        URL url = this.config.getEndPoint(PAYMENTTYPE.PAYMENTPLAN);
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("GetPaymentPlanParamsEu", xml);
        NodeList soapResponse = soapBuilder.createGetPaymentPlanParamsEuRequest(soapMessage, url.toString());
        PaymentPlanParamsResponse response = new PaymentPlanParamsResponse(soapResponse);
        return response;
    }
}
