package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

/**
 * @author klar-sar
 * @since 2012-12-06
 */
public class FixedDiscountBuilder<T extends OrderBuilder<T>> implements RowBuilder {
    
 //   private OrderBuilder<T> orderBuilder;
    private String discountId;
    private String name;
    private String description;
    private double discount;
    private int quantity;
    private String unit;
    private double amount;
    
    public FixedDiscountBuilder(/*T orderBuilder*/) {
   //     this.orderBuilder = orderBuilder;
    }
  
    public String getDiscountId() {
        return discountId;
    }
    
    public FixedDiscountBuilder<T> setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public FixedDiscountBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public FixedDiscountBuilder<T> setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public FixedDiscountBuilder<T> setDiscount(double discount) {
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
    public FixedDiscountBuilder<T> setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public FixedDiscountBuilder<T> setAmountIncVat(double amountDisountOnTotalPrice) {
        this.amount = amountDisountOnTotalPrice;
        return this;
    }
    
  /*  public T endRow() {
        return (T)orderBuilder;
    }*/
}
