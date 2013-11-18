package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class SveaCreateOrderInformation {

    public String ClientOrderNumber;
    public SveaCustomerIdentity CustomerIdentity;
    public String AddressSelector;
    public String OrderType;
    public ArrayList<SveaOrderRow> OrderRows; 
    public Map<String, Object> CreatePaymentPlanDetails;
    public String CustomerReference;
    public Date OrderDate;
    
    public SveaCreateOrderInformation() {
        this("", false);
    }
    
    public SveaCreateOrderInformation(String campaignCode, Boolean sendAutomaticGiroPaymentForm) {
        this.OrderRows = new ArrayList<SveaOrderRow> (); 
        this.CreatePaymentPlanDetails = new HashMap<String, Object>();
        
        if (!campaignCode.equals("")) {
            this.CreatePaymentPlanDetails.put("CampaignCode", campaignCode);
            this.CreatePaymentPlanDetails.put("SendAutomaticGiroPaymentForm", sendAutomaticGiroPaymentForm);
        }
    }
    
    public void addOrderRow(SveaOrderRow orderRow) {
        OrderRows.add(orderRow);
    }
    
    public Object getPaymentPlanDetails(String key) {
        if (key.equals("CampaignCode")) {
            return (String)this.CreatePaymentPlanDetails.get(key);
        } else {
            return (Boolean)this.CreatePaymentPlanDetails.get(key);
        }
    }
}
