package se.sveaekonomi.webpay.integration.response;

public interface Response {
	
    public Boolean isOrderAccepted();
    public void setOrderAccepted(Boolean isOrderAccepted);
    
    public String getResultCode();
    public void setResultCode(String resultCode);
    
    public String getErrorMessage();
    public void setErrorMessage(String errorMessage);

}
