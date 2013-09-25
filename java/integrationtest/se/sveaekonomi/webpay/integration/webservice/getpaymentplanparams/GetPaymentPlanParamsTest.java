package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CampaignCode;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class GetPaymentPlanParamsTest {
    
    @Test
    public void testGetPaymentPlanParams() throws Exception {
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(COUNTRYCODE.SE)
            .doRequest();
        
        List<CampaignCode> campaignCodes = response.getCampaignCodes();
        
        assertEquals(true, response.isOrderAccepted());
        assertEquals(3, campaignCodes.size());
        assertEquals("213060", campaignCodes.get(0).getCampaignCode());
        assertEquals("310012", campaignCodes.get(1).getCampaignCode());
        assertEquals("410024", campaignCodes.get(2).getCampaignCode());
    }
    
    @Test
    public void testResultGetPaymentPlanParams() throws Exception {
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(COUNTRYCODE.SE)    
            .doRequest();
        
        assertEquals(response.isOrderAccepted(), true);
        assertEquals(response.getCampaignCodes().get(0).getCampaignCode(), "213060");
        assertEquals(response.getCampaignCodes().get(0).getDescription(), "Köp nu betala om 3 månader (räntefritt)");
        assertEquals(response.getCampaignCodes().get(0).getPaymentPlanType(), "InterestAndAmortizationFree");
        assertEquals(response.getCampaignCodes().get(0).getContractLengthInMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getInitialFee(), "100");
        assertEquals(response.getCampaignCodes().get(0).getNotificationFee(), "29");
        assertEquals(response.getCampaignCodes().get(0).getInterestRatePercent(), "0");
        assertEquals(response.getCampaignCodes().get(0).getNumberOfInterestFreeMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getNumberOfPaymentFreeMonths(), "3");
        assertEquals(response.getCampaignCodes().get(0).getFromAmount(), "1000");
        assertEquals(response.getCampaignCodes().get(0).getToAmount(), "50000");
    }
}
