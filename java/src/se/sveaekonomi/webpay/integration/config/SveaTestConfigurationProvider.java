package se.sveaekonomi.webpay.integration.config;

import java.net.URL;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author klar-sar
 */
public class SveaTestConfigurationProvider implements ConfigurationProvider{
    
    @Override
    public String getUsername(PAYMENTTYPE type, COUNTRYCODE country) {
        if (type == PAYMENTTYPE.INVOICE || type == PAYMENTTYPE.PAYMENTPLAN) {
            switch (country) {
                case SE:
                    return "sverigetest";
                case NO:
                    return "norgetest2";
                case FI:
                    return "finlandtest2";
                case DK:
                    return "danmarktest2";
                case NL:
                    return "hollandtest";
                case DE:
                    return "germanytest";
                default:
                    break;
            }
        }
        
        return "";
    }

    @Override
    public String getPassword(PAYMENTTYPE type, COUNTRYCODE country) {
        if (type == PAYMENTTYPE.INVOICE || type == PAYMENTTYPE.PAYMENTPLAN) {
            switch (country) {
                case SE:
                    return "sverigetest";
                case NO:
                    return "norgetest2";
                case FI:
                    return "finlandtest2";
                case DK:
                    return "danmarktest2";
                case NL:
                    return "hollandtest";
                case DE:
                    return "germanytest";
                default:
                    break;
            }
        }
        
        return "";
    }

    @Override
    public int getClientNumber(PAYMENTTYPE type, COUNTRYCODE country) {
            switch (country) {
            case SE:
                if (type == PAYMENTTYPE.INVOICE)
                    return 79021;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 59999;
            	break;
            case NO:
                if (type == PAYMENTTYPE.INVOICE)
                    return 33308;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 32503;
            	break;
            case FI:
                if (type == PAYMENTTYPE.INVOICE)
                    return 26136;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 27136;
            	break;
            case DK:
                if (type == PAYMENTTYPE.INVOICE)
                    return 62008;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 64008;
            	break;
            case NL:
                if (type == PAYMENTTYPE.INVOICE)
                    return 85997;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 86997;
            	break;
            case DE:
                if (type == PAYMENTTYPE.INVOICE)
                    return 14997;
                else if (type == PAYMENTTYPE.PAYMENTPLAN)
                    return 16997;
            	break;
            default:
                break;
            }
            
        return 0;
    }

    @Override
    public String getMerchantId(PAYMENTTYPE type, COUNTRYCODE country) {
        if (PAYMENTTYPE.HOSTED == type) {
            return "1130";
        }
        
        return "";
    }

    @Override
    public String getSecretWord(PAYMENTTYPE type, COUNTRYCODE country) {
        if (PAYMENTTYPE.HOSTED == type) {
            return "8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3";
        }
        
        return "";
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
    	return "Integration package default SveaTestConfigurationProvider";
    }

    @Override
    public String getIntegrationCompany() {
    	return "Integration package default SveaTestConfigurationProvider.";
    }    
    
    @Override
    public String getIntegrationVersion() {
    	return "Integration package default SveaTestConfigurationProvider.";
    } 
}
