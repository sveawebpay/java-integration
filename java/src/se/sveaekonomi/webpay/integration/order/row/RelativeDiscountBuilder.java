package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

/**
 * @author klar-sar
 */
public class RelativeDiscountBuilder<T extends OrderBuilder<T>> implements RowBuilder {
    
//    private OrderBuilder<T> orderBuilder;
    private String discountId;
    private String name;
    private String description;
    private int discountPercent;
    private int quantity;
    private String unit;
    
    public RelativeDiscountBuilder(/*OrderBuilder<T> order*/) {
      //  orderBuilder = order;
    }
    
    public String getDiscountId() {
        return discountId;
    }
    
    public RelativeDiscountBuilder<T> setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public RelativeDiscountBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public RelativeDiscountBuilder<T> setDescription(String description) {
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
    public RelativeDiscountBuilder<T> setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public int getDiscountPercent() {
        return discountPercent;
    }
    
    public RelativeDiscountBuilder<T> setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
    
  /*  public T endRow() {
        return (T)orderBuilder;
    }*/
}
