package se.sveaekonomi.webpay.integration.response.webservice;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;

public class PaymentPlanParamsResponse extends Response {
    
    private List<CampaignCode> campaignCodes;
    
    public PaymentPlanParamsResponse(NodeList soapMessage) {
        super();
        campaignCodes = new ArrayList<CampaignCode>();
        setValues(soapMessage);
    }
    
    public List<CampaignCode> getCampaignCodes() {
        return campaignCodes;
    }
    
    public void setCampaignCodes(List<CampaignCode> campaignCodes) {
        this.campaignCodes = campaignCodes;
    }
    
    private void setValues(NodeList soapMessage) {
        try {
            int size = soapMessage.getLength();
            
            for (int i = 0; i < size; i++) {
                Element node = (Element)soapMessage.item(i);
                
                // mandatory
                this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
                
                this.setResultCode(getTagValue(node, "ResultCode"));
                
                if(this.isOrderAccepted()) {
	                NodeList campaigns = node.getElementsByTagName("CampaignCodeInfo");
	                int numberOfCampaigns = campaigns.getLength();
	                
	                for (int j = 0; j < numberOfCampaigns; j++) {
	                    Element campaignNode = (Element)campaigns.item(j);
	                    
	                    CampaignCode campaign = new CampaignCode();
	                    campaign.setCampaignCode(getTagValue(campaignNode, "CampaignCode"));
	                    campaign.setDescription(getTagValue(campaignNode, "Description"));
	                    campaign.setPaymentPlanType(getTagValue(campaignNode, "PaymentPlanType"));
	                    campaign.setContractLengthInMonths(getTagValue(campaignNode, "ContractLengthInMonths"));
	                    campaign.setMonthlyAnnuityFactor(getTagValue(campaignNode, "MonthlyAnnuityFactor"));
	                    campaign.setInitialFee(getTagValue(campaignNode, "InitialFee"));
	                    campaign.setNotificationFee(getTagValue(campaignNode, "NotificationFee"));
	                    campaign.setInterestRatePercent(getTagValue(campaignNode, "InterestRatePercent"));
	                    campaign.setNumberOfInterestFreeMonths(getTagValue(campaignNode, "NumberOfInterestFreeMonths"));
	                    campaign.setNumberOfPaymentFreeMonths(getTagValue(campaignNode, "NumberOfPaymentFreeMonths"));
	                    campaign.setFromAmount(getTagValue(campaignNode, "FromAmount"));
	                    campaign.setToAmount(getTagValue(campaignNode, "ToAmount"));
	                    campaignCodes.add(campaign);
	                }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return ((Node) textList.item(0)).getNodeValue().trim();
        }
        
        return null;
    }
}
