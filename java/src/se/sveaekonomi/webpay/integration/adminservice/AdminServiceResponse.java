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
		Element node = (Element) xmlResponse.item(0);

		String resultCode = node.getElementsByTagName("a:ResultCode").item(0).getTextContent();	// check what if =nil?
		this.setResultCode(resultCode);
		this.setOrderAccepted( (this.getResultCode().equals("0")) ? true : false);

		String errorMessage = node.getElementsByTagName("a:ErrorMessage").item(0).getTextContent();	// check what if =nil?
		this.setErrorMessage(errorMessage);
	}
}
