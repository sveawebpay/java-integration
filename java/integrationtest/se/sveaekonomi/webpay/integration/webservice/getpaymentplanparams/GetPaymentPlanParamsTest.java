package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CampaignCode;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;

public class GetPaymentPlanParamsTest {
    
    @Test
    public void testGetPaymentPlanParams() throws Exception {
        try {
            PaymentPlanParamsResponse response = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())                
                //.setPasswordBasedAuthorization("sverigetest", "sverigetest", 59999)
                .doRequest();
            
            List<CampaignCode> campaignCodes = response.getCampaignCodes();
            assertEquals(true, response.isOrderAccepted());
            assertEquals(3, campaignCodes.size());
            assertEquals("213060", campaignCodes.get(0).getCampaignCode());
            assertEquals("310012", campaignCodes.get(1).getCampaignCode());
            assertEquals("410024", campaignCodes.get(2).getCampaignCode());
        } catch (Exception e) {
            throw e;
        }
    }
}
