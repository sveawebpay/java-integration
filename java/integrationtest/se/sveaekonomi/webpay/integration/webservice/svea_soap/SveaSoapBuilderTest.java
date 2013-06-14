package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.TestingTool;
import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;

public class SveaSoapBuilderTest {
    
    @Test
    public void testRequest() throws Exception {
        CreateOrderResponse response = WebPay.createOrder()
        .addOrderRow(TestingTool.createOrderRow())
        .addCustomerDetails(Item.individualCustomer()
        		.setNationalIdNumber("194605092222"))
        .setCountryCode(COUNTRYCODE.SE)
        .setClientOrderNumber("33")
        .setOrderDate("2012-12-12")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()
           // .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
            //returns an InvoicePayment object
        .doRequest();
        
        assertEquals(true, response.isOrderAccepted());
        //WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
     /*   CreateOrderBuilder order = WebPay.createOrder();
       try {
            String xml = xmlBuilder.getCreateOrderEuXml(request.request);
            
            String url = order.getTestmode() ? SveaConfig.SWP_TEST_WS_URL : SveaConfig.SWP_PROD_WS_URL;
            String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
            NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
            CreateOrderResponse response = new CreateOrderResponse(soapResponse);            
            
            assertEquals(true, response.isOrderAccepted());
        } catch (Exception e) {
            throw e;
        }*/
    }
}
