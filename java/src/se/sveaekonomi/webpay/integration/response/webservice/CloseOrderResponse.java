package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;

public class CloseOrderResponse extends Response{
    
    public String orderType;
    
    public CloseOrderResponse(NodeList soapMessage) {
        super();
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
            //this.orderId = Long.parseLong(getTagValue(node, "SveaOrderId"));
        }
    }
    
    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return ((Node) textList.item(0)).getNodeValue().trim();
        }
        
        return null;
    }
}
