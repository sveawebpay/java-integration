package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

/**
 * @author klar-sar
 * @since 2012-12-05
 */
public class ShippingFeeBuilder<T extends OrderBuilder<T>> implements RowBuilder {
    
    //private OrderBuilder<T> orderBuilder;
    private String shippingId;
    private String name;
    private String description;
    private double amountExVat;
    private double amountIncVat;
    private int vatPercent;
    private int quantity;
    private String unit;
    private Integer discountPercent;
    
    public ShippingFeeBuilder(/*OrderBuilder<T> orderBuilder*/) {
        //this.orderBuilder = orderBuilder;
    }
    
    public String getShippingId() {
        return shippingId;
    }
    
    public ShippingFeeBuilder<T> setShippingId(String id) {
        this.shippingId = id;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public ShippingFeeBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ShippingFeeBuilder<T> setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getAmountExVat() {
        return amountExVat;
    }
    
    public ShippingFeeBuilder<T> setAmountExVat(double amountExVat) {
        this.amountExVat = amountExVat;
        return this;
    }
    
    public int getVatPercent() {
        return vatPercent;
    }
    
    public ShippingFeeBuilder<T> setVatPercent(int vatPercent) {
        this.vatPercent = vatPercent;
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
     * @param unit
     *            (i.e. "pcs", "st" etc)
     */
    public ShippingFeeBuilder<T> setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public Integer getDiscountPercent() {
        return discountPercent;
    }
    
    public ShippingFeeBuilder<T> setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
     
  /*  public T endRow() {
        return (T)orderBuilder;
    }*/

    public double getAmountIncVat() {
        return amountIncVat;
    }

    public ShippingFeeBuilder<T> setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
}
