package se.sveaekonomi.webpay.integration.response;

public class Response {
    
    private boolean isOrderAccepted;
    private String resultCode;
    private String errorMessage;
    
    public Response() {
        
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
