package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;

public class CloseOrderResponse extends Response{
    
    public String orderType;
    
    public CloseOrderResponse(NodeList soapMessage) {
        super(soapMessage);
        setValues(soapMessage);
    }
    
    private void setValues(NodeList soapMessage) {
        int size = soapMessage.getLength();
        
        for (int i = 0; i < size; i++) {
            Element node = (Element) soapMessage.item(i);
            // mandatory
            this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
            this.setResultCode(getTagValue(node, "ResultCode"));
            this.setErrorMessage(getTagValue(node, "ErrorMessage"));
        }
    }
}
