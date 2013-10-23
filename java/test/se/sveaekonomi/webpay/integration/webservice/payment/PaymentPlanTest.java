package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class PaymentPlanTest {

    @Test
    public void testPaymentPlanRequestObjectSpecifics() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.individualCustomer()
                        .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentPlanPayment("camp1")
                .prepareRequest();
        
        assertEquals("camp1", request.request.CreateOrderInformation.getPaymentPlanDetails("CampaignCode"));
        assertEquals(false, request.request.CreateOrderInformation.getPaymentPlanDetails("SendAutomaticGiroPaymentForm"));
    }
    
    @Test
    public void testPaymentPlanFailCompanyCustomer() {
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestCompanyNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentPlanPayment("camp1")
                .prepareRequest();
            
            //Fail on no exception
            fail();
        } catch(SveaWebPayException e) {
            String expectedMessage = "ERROR - CompanyCustomer is not allowed to use payment plan option.";
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
