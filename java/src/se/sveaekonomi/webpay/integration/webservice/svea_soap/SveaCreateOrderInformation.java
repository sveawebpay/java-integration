package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import java.util.ArrayList;
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
    public String OrderDate;
    
    public SveaCreateOrderInformation(String campaignCode, Boolean sendAutomaticGiroPaymentForm) {
        this.OrderRows = new ArrayList<SveaOrderRow> (); 
        this.CreatePaymentPlanDetails = new HashMap<String, Object>();
        if(campaignCode != ""){
            this.CreatePaymentPlanDetails.put("CampaignCode", campaignCode);
            this.CreatePaymentPlanDetails.put("SendAutomaticGiroPaymentForm", sendAutomaticGiroPaymentForm);
        }
    }
    
    public SveaCreateOrderInformation() {
        this("", false);
    }
    
    public void addOrderRow(SveaOrderRow orderRow) {
        OrderRows.add(orderRow);
    }
    
    public Object getPaymentPlanDetails(String key) {
        if(key=="CampaignCode")
            return (String)this.CreatePaymentPlanDetails.get(key);
        else
            return (Boolean)this.CreatePaymentPlanDetails.get(key);       
    }
}
