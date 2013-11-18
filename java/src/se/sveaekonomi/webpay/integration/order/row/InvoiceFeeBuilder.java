package se.sveaekonomi.webpay.integration.order.row;

public class InvoiceFeeBuilder extends RowBuilder {

    private String name;
    private String description;
    private Double amountExVat;
    private Double amountIncVat;
    private Double vatPercent;
    private String unit;
    private double discountPercent;
    
    public String getName() {
        return name;
    }
    
    /**
     * Optional
     * @param name
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Optional
     * @param description
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public Double getAmountExVat() {
        return amountExVat;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param amountExVat
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setAmountExVat(double amountExVat) {
        this.amountExVat = amountExVat;
        return this;
    }
    
    /**
     * There can only be one invoice fee per row
     */
    public Double getQuantity() {
        return 1.0;
    }
    
    /**
     * @return (i.e. "pcs", "st" etc)
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * @param unit (i.e. "pcs", "st" etc)
     */
    public InvoiceFeeBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public Double getVatPercent() {
        return vatPercent;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param vatPercent
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public double getDiscountPercent() {
        return discountPercent;
    }
    
    /**
     * Optional
     * @param discountPercent
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
       
    public Double getAmountIncVat() {
        return amountIncVat;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param amountIncVat
     * @return InvoiceFeeBuilder
     */
    public InvoiceFeeBuilder setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
    
    /**
     * Invoice fees have no article number
     */
	public String getArticleNumber() {
		return "";
	}
}
