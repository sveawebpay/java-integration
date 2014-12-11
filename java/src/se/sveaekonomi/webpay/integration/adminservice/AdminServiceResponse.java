package se.sveaekonomi.webpay.integration.adminservice;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdminServiceResponse {

	private boolean isOrderAccepted;
	private String resultCode;
	private String errorMessage;

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
	
	public AdminServiceResponse(NodeList nodes) {		
		setCommonResponseAttributes(nodes);
	}

	private void setCommonResponseAttributes(NodeList xmlResponse) {
		int size = xmlResponse.getLength();

		for (int i = 0; i < size; i++) {
			Element node = (Element) xmlResponse.item(i);
			String nodeName = node.getNodeName();
			// System.out.println(nodeName);

			switch (nodeName) {
			case "a:ResultCode":				
				this.setResultCode(node.getTextContent());
				this.setOrderAccepted( (this.getResultCode().endsWith("0")) ? true : false);
				break;
			case "a:ErrorMessage":
				this.setErrorMessage(node.getTextContent());
				break;
			}
		}
	}

}
