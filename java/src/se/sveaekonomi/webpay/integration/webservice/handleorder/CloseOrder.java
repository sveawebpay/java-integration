package se.sveaekonomi.webpay.integration.webservice.handleorder;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrderInformation;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CloseOrder {
    
    private CloseOrderBuilder order;
    private final SveaConfig conf = new SveaConfig();
    
    public CloseOrder(CloseOrderBuilder order) {
        this.order = order;
    }

    protected SveaAuth getStoreAuthorization() {
        return order.config.getAuthorizationForWebServicePayments(this.order.getOrderType());        
    }
    
    public CloseOrder setPasswordBasedAuthorization(String userName, String password, int clientNumber) {
        conf.setPasswordBasedAuthorization(userName, password, clientNumber, order.getOrderType());    
        return this;
    }
    
    public SveaRequest<SveaCloseOrder> prepareRequest() {
        SveaCloseOrder sveaCloseOrder = new SveaCloseOrder();
        sveaCloseOrder.Auth = getStoreAuthorization();
        SveaCloseOrderInformation orderInfo = new SveaCloseOrderInformation();
        orderInfo.SveaOrderId = order.getOrderId();
        sveaCloseOrder.CloseOrderInformation = orderInfo;

        SveaRequest<SveaCloseOrder> object = new SveaRequest<SveaCloseOrder>();
        object.request = sveaCloseOrder;

        return object;
    }
    
    public CloseOrderResponse doRequest() throws Exception {
        String url = order.getTestmode() ? SveaConfig.SWP_TEST_WS_URL : SveaConfig.SWP_PROD_WS_URL; 
        SveaRequest<SveaCloseOrder> request = this.prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml = xmlBuilder.getCloseOrderEuXml(request.request);
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("CloseOrderEu", xml);
        NodeList soapResponse = soapBuilder.closeOrderEuRequest(soapMessage, url);
        CloseOrderResponse response = new CloseOrderResponse(soapResponse);
        return response;
    }
}
