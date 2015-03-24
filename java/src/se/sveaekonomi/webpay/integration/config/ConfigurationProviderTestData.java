package se.sveaekonomi.webpay.integration.config;

import java.net.URL;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class ConfigurationProviderTestData implements ConfigurationProvider {

    @Override
    public String getUsername(PAYMENTTYPE type, COUNTRYCODE country) {
        return "sverigetest";
    }

    @Override
    public String getPassword(PAYMENTTYPE type, COUNTRYCODE country) {
        return "sverigetest";
    }

    @Override
    public int getClientNumber(PAYMENTTYPE type, COUNTRYCODE country) {
        return 79021;
    }

    @Override
    public String getMerchantId(PAYMENTTYPE type, COUNTRYCODE country) {
        return "1130";
    }

    @Override
    public String getSecretWord(PAYMENTTYPE type, COUNTRYCODE country) {
        return "8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3";        
    }

    @Override
    public URL getEndPoint(PAYMENTTYPE type) {
        if (PAYMENTTYPE.HOSTED == type) {
            return SveaConfig.getTestPayPageUrl();
        }
        
        if (PAYMENTTYPE.HOSTED_ADMIN == type) {
            return SveaConfig.getTestHostedAdminUrl();
        }
        
        if (PAYMENTTYPE.ADMIN_TYPE == type) {
            return SveaConfig.getTestAdminServiceUrl();
        }
        
        return SveaConfig.getTestWebserviceUrl();
    }

    
    @Override
    public String getIntegrationPlatform() {
    	return "Please provide your integration platform here.";
    }

    @Override
    public String getIntegrationCompany() {
    	return "Please provide your integration company here.";
    }    
    
    @Override
    public String getIntegrationVersion() {
    	return "Please provide your integration version here.";
    }     
}
