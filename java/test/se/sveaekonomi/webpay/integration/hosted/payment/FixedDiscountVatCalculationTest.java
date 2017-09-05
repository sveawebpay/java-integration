package se.sveaekonomi.webpay.integration.hosted.payment;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class FixedDiscountVatCalculationTest {

////	    print_r( $request->xmlMessage );
//	   
//	        // 240i@6% => 240 (13,58491) => 24000 (1358)
//	        $expectedDiscountRow =
//	        "  <row>\n".
//	        "   <sku>fixedDiscount</sku>\n".
//	        "   <name>-240i@6%*1</name>\n".
//	        "   <description></description>\n".
//	        "   <amount>-24000</amount>\n".      
//	        "   <vat>-1358</vat>\n";            
//	        "   <quantity>1</quantity>\n".
//	        "  </row>\n";   
//	        $this->assertEquals(1, substr_count($request->xmlMessage, $expectedDiscountRow));
//
//	        // 20i@6% => 2000 (1,132076) => 2000 (113)
//	        $expectedDiscountRow2 =
//	        "  <row>\n".
//	        "   <sku>fixedDiscount2</sku>\n".
//	        "   <name>-20i@6%*1</name>\n".
//	        "   <description></description>\n".
//	        "   <amount>-2000</amount>\n".      
//	        "   <vat>-113</vat>\n";            
//	        "   <quantity>1</quantity>\n".
//	        "  </row>\n";            
//	        $this->assertEquals(1, substr_count($request->xmlMessage, $expectedDiscountRow2));        
//	             
//	        //<response>
//	        //  <transaction id="600089">
//	        //    <paymentmethod>KORTCERT</paymentmethod>
//	        //    <merchantid>1130</merchantid>
//	        //    <customerrefno>2015-05-20T17:10:39 02:00</customerrefno>
//	        //    <amount>54000</amount>
//	        //    <currency>SEK</currency>
//	        //    <cardtype>VISA</cardtype>
//	        //    <maskedcardno>444433xxxxxx1100</maskedcardno>
//	        //    <expirymonth>01</expirymonth>
//	        //    <expiryyear>16</expiryyear>
//	        //    <authcode>304397</authcode>
//	        //    <customer>
//	        //      <firstname/>
//	        //      <lastname/>
//	        //      <initials/>
//	        //      <email/>
//	        //      <ssn>194605092222</ssn>
//	        //      <address/>
//	        //      <address2/>
//	        //      <city/>
//	        //      <country>SE</country>
//	        //      <zip/>
//	        //      <phone/>
//	        //      <vatnumber/>
//	        //      <housenumber/>
//	        //      <companyname/>
//	        //      <fullname/>
//	        //    </customer>
//	        //  </transaction>
//	        //  <statuscode>0</statuscode>
//	        //</response>        
//	    }
	
    @Test
    public void test_bv_order_sent_incvat_two_decimals_with_both_discounts() {
    	
    	CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
    			.addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
    	        .setCountryCode(TestingTool.DefaultTestCountryCode)
    	        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
    	        .setOrderDate(TestingTool.DefaultTestDate)
    	        .setCurrency(TestingTool.DefaultTestCurrency)
    	        .addOrderRow(
    	            WebPayItem.orderRow()
    	            .setAmountIncVat(1.00)
    	            .setVatPercent(6)
    	            .setQuantity(800.00)
    	            .setName("3.00i@6%*800")
    	        )
    	        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        ;

        order.
	        addDiscount( WebPayItem.fixedDiscount()
	            .setAmountIncVat(240)
	            .setVatPercent(6)
	            .setDiscountId("fixedDiscount")
	            .setName("-240i@6%*1")
    		)
	    ;
     
        order.
	        addDiscount( WebPayItem.fixedDiscount()
	            .setAmountIncVat(20)
	            .setVatPercent(6)
	            .setDiscountId("fixedDiscount2")
	            .setName("-20i@6%*1")
			)
	    ;
	
        PaymentForm form = order
        		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
        		.setReturnUrl("https://webpaypaymentgatewaytest.svea.com/webpay-admin/admin/merchantresponsetest.xhtml")
        		.getPaymentForm()
		;
        
        String actualXml = form.getXmlMessage();

        // should be 80000 (4528) + -24000 (-1358) + -2000 (-113) = 54000 (3057)
        String expectedAmountAndVat = "<amount>54000</amount><vat>3057</vat>";
        assertThat( actualXml, containsString(expectedAmountAndVat) );
        
        String expectedDiscount = "<row><sku>fixedDiscount</sku><name>-240i@6%*1</name><description></description><amount>-24000</amount><vat>-1358</vat><quantity>1.0</quantity><unit></unit></row>";
        assertThat( actualXml, containsString(expectedDiscount) );

        String expectedDiscount2 = "<row><sku>fixedDiscount2</sku><name>-20i@6%*1</name><description></description><amount>-2000</amount><vat>-113</vat><quantity>1.0</quantity><unit></unit></row>";
        assertThat( actualXml, containsString(expectedDiscount2) );  
    }
 
    @Test
    public void test_bv_order_sent_incvat_two_decimals_with_both_discounts_with_amount_only() {
    	
    	CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
    			.addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
    	        .setCountryCode(TestingTool.DefaultTestCountryCode)
    	        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
    	        .setOrderDate(TestingTool.DefaultTestDate)
    	        .setCurrency(TestingTool.DefaultTestCurrency)
    	        .addOrderRow(
    	            WebPayItem.orderRow()
    	            .setAmountIncVat(1.00)
    	            .setVatPercent(6)
    	            .setQuantity(800.00)
    	            .setName("3.00i@6%*800")
    	        )
    	        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        ;

        order.
	        addDiscount( WebPayItem.fixedDiscount()
	            .setAmountIncVat(240)
	            //.setVatPercent(6)
	            .setDiscountId("fixedDiscount")
	            .setName("-240i@6%*1")
    		)
	    ;
     
        order.
	        addDiscount( WebPayItem.fixedDiscount()
	            .setAmountIncVat(20)
	            //.setVatPercent(6)
	            .setDiscountId("fixedDiscount2")
	            .setName("-20i@6%*1")
			)
	    ;
	
        HostedPayment payment = order
    		.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
    		.setReturnUrl("https://webpaypaymentgatewaytest.svea.com/webpay-admin/admin/merchantresponsetest.xhtml")
		;
        PaymentForm form = payment.getPaymentForm();
        
        String actualXml = form.getXmlMessage();

        // should be 80000 (4528) + -24000 (-1358) + -2000 (-113) = 54000 (3057)
        String expectedAmountAndVat = "<amount>54000</amount><vat>3057</vat>";
        assertThat( actualXml, containsString(expectedAmountAndVat) );
        
        String expectedDiscount = "<row><sku>fixedDiscount</sku><name>-240i@6%*1</name><description></description><amount>-24000</amount><vat>-1358</vat><quantity>1.0</quantity><unit></unit></row>";
        assertThat( actualXml, containsString(expectedDiscount) );

        String expectedDiscount2 = "<row><sku>fixedDiscount2</sku><name>-20i@6%*1</name><description></description><amount>-2000</amount><vat>-113</vat><quantity>1.0</quantity><unit></unit></row>";
        assertThat( actualXml, containsString(expectedDiscount2) );  
    }    
          
}
