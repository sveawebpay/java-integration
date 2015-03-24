package se.sveaekonomi.webpay.integration.config;

import java.net.URL;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 *  Create a class (eg. one for testing values, one for production) that implements the ConfigurationProvider Interface.
 *  Let the implemented functions return the authorization values asked for.
 *  The integration package will then call these functions to get the value from your database.
 *  Later when starting a WebPay action in your integration file, put an instance of your class as parameter to the
 *  constructor.
 * @author klar-sar
 */
public interface ConfigurationProvider {

    /**
     * Get the return value from your database or likewise
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
     * @param country code
     * @return user name
     */
    public String getUsername(PAYMENTTYPE type, COUNTRYCODE country);
    
    /**
     * Get the return value from your database or likewise
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
     * @param country code
     * @return password
     */
    public String getPassword(PAYMENTTYPE type, COUNTRYCODE country);
    
    /**
     * Get the return value from your database or likewise
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
     * @param country code
     * @return client number
     */
    public int getClientNumber(PAYMENTTYPE type, COUNTRYCODE country);
    
    /**
     * Get the return value from your database or likewise
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
     * @param country code
     * @return merchant id
     */
    public String getMerchantId(PAYMENTTYPE type, COUNTRYCODE country);
    
    /**
     * Get the return value from your database or likewise
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN
     * @param country code
     * @return secret word
     */
    public String getSecretWord(PAYMENTTYPE type, COUNTRYCODE country);
    
    /**
     * Constants for the end point url found in the class SveaConfig
     * @param type eg. HOSTED, INVOICE or PAYMENTPLAN, HOSTED_ADMIN, ADMIN_TYPE
     * @return
     */
    public URL getEndPoint(PAYMENTTYPE type);

    /**
     * Use this to provide information about your integration platform (i.e. Magento, OpenCart et al), that will be sent to Svea with every service
     * request. Should return a string. The information provided is sent as plain text and should not include any confidential information.
     */    
    public String getIntegrationPlatform();
    
    /**
     * Use this to provide information about the company providing this particular integration (i.e. Svea Ekonomi, for the Svea Opencart module, et al), that 
     * will be sent to Svea with every service request. Should return a string. The information provided is sent as plain text and should not include any 
     * confidential information.
     */  
    public String getIntegrationCompany();
    
    /**
     * Use this to provide information about the version of this particular integration integration platform (i.e. 2.0.1 et al), that will be sent to Svea
     * with every service request. Should return a string. The information provided is sent as plain text and should not include any confidential information.
     */  
    public String getIntegrationVersion();
   
}
