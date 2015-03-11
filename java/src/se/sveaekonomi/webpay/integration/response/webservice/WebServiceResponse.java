package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;
import se.sveaekonomi.webpay.integration.response.ResponseHelper;

public class WebServiceResponse extends ResponseHelper implements Response {
    
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
    
    public WebServiceResponse(NodeList soapMessage) {
    	
        int size = soapMessage.getLength();
        
        for (int i = 0; i < size; i++) {
            Element node = (Element) soapMessage.item(i);
            this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
            this.setResultCode(getTagValue(node, "ResultCode"));
            this.setErrorMessage(getTagValue(node, "ErrorMessage"));
        }
    }        
}
