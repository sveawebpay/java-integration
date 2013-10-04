package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class PaymentPlanTest {

    @Test
    public void testPaymentPlanRequestObjectSpecifics() throws ValidationException {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .addOrderRow(TestingTool.createOrderRow())
                .addCustomerDetails(Item.individualCustomer()
                        .setNationalIdNumber("194605092222"))
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency(CURRENCY.SEK)
                .usePaymentPlanPayment("camp1")
                .prepareRequest();
        
        assertEquals("camp1", request.request.CreateOrderInformation.getPaymentPlanDetails("CampaignCode"));
        assertEquals(false, request.request.CreateOrderInformation.getPaymentPlanDetails("SendAutomaticGiroPaymentForm"));
    }
    
    @Test
    public void testPaymentPlanFailCompanyCustomer() throws ValidationException{
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createOrderRow())
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber("194605092222"))
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency(CURRENCY.SEK)
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
