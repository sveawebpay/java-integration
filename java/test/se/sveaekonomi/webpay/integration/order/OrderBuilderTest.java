package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class OrderBuilderTest {
    
    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = new CreateOrderBuilder();
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
        
        assertEquals(sveaRequest.getOrderRows().size(), 0);
        assertEquals(sveaRequest.getFixedDiscountRows().size(), 0);
    }
    
    @Test
    public void testCustomerIdentity() {
        order = createTestCustomerIdentity(order);

        assertEquals(order.getIndividualCustomer().getInitials(), "SB");
        assertEquals(order.getIndividualCustomer().getSsn(), 194609052222L);
        assertEquals(order.getIndividualCustomer().getFirstName(), "Tess");
        assertEquals(order.getIndividualCustomer().getLastName(), "Testson");
        assertEquals(order.getIndividualCustomer().getBirthDate(), 19231212);
        assertEquals(order.getIndividualCustomer().getEmail(), "test@svea.com");
        assertEquals(order.getIndividualCustomer().getPhoneNumber(), 999999, 0);
        assertEquals(order.getIndividualCustomer().getIpAddress(), "123.123.123");
        assertEquals(order.getIndividualCustomer().getStreetAddress(), "Gatan");
        assertEquals(order.getIndividualCustomer().getHouseNumber(), 23, 0);
        assertEquals(order.getIndividualCustomer().getCoAddress(), "c/o Eriksson");
        assertEquals(order.getIndividualCustomer().getZipCode(), "9999");
        assertEquals(order.getIndividualCustomer().getLocality(), "Stan");
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
        
        assertTrue(order != null);
        assertEquals(order.getOrderRows().get(0).getArticleNumber(), "1");
        assertEquals(order.getOrderRows().get(0).getQuantity(), 2);
        assertEquals(order.getOrderRows().get(0).getAmountExVat(), 100.00, 0);
        assertEquals(order.getOrderRows().get(0).getDescription(), "Specification");
        assertEquals(order.getOrderRows().get(0).getUnit(), "st");
        assertEquals(order.getOrderRows().get(0).getVatPercent(), 25);
        assertEquals(order.getOrderRows().get(0).getVatDiscount(), 0);
    }
    
    @Test
    public void testBuildOrderWithShippingFee() {
        createShippingFeeRow();

        assertEquals(order.getShippingFeeRows().get(0).getShippingId(), "33");
        assertEquals("Specification", order.getShippingFeeRows().get(0).getDescription());
        assertEquals(50, order.getShippingFeeRows().get(0).getAmountExVat(), 0);
        assertEquals(25, order.getShippingFeeRows().get(0).getVatPercent());
    }
    
    @Test
    public void testBuildWithInvoiceFee() {
        createTestInvoiceFee();

        assertEquals(order.getInvoiceFeeRows().get(0).getName(), "Svea fee");
        assertEquals(order.getInvoiceFeeRows().get(0).getDescription(), "Fee for invoice");
        assertEquals(order.getInvoiceFeeRows().get(0).getAmountExVat(), 50, 0);
        assertEquals(order.getInvoiceFeeRows().get(0).getUnit(), "st");
        assertEquals(order.getInvoiceFeeRows().get(0).getVatPercent(), 25);
        assertEquals(order.getInvoiceFeeRows().get(0).getDiscountPercent(), 0, 0);
    }
    
    @Test
    public void testBuildOrderWithFixedDiscount() {
        createTestFixedDiscountRow();

        assertEquals("1", order.getFixedDiscountRows().get(0).getDiscountId());
        assertEquals(100.00, order.getFixedDiscountRows().get(0).getDiscount(), 0);
        assertEquals("FixedDiscount", order.getFixedDiscountRows().get(0).getDescription());
    }
    
    @Test
    public void testBuildWithOrderWithRelativeDiscount() {
        createTestRelativeDiscountBuilder();

        assertEquals("1", order.getRelativeDiscountRows().get(0).getDiscountId());
        assertEquals(50, order.getRelativeDiscountRows().get(0).getDiscountPercent());
        assertEquals("RelativeDiscount", order.getRelativeDiscountRows().get(0).getDescription());
        assertEquals(order.getRelativeDiscountRows().get(0).getName(), "Relative");
        assertEquals(order.getRelativeDiscountRows().get(0).getUnit(), "st");
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
        order.setCurrency("SEK");
        
        assertEquals("SEK", order.getCurrency());
    }
    
    @Test
    public void testBuildOrderWithClientOrderNumber() {
        order.setClientOrderNumber("33");
        
        assertEquals("33", order.getClientOrderNumber());
    }
    
    private CreateOrderBuilder createTestCustomerIdentity(CreateOrderBuilder orderBuilder) {
         orderBuilder.addCustomerDetails(Item.individualCustomer()
                .setSsn(194609052222L)
                .setInitials("SB")
                .setBirthDate(1923, 12, 12)
                .setName("Tess", "Testson")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123")
                .setStreetAddress("Gatan", 23)             
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan"));
         
        return orderBuilder;
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
                .setDiscount((double) 100.00)
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
