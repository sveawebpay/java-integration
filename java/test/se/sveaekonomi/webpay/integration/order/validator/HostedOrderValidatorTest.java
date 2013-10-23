package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.payment.FakeHostedPayment;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public HostedOrderValidatorTest() {
        orderValidator = new HostedOrderValidator();
    }
    
    @Test
    public void testFailOnNullClientOrderNumber() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
                    + "MISSING VALUE - ClientOrderNumber is required. Use setClientOrderNumber(...).\n"
                    + "MISSING VALUE - Currency is required. Use setCurrency(...).\n"
                    + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnEmptyClientOrderNumber() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" 
                + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
            .addOrderRow(Item.orderRow()
                .setQuantity(1)
                .setAmountExVat(100)
                .setVatPercent(25))
            .setCurrency(TestingTool.DefaultTestCurrency)
            .setClientOrderNumber("")
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingReturnUrl() {
        String expectedMessage = "MISSING VALUE - Return url is required, setReturnUrl(...).\n";
        
        try {
            CreateOrderBuilder order = WebPay.createOrder()
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                   .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                   .setCurrency(TestingTool.DefaultTestCurrency)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addFee(Item.shippingFee())
                .addDiscount(Item.fixedDiscount())
                .addDiscount(Item.relativeDiscount());
            
            FakeHostedPayment payment = new FakeHostedPayment(order);
            payment.calculateRequestValues();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }
    
    @Test
    public void succeedOnGoodValuesSe() {
        CreateOrderBuilder order = WebPay.createOrder()
            .setValidator(new VoidValidator())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .addOrderRow(TestingTool.createMiniOrderRow())
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testValidateNLCustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder()
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCountryCode(COUNTRYCODE.NL)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testValidateDECustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder()
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCountryCode(COUNTRYCODE.DE)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
    
    @Test
    public void testFailVatPercentIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
            + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(100))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailAmountExVatIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setVatPercent(25))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailAmountExVatAndVatPercentIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountIncVat(125))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testValidateFailOrderIsNull() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use setAmountExVat(), setQuantity() and setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
                .addOrderRow(null)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailMissingIdentityInHostedNL() {
        String expectedMsg = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n" 
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n";
        
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addDiscount(TestingTool.createRelativeDiscount())
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.NL)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMsg, e.getCause().getMessage());
        }
    }
    
    @Test
    public void testFailMissingIdentityInHostedDE() {
        String expectedMsg = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n";
        
        try {
            WebPay.createOrder()
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addDiscount(TestingTool.createRelativeDiscount())
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.DE)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMsg, e.getCause().getMessage());
        }
    }
}
