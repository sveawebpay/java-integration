package se.sveaekonomi.webpay.integration.order.row;


/**
 * @author klar-sar
 * @since 2012-12-06
 */
public class FixedDiscountBuilder implements RowBuilder {
    
    private String discountId;
    private String name;
    private String description;
    private int quantity;
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
    
  /*  public T endRow() {
        return (T)orderBuilder;
    }*/
}
