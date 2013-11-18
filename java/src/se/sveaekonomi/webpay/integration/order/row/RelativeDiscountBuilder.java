package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar
 */
public class RelativeDiscountBuilder extends RowBuilder {

    private String discountId;
    private String name;
    private String description;
    private double discountPercent;
    private String unit;
    
    public String getDiscountId() {
        return discountId;
    }
    
    /**
     * Optional
     * @param discountId
     * @return RelativeDiscountBuilder
     */
    public RelativeDiscountBuilder setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Optional
     * @param name
     * @return RelativeDiscountBuilder
     */
    public RelativeDiscountBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Optional
     * @param description
     * @return RelativeDiscountBuilder
     */
    public RelativeDiscountBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * There can only be one relative discount per row
     */
    public Double getQuantity() {
        return 1.0;
    }
    
    /**
     * @return unit (i.e. "pcs", "st" etc)
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * @param unit (i.e. "pcs", "st" etc)
     */
    public RelativeDiscountBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public double getDiscountPercent() {
        return discountPercent;
    }
    
    /**
     * Required
     * @param discountPercent
     * @return RelativeDiscountBuilder
     */
    public RelativeDiscountBuilder setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }

    /**
     * For relative discounts the article number is synonymous with the discount id
     */
	public String getArticleNumber() {
        return getDiscountId();
	}

    /**
     * Do not use.
     * Will return null.
     * @return null
     */
	public Double getAmountExVat() {
		return null;
	}

    /**
     * Do not use.
     * Will return null.
     * @return null
     */
	public Double getVatPercent() {
		return null;
	}

    /**
     * Do not use.
     * Will return null.
     * @return null
     */
	public Double getAmountIncVat() {
		return null;
	}
}
