package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class PaymentPlanPricePerMonthTest {
    
    private PaymentPlanParamsResponse getParamsForTesting() {
        GetPaymentPlanParams request = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig());
        PaymentPlanParamsResponse response = request
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        
        return response;
    }
    
    @Test
    public void testBuildPriceCalculator() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(2000.0, paymentPlanParams);
                
        assertEquals("2029", result.get(0).get("pricePerMonth"));
        assertEquals("213060", result.get(0).get("campaignCode"));
        assertEquals("2029", result.get(1).get("pricePerMonth"));
        assertEquals("223060", result.get(1).get("campaignCode"));
        assertEquals("202", result.get(2).get("pricePerMonth"));
        assertEquals("310012", result.get(2).get("campaignCode"));
        assertEquals("129", result.get(3).get("pricePerMonth"));
        assertEquals("410024", result.get(3).get("campaignCode"));
    }
    
    @Test
    public void testBuildPriceCalculatorWithLowPrice() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(200.0, paymentPlanParams);
        
        assertTrue(result.isEmpty());
    }
}
