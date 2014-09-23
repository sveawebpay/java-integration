package se.sveaekonomi.webpay.integration.util.test;

import java.sql.Date;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

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
    	return createIndividualCustomer(null);
    }
    
    public static CompanyCustomer createCompanyCustomer() {
    	return createCompanyCustomer(null);
    }

    public static IndividualCustomer createIndividualCustomer(COUNTRYCODE country) {
        IndividualCustomer iCustomer = null;
        country = country == null ? COUNTRYCODE.SE : country; 
        
        switch (country) {
            case SE:
                iCustomer = Item.individualCustomer()
                   .setInitials("SB")
                   .setName("Tess", "Persson")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setBirthDate("19231212")
                   .setCoAddress("c/o Eriksson, Erik")
                   .setNationalIdNumber(DefaultTestIndividualNationalIdNumber)
                   .setZipCode("99999")
                   .setLocality("Stan");
                break;
            case NO:
                iCustomer = Item.individualCustomer()
                   .setName("Ola", "Normann")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("21222222")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testveien", "2")
                   .setNationalIdNumber("17054512066")
                   .setZipCode("359")
                   .setLocality("Oslo");
                break;
            case FI:
                iCustomer = Item.individualCustomer()
                   .setName("Kukka-Maaria", "Kanerva Haapakoski")
                   .setEmail("test@svea.com")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Atomitie", "2 C")
                   .setNationalIdNumber("160264-999N")
                   .setZipCode("370")
                   .setLocality("Helsinki");
                break;
            case DK:
                iCustomer = Item.individualCustomer()
                   .setName("Hanne", "Jensen")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testvejen", "42")
                   .setCoAddress("c/o Test A/S")
                   .setNationalIdNumber("2603692503")
                   .setZipCode("2100")
                   .setLocality("KØBENHVN Ø");
                break;
            case DE:
                iCustomer = Item.individualCustomer()
                   .setName("Theo", "Giebel")
                   .setEmail("test@svea.com")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Zörgiebelweg", "21")
                   .setCoAddress("c/o Test A/S")
                   .setNationalIdNumber("19680403")
                   .setZipCode("13591")
                   .setLocality("BERLIN");
                break;
            case NL:
                iCustomer = Item.individualCustomer()
                   .setInitials("SB")
                   .setName("Sneider", "Boasman")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("999999")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Gate", "42")
                   .setBirthDate("19550307")
                   .setCoAddress("138")
                   .setNationalIdNumber("19550307")
                   .setZipCode("1102 HG")
                   .setLocality("BARENDRECHT");
                break;
            default:
                throw new SveaWebPayException("Unsupported argument for method.");
        }

        return iCustomer;
    }

    public static CompanyCustomer createMiniCompanyCustomer() {
        return Item.companyCustomer()
                   .setVatNumber("2345234")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setCompanyName("TestCompagniet");
    }

    public static CompanyCustomer createCompanyCustomer(COUNTRYCODE country) {
        CompanyCustomer cCustomer = null;
        country = country == null ? COUNTRYCODE.SE : country; 

        switch (country) {
            case SE:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("Tess, T Persson")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setCoAddress("c/o Eriksson, Erik")
                   .setZipCode("99999")
                   .setLocality("Stan");
                break;
            case NO:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("Test firma AS")
                   .setNationalIdNumber("923313850")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testveien", "1")
                   .setZipCode("259")
                   .setLocality("Oslo");
                break;
            case FI:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("Testi Yritys Oy")
                   .setNationalIdNumber("9999999-2")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testitie", "1")
                   .setZipCode("370")
                   .setLocality("Helsinki");
                break;
            case DK:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("Test A/S")
                   .setNationalIdNumber("99999993")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testvejen", "42")
                   .setZipCode("2100")
                   .setLocality("KØBENHVN Ø");
                break;
            case DE:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("K. H. Maier gmbH")
                   .setNationalIdNumber("12345")
                   .setVatNumber("DE123456789")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0241/12 34 56")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Adalbertsteinweg", "1")
                   .setZipCode("52070")
                   .setLocality("AACHEN");
                break;
            case NL:
                cCustomer = Item.companyCustomer()
                   .setCompanyName("Svea bakkerij 123")
                   .setVatNumber("NL123456789A12")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("999999")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("broodstraat", "1")
                   .setCoAddress("236")
                   .setZipCode("1111 CD")
                   .setLocality("BARENDRECHT");
                break;
            default:
                throw new SveaWebPayException("Unsupported argument for method.");
        }

        return cCustomer;
    }
    
	/**
	 * returns an invoice test order response from Svea. 
	 * 
	 * @param nameOfOriginatingTest
	 * @return
	 */
	public static CreateOrderResponse createInvoiceTestOrder( String nameOfOriginatingTest ) {
        
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
        		// add order rows
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setCustomerReference(nameOfOriginatingTest)
    	;
        
        // choose payment method and do request
        SveaRequest<SveaCreateOrder> soap_request = order.useInvoicePayment().prepareRequest(); // break and inspect here, if needed
        
        CreateOrderResponse response = order.useInvoicePayment().doRequest();
        
        return response;
	}
	
	/**
	 * returns a payment plan test order response from Svea. 
	 * 
	 * @param nameOfOriginatingTest
	 * @return
	 */
	public static CreateOrderResponse createPaymentPlanTestOrder( String nameOfOriginatingTest ) {
        		
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
        		// add order rows with sufficiently large total amount to allow payment plan to be used
                .addOrderRow(TestingTool.createPaymentPlanOrderRow())
                .addCustomerDetails(Item.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setCustomerReference(nameOfOriginatingTest)
    	;

    	// get payment plan params
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

        // choose payment method and do request
        SveaRequest<SveaCreateOrder> soap_request = order.usePaymentPlanPayment(code).prepareRequest(); // break and inspect here, if needed
        
        CreateOrderResponse response = order.usePaymentPlanPayment(code).doRequest();
        
        return response;
	}	
	
}