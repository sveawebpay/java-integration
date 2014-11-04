package se.sveaekonomi.webpay.integration.response.hosted.hostedadmin;

import se.sveaekonomi.webpay.integration.Respondable;

// TODO look at extending Response...
public class ConfirmTransactionResponse implements Respondable {
	
    private boolean isOrderAccepted;
	
	@Override
    public boolean isOrderAccepted() {
        return isOrderAccepted;
    }	
	

}
