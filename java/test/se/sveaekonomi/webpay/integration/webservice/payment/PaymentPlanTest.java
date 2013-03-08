package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class PaymentPlanTest {
    
  
    
    @Before
    public void setUp() {
    
    }
    
    @Test
    public void testPaymentPlanRequestObjectSpecifics() throws ValidationException{
    	SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
              .setArticleNumber("1")
              .setQuantity(2)
              .setAmountExVat(100.00)
              .setDescription("Specification")
              .setName("Prod")
              .setUnit("st")
              .setVatPercent(25)
              .setDiscountPercent(0))
        
        .addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
        
        .addCustomerDetails(Item.individualCustomer()
              .setNationalIdNumber("194605092222")
              .setBirthDate(1923,  12, 12)
              .setName("Tess", "Testson")
              .setEmail("test@svea.com")
              .setPhoneNumber(999999)
              .setIpAddress("123.123.123")
              .setStreetAddress("Gatan", 23)
              .setCoAddress("c/o Eriksson")
              .setZipCode("9999")
              .setLocality("Stan"))
                      
              .setCountryCode(COUNTRYCODE.SE)
              .setAddressSelector("ad33")
              .setOrderDate("2012-12-12")
              .setClientOrderNumber("33")
              .setCurrency(CURRENCY.SEK)
              .usePaymentPlanPayment("camp1")
              .prepareRequest();
     
        assertEquals("camp1", request.request.CreateOrderInformation.getPaymentPlanDetails("CampaignCode"));
        assertEquals(false, request.request.CreateOrderInformation.getPaymentPlanDetails("SendAutomaticGiroPaymentForm"));
    }
}
