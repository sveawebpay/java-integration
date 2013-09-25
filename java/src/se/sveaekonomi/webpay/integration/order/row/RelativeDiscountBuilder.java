package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar
 */
public class RelativeDiscountBuilder implements RowBuilder {

    private String discountId;
    private String name;
    private String description;
    private int discountPercent;
    private int quantity;
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
    
    public int getQuantity() {
        return quantity;
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
    
    public int getDiscountPercent() {
        return discountPercent;
    }
    
    /**
     * Required
     * @param discountPercent
     * @return RelativeDiscountBuilder
     */
    public RelativeDiscountBuilder setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
}
