package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.response.webservice.CampaignCode;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;

public class PaymentPlanPricePerMonth {
	
	public List<Map<String, String>> calculate(Double amount, PaymentPlanParamsResponse paymentPlanParams) {
		List<Map<String, String>> pricesPerMonth = new ArrayList<Map<String, String>>();
		
		if (null == paymentPlanParams) {
			return pricesPerMonth;
		}
		
		List<CampaignCode> campaignCodes = paymentPlanParams.getCampaignCodes();
		
        for (CampaignCode campaignCode : campaignCodes) {
        	Double fromAmount = Double.parseDouble(campaignCode.getFromAmount());
        	Double toAmount = Double.parseDouble(campaignCode.getToAmount());
        	Double monthlyAnnuityFactor = Double.parseDouble(campaignCode.getMonthlyAnnuityFactor());
        	Double notificationFee = Double.parseDouble(campaignCode.getNotificationFee());
        	
        	if (fromAmount <= amount && amount <= toAmount) {
        		Map<String, String> priceMap = new HashMap<String, String>();
        		Long pricePerMonth = Math.round(amount * monthlyAnnuityFactor + notificationFee);
        		priceMap.put("campaignCode", campaignCode.getCampaignCode());
        		priceMap.put("pricePerMonth", pricePerMonth.toString());
        		pricesPerMonth.add(priceMap);
        	}
        }
    	
    	return pricesPerMonth;
    }
}
