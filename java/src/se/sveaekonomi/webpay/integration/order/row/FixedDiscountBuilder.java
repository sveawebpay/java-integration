package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

/**
 * @author klar-sar
 * @since 2012-12-06
 */
public class FixedDiscountBuilder implements RowBuilder {
    
    private String discountId;
    private String name;
    private String description;
    private double discount;
    private int quantity;
    private String unit;
    private double amount;
    
    public String getDiscountId() {
        return discountId;
    }
    
    public FixedDiscountBuilder setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public FixedDiscountBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public FixedDiscountBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public FixedDiscountBuilder setDiscount(double discount) {
        this.discount = discount;
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
     * @param unit of (i.e. "pcs", "st" etc)
     */
    public FixedDiscountBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public FixedDiscountBuilder setAmountIncVat(double amountDisountOnTotalPrice) {
        this.amount = amountDisountOnTotalPrice;
        return this;
    }
    
  /*  public T endRow() {
        return (T)orderBuilder;
    }*/
}
