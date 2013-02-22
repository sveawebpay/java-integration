package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class PaymentPlanTest {
    
    private CreateOrderBuilder orderBuilder;
    
    @Before
    public void setUp() {
        orderBuilder = new CreateOrderBuilder();
    }
    
    @Test
    public void testPaymentPlanRequestObjectSpecifics() throws ValidationException{
        this.orderBuilder.addOrderRow(Item.orderRow()
              .setArticleNumber("1")
              .setQuantity(2)
              .setAmountExVat(100.00)
              .setDescription("Specification")
              .setName("Prod")
              .setUnit("st")
              .setVatPercent(25)
              .setDiscountPercent(0));
        
        orderBuilder.addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
        
        orderBuilder.addCustomerDetails(Item.individualCustomer()
              .setSsn(194605092222L)
              .setBirthDate(1923,  12, 12)
              .setName("Tess", "Testson")
              .setEmail("test@svea.com")
              .setPhoneNumber(999999)
              .setIpAddress("123.123.123")
              .setStreetAddress("Gatan", 23)
              .setCoAddress("c/o Eriksson")
              .setZipCode("9999")
              .setLocality("Stan"));

        SveaRequest<SveaCreateOrder> request = this.orderBuilder
              .setTestmode()
              .setCountryCode(COUNTRYCODE.SE)
              .setAddressSelector("ad33")
              .setOrderDate("2012-12-12")
              .setClientOrderNumber("33")
              .setCurrency("SEK")
              .usePaymentPlanPayment("camp1")
              .prepareRequest();
     
        assertEquals("camp1", request.request.CreateOrderInformation.getPaymentPlanDetails("CampaignCode"));
        assertEquals(false, request.request.CreateOrderInformation.getPaymentPlanDetails("SendAutomaticGiroPaymentForm"));
    }
}
