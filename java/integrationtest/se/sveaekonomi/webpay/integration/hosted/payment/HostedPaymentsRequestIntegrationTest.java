package se.sveaekonomi.webpay.integration.hosted.payment;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.PaymentUrl;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedPaymentsRequestIntegrationTest {
    
    @Test
    public void test_HostedPayment_getPaymentUrl_xml() {
    	    	
    	OrderRowBuilder row = WebPayItem.orderRow()
        .setArticleNumber("665")
        .setName("Orderrow1")
        .setDescription("Orderrow description")
        .setAmountExVat(4.00)
        .setQuantity(1.0)
        .setUnit("st")
        .setVatPercent(25);
    	
    	IndividualCustomer customer = WebPayItem.individualCustomer()
		.setIpAddress("127.0.0.1");

        PaymentUrl paymentUrl = null;
		try {
			paymentUrl = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(row)
			.addCustomerDetails(customer)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setClientOrderNumber(  new String( "test_HostedPayment_getPaymentUrl_xml#" + new java.util.Date().getTime() ) )
			.setCurrency(CURRENCY.EUR)
			.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
				.setReturnUrl("https://webpaypaymentgatewaytest.svea.com/webpay-admin/admin/merchantresponsetest.xhtml")
				.setPayPageLanguageCode(LANGUAGECODE.sv )
				.getPaymentUrl();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
				
		Assert.assertTrue(paymentUrl.isOrderAccepted());
		Assert.assertNotNull(paymentUrl.getId());
		Assert.assertNotNull(paymentUrl.getCreated());
		Assert.assertEquals( "https://webpay", paymentUrl.getUrl().substring(0, 14) );  
		Assert.assertEquals( "https://test", paymentUrl.getTestUrl().substring(0, 12) );		
    }
}
