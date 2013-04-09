package se.sveaekonomi.webpay.integration.webservice.handleorder;

import java.net.URL;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrderInformation;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CloseOrder {
    
    private CloseOrderBuilder order;  
    
    public CloseOrder(CloseOrderBuilder order) {
        this.order = order;
    }
    
    
    protected SveaAuth getStoreAuthorization() {
        //return conf.getAuthorizationForWebServicePayments(this.order.getOrderType());
    	 SveaAuth auth = new SveaAuth();
    	 PAYMENTTYPE type = (order.getOrderType() == "Invoice" ? PAYMENTTYPE.INVOICE : PAYMENTTYPE.PAYMENTPLAN);
         auth.Username = order.getConfig().getUsername(type, order.getCountryCode());
         auth.Password = order.getConfig().getPassword(type, order.getCountryCode());
         auth.ClientNumber = order.getConfig().getClientNumber(type, order.getCountryCode());
         return auth;
    }
    
    /**
     * Note! This function may change in future updates.
     * @param userName
     * @param password
     * @param clientNumber
     * @return
     */
 /*   public CloseOrder setPasswordBasedAuthorization(String userName, String password, int clientNumber) {
        conf.setPasswordBasedAuthorization(userName, password, clientNumber, order.getOrderType());    
        return this;
    }*/
    
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
    	URL url = order.getConfig().getEndPoint(PAYMENTTYPE.INVOICE);
        SveaRequest<SveaCloseOrder> request = this.prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml = xmlBuilder.getCloseOrderEuXml(request.request);
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("CloseOrderEu", xml);
        NodeList soapResponse = soapBuilder.closeOrderEuRequest(soapMessage, url.toString());
        CloseOrderResponse response = new CloseOrderResponse(soapResponse);
        return response;
    }
}
