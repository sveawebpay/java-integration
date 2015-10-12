package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.calculation.Helper;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class PaymentPlanPricePerMonthTest {
    
	Double priceBelowAnyCampaignMinValue = 99.0; // lowest campaign is from 100.00       	 
	
    private PaymentPlanParamsResponse getParamsForTesting() {
        GetPaymentPlanParams request = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig());
        PaymentPlanParamsResponse response = request
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        
        return response;
    }
    
    
    // legacy (WebPay.paymentPlanPricePerMonth)
    @Test
    public void testBuildPriceCalculator() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(2000.0, paymentPlanParams);      
        assertEquals("213060", result.get(0).get("campaignCode"));
        assertEquals("2029", result.get(0).get("pricePerMonth"));        
        assertEquals("223060", result.get(1).get("campaignCode"));
        assertEquals("2029", result.get(1).get("pricePerMonth"));
        assertEquals("310012", result.get(2).get("campaignCode"));
        assertEquals("202", result.get(2).get("pricePerMonth"));
    }
    
    @Test
    public void testBuildPriceCalculatorWithLowPrice() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(priceBelowAnyCampaignMinValue, paymentPlanParams);	
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBuildPriceCalculatorWithLowPrice_and_ignoreMaxAndMinFlag_false_should_return_empty_result() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = WebPay.paymentPlanPricePerMonth(priceBelowAnyCampaignMinValue, paymentPlanParams);        
        assertTrue(result.isEmpty());
    }      
    
    // new (Helper.paymentPlanPricePerMonth)
    @Test
    public void testBuildPriceCalculator_new() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = Helper.paymentPlanPricePerMonth(2000.0, paymentPlanParams);      
        assertEquals("213060", result.get(0).get("campaignCode"));
        assertEquals("2029", result.get(0).get("pricePerMonth"));        
        assertEquals("223060", result.get(1).get("campaignCode"));
        assertEquals("2029", result.get(1).get("pricePerMonth"));
        assertEquals("310012", result.get(2).get("campaignCode"));
        assertEquals("202", result.get(2).get("pricePerMonth"));
    }
    
    @Test
    public void testBuildPriceCalculatorWithLowPrice_new() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = Helper.paymentPlanPricePerMonth(priceBelowAnyCampaignMinValue, paymentPlanParams);        
        assertTrue(result.isEmpty());        
    }
    
    @Test
    public void testBuildPriceCalculatorWithLowPrice_and_ignoreMaxAndMinFlag_true_should_return_nonempty_result_new() {
        PaymentPlanParamsResponse paymentPlanParams = getParamsForTesting();
        
        List<Map<String, String>> result = Helper.paymentPlanPricePerMonth(99.0, paymentPlanParams, true);        
        assertFalse(result.isEmpty());
        assertEquals("213060", result.get(0).get("campaignCode"));
        assertEquals("Köp nu betala om 3 månader (räntefritt)", result.get(0).get("description"));        
        assertEquals("128", result.get(0).get("pricePerMonth"));        
    }  
}
