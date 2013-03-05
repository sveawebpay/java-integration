package se.sveaekonomi.webpay.integration.order.create;

import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.row.Item;

public class BuildCustomerIdentity implements CreateBuilderCommand<CreateOrderBuilder> {
    
    public CreateOrderBuilder run(CreateOrderBuilder orderBuilder) {
        return orderBuilder.addCustomerDetails(Item.individualCustomer()            
                .setNationalIdNumber(194609052222L)
                .setInitials("SB")
                .setBirthDate(1923, 12, 12)
                .setName("Tess", "Testson")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123")
                .setStreetAddress("Gatan", 23)               
                .setZipCode("9999")
                .setLocality("Stan"));  
    }
}
