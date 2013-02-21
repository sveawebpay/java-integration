package testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.sveaekonomi.webpay.integration.response.HostedPaymentResponseTest;
import se.sveaekonomi.webpay.integration.response.WebServicePaymentsResponseTest;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddressesTest;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParamsTest;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrderTest;
import se.sveaekonomi.webpay.integration.webservice.handleorder.DeliverOrderTest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({ GetAddressesTest.class,
    CloseOrderTest.class,
    DeliverOrderTest.class,
    WebServicePaymentsResponseTest.class,
    SveaSoapBuilderTest.class,
    HostedPaymentResponseTest.class,
    GetPaymentPlanParamsTest.class})
public class IntegrationTestSuite {
     
}
