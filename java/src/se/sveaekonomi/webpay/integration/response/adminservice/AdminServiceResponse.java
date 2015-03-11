package se.sveaekonomi.webpay.integration.response.adminservice;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;

public class AdminServiceResponse implements Response {

	private Boolean isOrderAccepted;
	private String resultCode;
	private String errorMessage;

	public Boolean isOrderAccepted() {
		return isOrderAccepted;
	}

	public void setOrderAccepted(Boolean isOrderAccepted) {
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

		String resultCode = node.getElementsByTagName("a:ResultCode").item(0).getTextContent();
		this.setResultCode(resultCode);
		this.setOrderAccepted( (this.getResultCode().equals("0")) ? true : false);

		String errorMessage = node.getElementsByTagName("a:ErrorMessage").item(0).getTextContent();
		this.setErrorMessage(errorMessage);
	}
}
