package se.sveaekonomi.webpay.integration.util.test;

import java.sql.Date;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;

public class TestingTool {
    public static final COUNTRYCODE DefaultTestCountryCode = COUNTRYCODE.SE;
    public static final CURRENCY DefaultTestCurrency = CURRENCY.SEK;
    public static final String DefaultTestClientOrderNumber = "33";
    public static final String DefaultTestCustomerReferenceNumber = "ref33";
    public static final String DefaultTestIndividualNationalIdNumber = "194605092222";
    public static final String DefaultTestCompanyNationalIdNumber = "164608142222";
    public static final Date DefaultTestDate = Date.valueOf("2012-12-12");
	
    public static OrderRowBuilder createMiniOrderRow() {
        return Item.orderRow()
                   .setQuantity(1.0)
                   .setAmountExVat(4)
                   .setAmountIncVat(5);
    }
    
    public static OrderRowBuilder createExVatBasedOrderRow(String articleNumber) {
    	articleNumber = articleNumber == null ? "1" : articleNumber;
        return Item.orderRow()
                .setArticleNumber(articleNumber)
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(25)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createPaymentPlanOrderRow() {
        return Item.orderRow()
                   .setArticleNumber("1")
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountExVat(1000.00)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setVatDiscount(0);
    }
    
    public static OrderRowBuilder createIncVatBasedOrderRow(String articleNumber) {
    	articleNumber = articleNumber != null ? articleNumber : "1";
        return Item.orderRow()
                   .setArticleNumber(articleNumber)
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountIncVat(125)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static OrderRowBuilder createIncAndExVatOrderRow(String articleNumber) {
    	articleNumber = articleNumber != null ? articleNumber : "1";
        return Item.orderRow()
                   .setArticleNumber(articleNumber)
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountIncVat(125)
                   .setAmountExVat(100)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }
    
    public static OrderRowBuilder createOrderRowDe() {
        return Item.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(19)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createOrderRowNl() {
        return Item.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(6)
                .setVatDiscount(0); 
    }
    
    public static ShippingFeeBuilder createExVatBasedShippingFee() {
        return Item.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static ShippingFeeBuilder createIncVatBasedShippingFee() {
        return Item.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountIncVat(62.50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static ShippingFeeBuilder createIncAndExVatShippingFee() {
        return Item.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountIncVat(62.50)
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createExVatBasedInvoiceFee() {
        return Item.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createIncVatBasedInvoiceFee() {
        return Item.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountIncVat(62.50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createIncAndExVatInvoiceFee() {
        return Item.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountIncVat(62.50)
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }

    public static RelativeDiscountBuilder createRelativeDiscount() {
        return Item.relativeDiscount()
                   .setDiscountId("1")
                   .setName("Relative")
                   .setDescription("RelativeDiscount")
                   .setUnit("st")
                   .setDiscountPercent(50.0);
    }

    public static IndividualCustomer createIndividualCustomer() {
        return Item.individualCustomer()
                   .setInitials("SB")
                   .setName("Tess", "Persson")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setBirthDate(1923,12,12)
                   .setCoAddress("c/o Eriksson, Erik")
                   .setNationalIdNumber(DefaultTestIndividualNationalIdNumber)
                   .setZipCode("99999")
                   .setLocality("Stan");
    }

    public static IndividualCustomer createIndividualCustomerNl() {
        return Item.individualCustomer()
                   .setInitials("SB")
                   .setBirthDate(1923,12,12)
                   .setName("Svea bakkerij", "123")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("999999")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Gatan", "23")
                   .setCoAddress("c/o Eriksson")
                   .setZipCode("9999")
                   .setLocality("Stan");
    }

    public static CompanyCustomer createMiniCompanyCustomer() {
        return Item.companyCustomer()
                   .setVatNumber("2345234")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setCompanyName("TestCompagniet");
    }

    public static CompanyCustomer createCompanyCustomer() {
        return Item.companyCustomer()
                   .setCompanyName("Tess, T Persson")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setCoAddress("c/o Eriksson, Erik")
                   .setZipCode("99999")
                   .setLocality("Stan");
    }
}