package testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.sveaekonomi.webpay.integration.hosted.helper.HostedRowFormatterTest;
import se.sveaekonomi.webpay.integration.hosted.helper.HostedXmlBuilderTest;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentFormTest;
import se.sveaekonomi.webpay.integration.hosted.payment.CardPaymentTest;
import se.sveaekonomi.webpay.integration.hosted.payment.DirectPaymentTest;
import se.sveaekonomi.webpay.integration.hosted.payment.HostedPaymentTest;
import se.sveaekonomi.webpay.integration.hosted.payment.PayPagePaymentTest;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodTest;
import se.sveaekonomi.webpay.integration.order.NewOrderBuilderTest;
import se.sveaekonomi.webpay.integration.order.OrderBuilderTest;
import se.sveaekonomi.webpay.integration.order.validator.HostedOrderValidatorTest;
import se.sveaekonomi.webpay.integration.order.validator.WebServiceOrderValidatorTest;
import se.sveaekonomi.webpay.integration.util.security.Base64UtilTest;
import se.sveaekonomi.webpay.integration.util.security.HashUtilTest;
import se.sveaekonomi.webpay.integration.webservice.helper.WebserviceRowFormatterTest;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilderTest;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePaymentTest;

@RunWith(Suite.class)
@SuiteClasses({
    HostedRowFormatterTest.class,
    HostedXmlBuilderTest.class,
    PaymentFormTest.class,
    CardPaymentTest.class,
    DirectPaymentTest.class,
    HostedPaymentTest.class,
    PayPagePaymentTest.class,
    PaymentMethodTest.class,
    OrderBuilderTest.class,
    HostedOrderValidatorTest.class,
    WebServiceOrderValidatorTest.class,
    Base64UtilTest.class,
    HashUtilTest.class,
    WebserviceRowFormatterTest.class,
    WebServiceXmlBuilderTest.class,
    InvoicePaymentTest.class,
    NewOrderBuilderTest.class})
public class UnitTestSuite {
    
}
