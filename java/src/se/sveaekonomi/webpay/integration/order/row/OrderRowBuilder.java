package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;

/**
 * @author klar-sar
 * @since 2012-12-04
 */
public class OrderRowBuilder<T extends OrderBuilder<T>> implements RowBuilder {
        
    private String articleNumber;
    private String name;
    private String description;
    private double amountExVat;
    private double amountIncVat;
    private int vatPercent;
    private int quantity;
    private String unit;
    private int vatDiscount;
    private Integer discountPercent;
    
    public OrderRowBuilder() {
    }
    
    public String getArticleNumber() {
        return articleNumber;
    }
    
    public OrderRowBuilder<T> setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public OrderRowBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public OrderRowBuilder<T> setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getAmountExVat() {
        return amountExVat;
    }
    
    public OrderRowBuilder<T> setAmountExVat(double dExVatAmount) {
        this.amountExVat = dExVatAmount;
        return this;
    }
    
    public int getVatPercent() {
        return vatPercent;
    }
    
    public OrderRowBuilder<T> setVatPercent(int vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public OrderRowBuilder<T> setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
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
    public OrderRowBuilder<T> setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public int getVatDiscount() {
        return vatDiscount;
    }
    
    public OrderRowBuilder<T> setVatDiscount(int vatDiscount) {
        this.vatDiscount = vatDiscount;
        return this;
    }
    
    public Integer getDiscountPercent() {
        return discountPercent;
    }
    
    public OrderRowBuilder<T> setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }

    public double getAmountIncVat() {
        return amountIncVat;
    }

    public OrderRowBuilder<T> setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
}
