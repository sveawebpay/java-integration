package se.sveaekonomi.webpay.integration.response.webservice;

public class CampaignCode {
	
    private String campaignCode;
    private String description;
    private String paymentPlanType;
    private String contractLengthInMonths;
    private String monthlyAnnuityFactor;
    private String initialFee;
    private String notificationFee;
    private String interestRatePercent;
    private String numberOfInterestFreeMonths;
    private String numberOfPaymentFreeMonths;
    private String fromAmount;
    private String toAmount;
    
    public String getCampaignCode() {
        return campaignCode;
    }
    
    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPaymentPlanType() {
        return paymentPlanType;
    }
    
    public void setPaymentPlanType(String paymentPlanType) {
        this.paymentPlanType = paymentPlanType;
    }
    
    public String getContractLengthInMonths() {
        return contractLengthInMonths;
    }
    
    public void setContractLengthInMonths(String contractLengthInMonths) {
        this.contractLengthInMonths = contractLengthInMonths;
    }
    
    public String getMonthlyAnnuityFactor() {
        return monthlyAnnuityFactor;
    }
    
    public void setMonthlyAnnuityFactor(String monthlyAnnuityFactor) {
        this.monthlyAnnuityFactor = monthlyAnnuityFactor;
    }
    
    public String getInitialFee() {
        return initialFee;
    }
    
    public void setInitialFee(String initialFee) {
        this.initialFee = initialFee;
    }
    
    public String getNotificationFee() {
        return notificationFee;
    }
    
    public void setNotificationFee(String notificationFee) {
        this.notificationFee = notificationFee;
    }
    
    public String getInterestRatePercent() {
        return interestRatePercent;
    }
    
    public void setInterestRatePercent(String interestRatePercent) {
        this.interestRatePercent = interestRatePercent;
    }
    
    public String getNumberOfInterestFreeMonths() {
        return numberOfInterestFreeMonths;
    }
    
    public void setNumberOfInterestFreeMonths(String numberOfInterestFreeMonths) {
        this.numberOfInterestFreeMonths = numberOfInterestFreeMonths;
    }
    
    public String getNumberOfPaymentFreeMonths() {
        return numberOfPaymentFreeMonths;
    }
    
    public void setNumberOfPaymentFreeMonths(String numberOfPaymentFreeMonths) {
        this.numberOfPaymentFreeMonths = numberOfPaymentFreeMonths;
    }
    
    public String getFromAmount() {
        return fromAmount;
    }
    
    public void setFromAmount(String fromAmount) {
        this.fromAmount = fromAmount;
    }
    
    public String getToAmount() {
        return toAmount;
    }
    
    public void setToAmount(String toAmount) {
        this.toAmount = toAmount;
    }
}
