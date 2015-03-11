package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CampaignCode;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class GetPaymentPlanParamsTest {

    @Test
    public void testGetPaymentPlanParams() {
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .doRequest();
        
        List<CampaignCode> campaignCodes = response.getCampaignCodes();
        
        assertTrue(response.isOrderAccepted());
        assertEquals(4, campaignCodes.size());
        assertEquals("213060", campaignCodes.get(0).getCampaignCode());
        assertEquals("223060", campaignCodes.get(1).getCampaignCode());
        assertEquals("310012", campaignCodes.get(2).getCampaignCode());
        assertEquals("410024", campaignCodes.get(3).getCampaignCode());
    }
    
    @Test
    public void testResultGetPaymentPlanParams() {
        PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("213060", response.getCampaignCodes().get(0).getCampaignCode());
        assertEquals("Köp nu betala om 3 månader (räntefritt)", response.getCampaignCodes().get(0).getDescription());
        assertEquals("InterestAndAmortizationFree", response.getCampaignCodes().get(0).getPaymentPlanType());
        assertEquals("3", response.getCampaignCodes().get(0).getContractLengthInMonths());
        assertEquals("100", response.getCampaignCodes().get(0).getInitialFee());
        assertEquals("29", response.getCampaignCodes().get(0).getNotificationFee());
        assertEquals("0", response.getCampaignCodes().get(0).getInterestRatePercent());
        assertEquals("3", response.getCampaignCodes().get(0).getNumberOfInterestFreeMonths());
        assertEquals("3", response.getCampaignCodes().get(0).getNumberOfPaymentFreeMonths());
        assertEquals("1000", response.getCampaignCodes().get(0).getFromAmount());
        assertEquals("50000", response.getCampaignCodes().get(0).getToAmount());
    }
}
