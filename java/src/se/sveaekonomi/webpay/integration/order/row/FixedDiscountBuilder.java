package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar
 * @since 2012-12-06
 */
public class FixedDiscountBuilder extends RowBuilder {
    
    private String discountId;
    private String name;
    private String description;
    private String unit = "";
    private double amount;
    
    public String getDiscountId() {
        return discountId;
    }
    
    /**
     * Optional
     * @param discountId
     * @return FixedDiscountBuilder
     */
    public FixedDiscountBuilder setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Optional
     * @param name
     * @return FixedDiscountBuilder
     */
    public FixedDiscountBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Optional
     * @param description
     * @return FixedDiscountBuilder
     */
    public FixedDiscountBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * There can only be one fixed discount per row
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
     * Optional
     * @param unit of (i.e. "pcs", "st" etc)
     */
    public FixedDiscountBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public double getAmount() {
        return amount;
    }
    
    /**
     * Required
     * @param amountDisountOnTotalPrice
     * @return FixedDiscountBuilder
     */
    public FixedDiscountBuilder setAmountIncVat(double amountDisountOnTotalPrice) {
        this.amount = amountDisountOnTotalPrice;
        return this;
    }
    
    /**
     * For fixed discounts the article number is synonomous with the discount id
     */
	public String getArticleNumber() {
		return getDiscountId();
	}

    /**
     * We do not give discounts on discounts
     */
	public double getDiscountPercent() {
		return 0;
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
