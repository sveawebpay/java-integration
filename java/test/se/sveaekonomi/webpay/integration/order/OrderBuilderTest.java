package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class OrderBuilderTest {

    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.createOrder(SveaConfig.getDefaultConfig());
        order.setValidator(new VoidValidator());
    }
    
    @Test
    public void testThatValidatorIsCalledOnBuild() {
        order.build();
        VoidValidator v = (VoidValidator) order.getValidator();
        
        assertEquals(1, v.noOfCalls);
    }
    
    @Test
    public void testBuildEmptyOrder() {
        CreateOrderBuilder sveaRequest = order
                .setCountryCode(COUNTRYCODE.NL)
                .build();
        
        assertEquals(0, sveaRequest.getOrderRows().size());
        assertEquals(0, sveaRequest.getFixedDiscountRows().size());
    }
    
    @Test
    public void testCustomerIdentity() {
        order = createTestCustomerIdentity(order);
        
        assertEquals("SB", order.getIndividualCustomer().getInitials());
        assertEquals("194605092222", order.getIndividualCustomer().getNationalIdNumber());
        assertEquals("Tess", order.getIndividualCustomer().getFirstName());
        assertEquals("Persson", order.getIndividualCustomer().getLastName());
        assertEquals("19231212", order.getIndividualCustomer().getBirthDate());
        assertEquals("test@svea.com", order.getIndividualCustomer().getEmail());
        assertEquals("0811111111", order.getIndividualCustomer().getPhoneNumber());
        assertEquals("123.123.123", order.getIndividualCustomer().getIpAddress());
        assertEquals("Testgatan", order.getIndividualCustomer().getStreetAddress());
        assertEquals("1", order.getIndividualCustomer().getHouseNumber());
        assertEquals("c/o Eriksson, Erik", order.getIndividualCustomer().getCoAddress());
        assertEquals("99999", order.getIndividualCustomer().getZipCode());
        assertEquals("Stan", order.getIndividualCustomer().getLocality());
    }
    
    @Test
    public void testBuildCompanyDetails() {
        order = createCompanyDetails(order);
        
        assertEquals("TestCompagniet", order.getCompanyCustomer().getCompanyName());
        assertEquals("2345234", order.getCompanyCustomer().getVatNumber());
    }
    
    @Test
    public void testBuildOrderWithOneOrderRow() {
        createTestOrderRow();
        
        assertNotNull(order);
        assertEquals("1", order.getOrderRows().get(0).getArticleNumber());
        assertEquals(2, order.getOrderRows().get(0).getQuantity(), 0);
        assertEquals(100.00, order.getOrderRows().get(0).getAmountExVat(), 0);
        assertEquals("Specification", order.getOrderRows().get(0).getDescription());
        assertEquals("st", order.getOrderRows().get(0).getUnit());
        assertEquals(25, order.getOrderRows().get(0).getVatPercent(), 0);
        assertEquals(0, order.getOrderRows().get(0).getVatDiscount());
    }
    
    @Test
    public void testBuildOrderWithShippingFee() {
        createShippingFeeRow();
        
        assertEquals("33", order.getShippingFeeRows().get(0).getShippingId());
        assertEquals("Specification", order.getShippingFeeRows().get(0).getDescription());
        assertEquals(50, order.getShippingFeeRows().get(0).getAmountExVat(), 0);
        assertEquals(25, order.getShippingFeeRows().get(0).getVatPercent(), 0);
    }
    
    @Test
    public void testBuildWithInvoiceFee() {
        createTestInvoiceFee();
        
        assertEquals("Svea fee", order.getInvoiceFeeRows().get(0).getName());
        assertEquals("Fee for invoice", order.getInvoiceFeeRows().get(0).getDescription());
        assertEquals(50, order.getInvoiceFeeRows().get(0).getAmountExVat(), 0);
        assertEquals("st", order.getInvoiceFeeRows().get(0).getUnit());
        assertEquals(25, order.getInvoiceFeeRows().get(0).getVatPercent(), 0);
        assertEquals(0, order.getInvoiceFeeRows().get(0).getDiscountPercent(), 0);
    }
    
    @Test
    public void testBuildOrderWithFixedDiscount() {
        createTestFixedDiscountRow();
        
        assertEquals("1", order.getFixedDiscountRows().get(0).getDiscountId());
        assertEquals(100.00, order.getFixedDiscountRows().get(0).getAmountIncVat(), 0);
        assertEquals("FixedDiscount", order.getFixedDiscountRows().get(0).getDescription());
    }
    
    @Test
    public void testBuildWithOrderWithRelativeDiscount() {
        createTestRelativeDiscountBuilder();
        
        assertEquals("1", order.getRelativeDiscountRows().get(0).getDiscountId());
        assertEquals(50, order.getRelativeDiscountRows().get(0).getDiscountPercent(), 0);
        assertEquals("RelativeDiscount", order.getRelativeDiscountRows().get(0).getDescription());
        assertEquals("Relative", order.getRelativeDiscountRows().get(0).getName());
        assertEquals("st", order.getRelativeDiscountRows().get(0).getUnit());
    }
    
    @Test
    public void testBuildOrderWithOrderDate() {
        order.setOrderDate(TestingTool.DefaultTestDate);
        
        assertEquals(TestingTool.DefaultTestDate, order.getOrderDate());
    }
    
    @Test
    public void testBuildOrderWithCountryCode() {
        order.setCountryCode(TestingTool.DefaultTestCountryCode);
        
        assertEquals(COUNTRYCODE.SE, order.getCountryCode());
    }
    
    @Test
    public void testBuildOrderWithCurrency() {
        order.setCurrency(TestingTool.DefaultTestCurrency);
        
        assertEquals("SEK", order.getCurrency());
    }
    
    @Test
    public void testBuildOrderWithClientOrderNumber() {
        order.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber);
        
        assertEquals(TestingTool.DefaultTestClientOrderNumber, order.getClientOrderNumber());
    }   
    
    private CreateOrderBuilder createTestCustomerIdentity(CreateOrderBuilder orderBuilder) {
         return orderBuilder.addCustomerDetails(TestingTool.createIndividualCustomer());
    }
    
    private CreateOrderBuilder createCompanyDetails(CreateOrderBuilder orderBuilder) {
       return order.addCustomerDetails(TestingTool.createMiniCompanyCustomer());
    }
    
    private void createTestOrderRow() {
        order.addOrderRow(TestingTool.createExVatBasedOrderRow("1"));
    }
    
    private void createShippingFeeRow() {
        order.addFee(TestingTool.createExVatBasedShippingFee());
    }
    
    private void createTestInvoiceFee() {
        order.addFee(TestingTool.createExVatBasedInvoiceFee());
    }
    
    private void createTestFixedDiscountRow() {
        order.addDiscount(Item.fixedDiscount()
                .setDiscountId("1")
                .setAmountIncVat((double) 100.00)
                .setDescription("FixedDiscount"));
    }
    
    private void createTestRelativeDiscountBuilder() {
        order.addDiscount(TestingTool.createRelativeDiscount());
    }
}
