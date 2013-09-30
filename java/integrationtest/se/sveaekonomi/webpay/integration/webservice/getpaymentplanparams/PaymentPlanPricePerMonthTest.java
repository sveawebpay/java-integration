package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class PaymentPlanPricePerMonthTest {
    
    private PaymentPlanParamsResponse getParamsForTesting() throws Exception {
        GetPaymentPlanParams request = WebPay.getPaymentPlanParams();
        PaymentPlanParamsResponse response = request
                .setCountryCode(COUNTRYCODE.SE)
                .doRequest();
        
        return response;
    }
    
    @Test
    public void testBuildPriceCalculator() throws Exception {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(2000.0, paymentPlanParams);
        
        assertEquals("213060", result.get(0).get("campaignCode"));
        assertEquals("2029", result.get(0).get("pricePerMonth"));
        
        assertEquals("202", result.get(1).get("pricePerMonth"));
    }
    
    @Test
    public void testBuildPriceCalculatorWithLowPrice() throws Exception {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(200.0, paymentPlanParams);
        
        assertTrue(result.isEmpty());
    }
}
