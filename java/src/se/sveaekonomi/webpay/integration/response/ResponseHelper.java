package se.sveaekonomi.webpay.integration.response;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResponseHelper {
	
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
	
}
