package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;

public class OrderBuilderTest {

    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.createOrder();
        order.setValidator(new VoidValidator());
    }
    
    @Test
    public void testThatValidatorIsCalledOnBuild() throws ValidationException {
        order.build();
        VoidValidator v = (VoidValidator) order.getValidator();
        
        assertEquals(1, v.noOfCalls);
    }
    
    @Test
    public void testBuildEmptyOrder() throws ValidationException {
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
        assertEquals("194609052222", order.getIndividualCustomer().getNationalIdNumber());
        assertEquals("Tess", order.getIndividualCustomer().getFirstName());
        assertEquals("Testson", order.getIndividualCustomer().getLastName());
        assertEquals("19231212", order.getIndividualCustomer().getBirthDate());
        assertEquals("test@svea.com", order.getIndividualCustomer().getEmail());
        assertEquals("999999", order.getIndividualCustomer().getPhoneNumber());
        assertEquals("123.123.123", order.getIndividualCustomer().getIpAddress());
        assertEquals("Gatan", order.getIndividualCustomer().getStreetAddress());
        assertEquals("23", order.getIndividualCustomer().getHouseNumber());
        assertEquals("c/o Eriksson", order.getIndividualCustomer().getCoAddress());
        assertEquals("9999", order.getIndividualCustomer().getZipCode());
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
        assertEquals(2, order.getOrderRows().get(0).getQuantity());
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
        assertEquals(100.00, order.getFixedDiscountRows().get(0).getAmount(), 0);
        assertEquals("FixedDiscount", order.getFixedDiscountRows().get(0).getDescription());
    }
    
    @Test
    public void testBuildWithOrderWithRelativeDiscount() {
        createTestRelativeDiscountBuilder();
        
        assertEquals("1", order.getRelativeDiscountRows().get(0).getDiscountId());
        assertEquals(50, order.getRelativeDiscountRows().get(0).getDiscountPercent());
        assertEquals("RelativeDiscount", order.getRelativeDiscountRows().get(0).getDescription());
        assertEquals("Relative", order.getRelativeDiscountRows().get(0).getName());
        assertEquals("st", order.getRelativeDiscountRows().get(0).getUnit());
    }
    
    @Test
    public void testBuildOrderWithOrderDate() {
        order.setOrderDate("2012-12-12");
        
        assertEquals("2012-12-12", order.getOrderDate());
    }
    
    @Test
    public void testBuildOrderWithCountryCode() {
        order.setCountryCode(COUNTRYCODE.SE);
        
        assertEquals(COUNTRYCODE.SE, order.getCountryCode());
    }
    
    @Test
    public void testBuildOrderWithCurrency() {
        order.setCurrency(CURRENCY.SEK);
        
        assertEquals("SEK", order.getCurrency());
    }
    
    @Test
    public void testBuildOrderWithClientOrderNumber() {
        order.setClientOrderNumber("33");
        
        assertEquals("33", order.getClientOrderNumber());
    }   
    
    private CreateOrderBuilder createTestCustomerIdentity(CreateOrderBuilder orderBuilder) {
         return orderBuilder.addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber("194609052222")
                .setInitials("SB")
                .setBirthDate(1923, 12, 12)
                .setName("Tess", "Testson")
                .setEmail("test@svea.com")
                .setPhoneNumber("999999")
                .setIpAddress("123.123.123")
                .setStreetAddress("Gatan", "23")
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan"));
    }
    
    private CreateOrderBuilder createCompanyDetails(CreateOrderBuilder orderBuilder) {
       return order.addCustomerDetails(Item.companyCustomer()
                .setCompanyName("TestCompagniet")
                .setVatNumber("2345234"));
    }
    
    private void createTestOrderRow() {
        order.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setUnit("st")
                .setVatPercent(25)
                .setVatDiscount(0));
    }
    
    private void createShippingFeeRow() {
        order.addFee(Item.shippingFee()
                .setAmountExVat(50)
                .setShippingId("33")
                .setDescription("Specification")
                .setVatPercent(25));
    }
    
    private void createTestInvoiceFee() {
        order.addFee(Item.invoiceFee()
                .setName("Svea fee")
                .setDescription("Fee for invoice")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
    }
    
    private void createTestFixedDiscountRow() {
        order.addDiscount(Item.fixedDiscount()
                .setDiscountId("1")
                .setAmountIncVat((double) 100.00)
                //.setDiscount((double) 100.00)
                .setAmountIncVat((double) 100.00)
                .setDescription("FixedDiscount"));
    }
    
    private void createTestRelativeDiscountBuilder() {
        order.addDiscount(Item.relativeDiscount()
                .setDiscountId("1")
                .setDiscountPercent(50)
                .setDescription("RelativeDiscount")
                .setName("Relative")
                .setUnit("st"));
    }
}
