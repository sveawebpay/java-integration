package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

public class InvoiceFeeBuilder <T extends OrderBuilder<T>> implements RowBuilder {

    //private OrderBuilder<T> orderBuilder;
    private String name;
    private String description;
    private double amountExVat;
    private double amountIncVat;
    private int vatPercent;
    private int quantity;
    private String unit;
    private Integer discountPercent;
    
    public InvoiceFeeBuilder(/*T orderBuilder*/) {
        //this.orderBuilder = orderBuilder;
    }
    
    public String getName() {
        return name;
    }
    
    public InvoiceFeeBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public InvoiceFeeBuilder<T> setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getAmountExVat() {
        return amountExVat;
    }
    
    public InvoiceFeeBuilder<T> setAmountExVat(int amountExVat) {
        this.amountExVat = amountExVat;
        return this;
    }
    
    public int getQuantity() {
        return quantity;
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
    public InvoiceFeeBuilder<T> setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public int getVatPercent() {
        return vatPercent;
    }
    
    public InvoiceFeeBuilder<T> setVatPercent(int vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public Integer getDiscountPercent() {
        return discountPercent;
    }
    
    public InvoiceFeeBuilder<T> setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
       
    public double getAmountIncVat() {
        return amountIncVat;
    }

    public InvoiceFeeBuilder<T> setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
    
   /* public T endRow() {
        return (T)orderBuilder;
    }*/
}
