package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DoPaymentPlanPaymentTest {
    
    @Test
    public void testPaymentPlanRequestReturnsAcceptedResult() {
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
        
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
        .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .addCustomerDetails(TestingTool.createIndividualCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        .setOrderDate(TestingTool.DefaultTestDate)
        .setCurrency(TestingTool.DefaultTestCurrency)
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .usePaymentPlanPayment(code)
        .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
    
    @Test
    public void testDeliverPaymentPlanOrderResult() {
        long orderId = createPaymentPlanAndReturnOrderId();
        
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
        .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .setOrderId(orderId)
        .setNumberOfCreditDays(1)
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .deliverPaymentPlanOrder()
        .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
    
    private long createPaymentPlanAndReturnOrderId() {
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
        
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .addCustomerDetails(TestingTool.createIndividualCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        .setOrderDate(TestingTool.DefaultTestDate)
        .setCurrency(TestingTool.DefaultTestCurrency)
        .usePaymentPlanPayment(code)
        .doRequest();
        
        return response.orderId;
    }
}
