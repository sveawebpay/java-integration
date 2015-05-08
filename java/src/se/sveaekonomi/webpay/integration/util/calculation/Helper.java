package se.sveaekonomi.webpay.integration.util.calculation;

import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.PaymentPlanPricePerMonth;


public class Helper {
    /**
     * This is a helper function provided to calculate the monthly price for the different payment plan options for a given sum. This information may be used when displaying i.e. payment options to the customer by checkout, or to fetch the lowest amount due per month for a given product price to display.
     * 
     * ```java
     * ...
     * 	Response is a List of Map items, each with keys 'campaignCode', 'description' and 'pricePerMonth' holding the individual campaign code w/description
     *  and price due/month for the given amount. 
     * 	List<Map<String, String>> response = WebPay.paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params);	
     * ...
     * ```
     * 
     */
    public static List<Map<String, String>> paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params, Boolean ignoreMaxAndMinFlag) {
        return new PaymentPlanPricePerMonth().calculate(amount, params, ignoreMaxAndMinFlag);
    }

    public static List<Map<String, String>> paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params) {
        return new PaymentPlanPricePerMonth().calculate(amount, params, false);
    }
}
