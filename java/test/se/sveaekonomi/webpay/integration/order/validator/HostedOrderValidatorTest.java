package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;
import org.junit.Test;
import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;

public class HostedOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public HostedOrderValidatorTest() {
        orderValidator = new HostedOrderValidator();
    }      
    
    @Test
    public void testFailOnNullClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - OrgNumber is required for company customers when countrycode is SE, NO, DK or FI. Use function setCompanyIdNumber().\n"
                    + "MISSING VALUE - ClientOrderNumber is required. Use function setClientOrderNumber().\n"
                    + "MISSING VALUE - Currency is required. Use function setCurrency().\n"
                    + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n";
        
        CreateOrderBuilder order = WebPay.createOrder()
        		.addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"))
                .setValidator(new VoidValidator())
                .build();
        
        assertEquals(orderValidator.validate(order), expectedMessage);      
    }
    
    @Test
    public void testFailOnEmptyClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n";
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
    	//.setValidator(new VoidValidator())
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
    	//.setValidator(new VoidValidator())
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
    String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n"
    		+ "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
    CreateOrderBuilder order = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
    		.setQuantity(1)
    		.setAmountExVat(100))
    	//	.setVatPercent(25))
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
    String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n"
    		+ "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";
    CreateOrderBuilder order = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
    		.setQuantity(1)
    	//	.setAmountExVat(100))
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
    public void testFailAmountExVatAndVatPercentIsMissing() throws ValidationException {
    String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n"
    		+ "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
    CreateOrderBuilder order = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
    		.setQuantity(1)
    		.setAmountIncVat(125))
    	//	.setAmountExVat(100))
    	//	.setVatPercent(25))
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
    public void testValidateFailOrderisNull() throws ValidationException {
    String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n"
    		+ "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use functions setAmountExVat(), setQuantity() and setVatPercent().\n";
    CreateOrderBuilder order = WebPay.createOrder()
    	.addOrderRow(null)
    	//	.setQuantity(1)
    	//	.setAmountIncVat(125))
    	//	.setAmountExVat(100))
    	//	.setVatPercent(25))
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
}
