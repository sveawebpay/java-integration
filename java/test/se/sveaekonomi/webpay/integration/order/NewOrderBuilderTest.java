package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class NewOrderBuilderTest {

    @Test
    public void testBuildOrderRowArrayList() {
        
        ArrayList<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>(); 
        orderRows.add(TestingTool.createExVatBasedOrderRow("1"));
        orderRows.add(TestingTool.createExVatBasedOrderRow("2"));
        
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .addOrderRows(orderRows)
                .addCustomerDetails(TestingTool.createCompanyCustomer())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .useInvoicePayment()
                .prepareRequest();
        
         assertEquals("164608142222", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
         assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
         assertEquals("2", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
    }
    
    @Test
    public void testBuildOrderWithCompanyCustomer() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addCustomerDetails(TestingTool.createCompanyCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
        .setOrderDate(TestingTool.DefaultTestDate)
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        assertEquals("164608142222", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber); 
    }
}
