package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaGetPaymentPlanParams;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class GetPaymentPlanParams {
    
    private boolean testmode;
    private final SveaConfig conf = new SveaConfig();
    
    public boolean getTestmode() {
        return testmode;
    }
    
    public GetPaymentPlanParams setTestmode() {
        this.testmode = true;
        return this;
    }
    
    public GetPaymentPlanParams setPasswordBasedAuthorization(String userName, String password, int clientNumber) {
        conf.setPasswordBasedAuthorization(userName, password, clientNumber, "PaymentPlan"); 
        return this;
    }
    
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
        String url = testmode ? SveaConfig.SWP_TEST_WS_URL : SveaConfig.SWP_PROD_WS_URL;
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("GetPaymentPlanParamsEu", xml);
        NodeList soapResponse = soapBuilder.createGetPaymentPlanParamsEuRequest(soapMessage, url);
        PaymentPlanParamsResponse response = new PaymentPlanParamsResponse(soapResponse);
        return response;
    }
}
