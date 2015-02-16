package se.sveaekonomi.webpay.integration.response;

public interface Response {
	
    public boolean isOrderAccepted();
    public void setOrderAccepted(boolean isOrderAccepted);
    
    public String getResultCode();
    public void setResultCode(String resultCode);
    
    public String getErrorMessage();
    public void setErrorMessage(String errorMessage);

}
