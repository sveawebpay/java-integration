package se.sveaekonomi.webpay.integration.response;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Response {
    
    private boolean isOrderAccepted;
    private String resultCode;
    private String errorMessage;
    
    public Response(NodeList soapMessage) {
    	if (soapMessage == null) {
    		return;
    	}
    	
        int size = soapMessage.getLength();
        
        for (int i = 0; i < size; i++) {
            Element node = (Element) soapMessage.item(i);
            this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
            this.setResultCode(getTagValue(node, "ResultCode"));
            this.setErrorMessage(getTagValue(node, "ErrorMessage"));
        }
    }

    // get child nodes from a node with name "tag" , or null if not found
    protected NodeList getTagNodes( Element elementNode, String tagName ) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return textList;
        }
        
        return null;
    }
    
    // get node value from a node with name "tag", or null if not found
    protected String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();            
            Node node = textList.item(0);
			String trim = node.getNodeValue().trim();
			return trim;
        }
        
        return null;
    }

    // get attribute value from a node with name "tag", or null if not found
	protected String getTagAttribute(Element elementNode, String tagName, String attributeName) {
		String[] temp;
		Node trans = elementNode.getElementsByTagName(tagName).item(0);
		NamedNodeMap attr = trans.getAttributes();
		Node nodeAttr = attr.getNamedItem("id");
		String node = nodeAttr.toString();
		temp = node.split("=");
		return temp[1].substring(1, temp[1].length() - 1);
	}
        
    public boolean isOrderAccepted() {
        return isOrderAccepted;
    }

    public void setOrderAccepted(boolean isOrderAccepted) {
        this.isOrderAccepted = isOrderAccepted;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
