package se.sveaekonomi.webpay.integration.order.row;

public abstract class RowBuilder {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUnit();
    
	public abstract String getArticleNumber();
	public abstract double getDiscountPercent();
	public abstract Double getQuantity();
	
	public abstract Double getAmountExVat();
	public abstract  Double getVatPercent();
	public abstract Double getAmountIncVat();
}
