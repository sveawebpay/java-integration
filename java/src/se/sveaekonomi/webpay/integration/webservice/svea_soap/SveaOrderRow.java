package se.sveaekonomi.webpay.integration.webservice.svea_soap;

public class SveaOrderRow {
    
    public String ArticleNumber = "";
    public String Description = "";
    public double PricePerUnit = 0;
    public double NumberOfUnits = 0;
    public String Unit = "";
    public double VatPercent = 0;
    public double DiscountPercent = 0;
	public Boolean PriceIncludingVat;
}
