package se.sveaekonomi.webpay.integration.webservice.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebServiceXmlBuilderTest {
    
    WebServiceXmlBuilder xmlBuilder;
    String xml;
    
    @Before
    public void setUp() {
        xmlBuilder = new WebServiceXmlBuilder();
        xml = "";
    }
    
    @Test
    public void testCreateOrderEu() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .addOrderRow(Item.orderRow()
                .setAmountExVat(100.00)
                .setQuantity(1.0)
                .setVatPercent(25))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .prepareRequest();
        
        xml = xmlBuilder.getCreateOrderEuXml(request.request);
        
        final String EXPECTED_XML = "<web:request><web:Auth><web:ClientNumber>79021</web:ClientNumber><web:Username>sverigetest</web:Username><web:Password>sverigetest</web:Password></web:Auth><web:CreateOrderInformation><web:ClientOrderNumber>33</web:ClientOrderNumber><web:OrderRows><web:OrderRow><web:ArticleNumber></web:ArticleNumber><web:Description></web:Description><web:PricePerUnit>100.0</web:PricePerUnit><web:NumberOfUnits>1.0</web:NumberOfUnits><web:Unit></web:Unit><web:VatPercent>25.0</web:VatPercent><web:DiscountPercent>0.0</web:DiscountPercent></web:OrderRow></web:OrderRows><web:CustomerIdentity><web:NationalIdNumber>194605092222</web:NationalIdNumber><web:Email></web:Email><web:PhoneNumber></web:PhoneNumber><web:IpAddress></web:IpAddress><web:FullName></web:FullName><web:Street></web:Street><web:CoAddress></web:CoAddress><web:ZipCode></web:ZipCode><web:HouseNumber></web:HouseNumber><web:Locality></web:Locality><web:CountryCode>SE</web:CountryCode><web:CustomerType>Individual</web:CustomerType></web:CustomerIdentity><web:OrderDate>2012-12-12</web:OrderDate><web:AddressSelector></web:AddressSelector><web:OrderType>Invoice</web:OrderType></web:CreateOrderInformation></web:request>";
        
        assertEquals(EXPECTED_XML, xml);
    }
}
