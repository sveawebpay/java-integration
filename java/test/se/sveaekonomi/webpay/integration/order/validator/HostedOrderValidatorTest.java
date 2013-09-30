package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.bind.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.payment.FakeHostedPayment;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public HostedOrderValidatorTest() {
        orderValidator = new HostedOrderValidator();
    }
    
    @Test
    public void testFailOnNullClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
                    + "MISSING VALUE - ClientOrderNumber is required. Use setClientOrderNumber(...).\n"
                    + "MISSING VALUE - Currency is required. Use setCurrency(...).\n"
                    + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"))
                .setValidator(new VoidValidator())
                .build();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testFailOnEmptyClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" 
                + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
            .addOrderRow(Item.orderRow()
                .setQuantity(1)
                .setAmountExVat(100)
                .setVatPercent(25))
            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber("")
            .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet")
                .setNationalIdNumber("1222"))
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        assertEquals(orderValidator.validate(order), expectedMessage);
    }
    
    @Test
    public void testFailOnMissingReturnUrl() {
        String expectedMessage = "MISSING VALUE - Return url is required, setReturnUrl(...).\n";
        
        try {
            CreateOrderBuilder order = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                   .setClientOrderNumber("nr22")
                   .setCurrency(CURRENCY.SEK)
                .addOrderRow(Item.orderRow()
                    .setAmountExVat(4)
                    .setVatPercent(25)
                    .setQuantity(1))
                .addFee(Item.shippingFee())
                .addDiscount(Item.fixedDiscount())
                .addDiscount(Item.relativeDiscount());
                
            FakeHostedPayment payment = new FakeHostedPayment(order);
            payment.calculateRequestValues();
            
            //Fail on no exception
            fail();
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), expectedMessage);
        }
    }
    
    @Test
    public void succeedOnGoodValuesSe() throws ValidationException {
        CreateOrderBuilder order = WebPay.createOrder()
            .setValidator(new VoidValidator())
            .setClientOrderNumber("1")
            .addOrderRow(Item.orderRow()
                .setAmountExVat(5.0)
                .setVatPercent(25)
                .setQuantity(1))
            .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"));
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testValidateNLCustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder()
                .setClientOrderNumber("1")
                .setCountryCode(COUNTRYCODE.NL)
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(5.0)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet"));
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testValidateDECustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder()
                .setClientOrderNumber("1")
                .setCountryCode(COUNTRYCODE.DE)
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(5.0)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet"));
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testFailVatPercentIsMissing() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
            + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(100))
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("")
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet")
                        .setNationalIdNumber("1222"))
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        
        assertEquals(orderValidator.validate(order), expectedMessage);
    }
    
    @Test
    public void testFailAmountExVatIsMissing() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setVatPercent(25))
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("")
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet")
                        .setNationalIdNumber("1222"))
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailAmountExVatAndVatPercentIsMissing() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountIncVat(125))
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("")
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet")
                        .setNationalIdNumber("1222"))
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testValidateFailOrderIsNull() throws ValidationException {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use setAmountExVat(), setQuantity() and setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(null)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("")
                .addCustomerDetails(Item.companyCustomer()
                        .setVatNumber("2345234")
                        .setCompanyName("TestCompagniet")
                        .setNationalIdNumber("1222"))
                .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(orderValidator.validate(order), expectedMessage);
    }
    
    @Test
    public void testFailMissingIdentityInHostedNL() throws Exception {
        String expectedMsg = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n" 
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n";
        
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createOrderRow())
                    .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                    .setDescription("RelativeDiscount"))
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.NL)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.SEK)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), expectedMsg);
        }
    }
    
    @Test
    public void testFailMissingIdentityInHostedDE() throws Exception {
        String expectedMsg = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n";
        
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createOrderRow())
                    .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                    .setDescription("RelativeDiscount"))
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.DE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(CURRENCY.SEK)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), expectedMsg);
        }
    }
}
