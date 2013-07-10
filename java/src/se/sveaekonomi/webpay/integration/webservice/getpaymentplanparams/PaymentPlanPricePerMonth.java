package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.response.webservice.CampaignCode;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;

public class PaymentPlanPricePerMonth {
	
	public List<Map<String, String>> calculate(Double price, PaymentPlanParamsResponse params) {
		if (null == params) {
			return null;
		}
		
		List<CampaignCode> campaignCodes = params.getCampaignCodes();
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
        for (CampaignCode campaignCode : campaignCodes) {
        	Double fromAmount = Double.parseDouble(campaignCode.getFromAmount());
        	Double toAmount = Double.parseDouble(campaignCode.getToAmount());
        	Double monthlyAnnuityFactor = Double.parseDouble(campaignCode.getMonthlyAnnuityFactor());
        	Double notificationFee = Double.parseDouble(campaignCode.getNotificationFee());
        	
        	if (fromAmount <= price && price <= toAmount) {
        		Map<String, String> pair = new HashMap<String, String>();
        		Long pricePerMonth = Math.round(price * monthlyAnnuityFactor + notificationFee);
        		pair.put("pricePerMonth", pricePerMonth.toString());
        		pair.put("campaignCode", campaignCode.getCampaignCode());
        		result.add(pair);
        	}
        }
    	
    	return result;
    }
}
