package se.sveaekonomi.webpay.integration.webservice.handleorder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CloseOrderTest {
    
    @Test
    public void testCloseOrder() throws Exception {
        Long orderId = 0L;
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
                   
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.createTestConfig())
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0))
        .addCustomerDetails(Item.individualCustomer()          
            .setNationalIdNumber("194605092222"))        
        .setCountryCode(COUNTRYCODE.SE)
        .setClientOrderNumber("33")
        .setOrderDate("2012-12-12")
        .setCurrency(CURRENCY.SEK)
        .useInvoicePayment()
            .prepareRequest();
    
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
                       
        try {
            String xml = xmlBuilder.getCreateOrderEuXml(request.request);
                    
            String url = WebPay.createOrder().getWebserviceUrl().toString();
            String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
            NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
            CreateOrderResponse response = new CreateOrderResponse(soapResponse);            
            orderId = response.orderId;
            
            assertEquals(true, response.isOrderAccepted());
        } catch (Exception e) {
            throw e;
        }
       
        soapBuilder = new SveaSoapBuilder();
        
          CloseOrderResponse closeResponse = WebPay.closeOrder()              
                .setOrderId(orderId)
                .closeInvoiceOrder()
                .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
                .doRequest();   
         
            assertEquals(true, closeResponse.isOrderAccepted());      
    }
}
