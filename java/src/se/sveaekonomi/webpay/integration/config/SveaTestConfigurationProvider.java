package se.sveaekonomi.webpay.integration.config;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import java.net.URL;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * 
 * @author klar-sar
 *
 */
public class SveaTestConfigurationProvider implements ConfigurationProvider{
	
		
	@Override	
	public String getUsername(PAYMENTTYPE type, COUNTRYCODE country) {
		if(type == PAYMENTTYPE.INVOICE || type == PAYMENTTYPE.PAYMENTPLAN){					
			switch (country) {
			case SE:					
				return "sverigetest";
			case NO:			
				return "webpay_test_no";
			case FI:
				return "finlandtest";
			case DK:
				return "danmarktest";
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
		if(type == PAYMENTTYPE.INVOICE || type == PAYMENTTYPE.PAYMENTPLAN){					
			switch (country) {
			case SE:					
				return "sverigetest";
			case NO:			
				return "dvn349hvs9+29hvs";
			case FI:
				return "finlandtest";
			case DK:
				return "danmarktest";
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
				if(type == PAYMENTTYPE.INVOICE)
					return 79021;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 59999;
			case NO:			
				if(type == PAYMENTTYPE.INVOICE)
					return 32666;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 36000;
			case FI:
				if(type == PAYMENTTYPE.INVOICE)
					return 29995;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 29992;
			case DK:
				if(type == PAYMENTTYPE.INVOICE)
					return 60006;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 60004;
			case NL:
				if(type == PAYMENTTYPE.INVOICE)
					return 85997;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 86997;
			case DE:
				if(type == PAYMENTTYPE.INVOICE)
					return 14997;
				else if(type == PAYMENTTYPE.PAYMENTPLAN)
					return 16997;
			default:
				break;						
			}							
		return 0;
	}

	@Override
	public String getMerchantId(PAYMENTTYPE type, COUNTRYCODE country) {
		if(PAYMENTTYPE.HOSTED == type)
			return "1130";		
		return "";
	}

	@Override
	public String getSecret(PAYMENTTYPE type, COUNTRYCODE country) {
		if(PAYMENTTYPE.HOSTED == type)
			return "8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3";
		return "";
	}

	@Override
	public URL getEndPoint(PAYMENTTYPE type) {
		if(PAYMENTTYPE.HOSTED == type)
			return SveaConfig.getTestPayPageUrl();		
		return SveaConfig.getTestWebserviceUrl();		
	}

}
