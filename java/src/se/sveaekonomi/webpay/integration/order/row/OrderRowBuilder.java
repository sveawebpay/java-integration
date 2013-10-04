package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar
 * @since 2012-12-04
 */
public class OrderRowBuilder implements RowBuilder {

    private String articleNumber;
    private String name;
    private String description;
    private Double amountExVat;
    private Double amountIncVat;
    private Double vatPercent;
    private int quantity;
    private String unit;
    private int vatDiscount;
    private Integer discountPercent;
    
    public OrderRowBuilder() {
        
    }
    
    public String getArticleNumber() {
        return articleNumber;
    }
    
    /**
     * Optional
     * @param articleNumber
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Optional
     * @param name
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Optional
     * @param description
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public Double getAmountExVat() {
        return amountExVat;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param dExVatAmount
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setAmountExVat(double dExVatAmount) {
        this.amountExVat = dExVatAmount;
        return this;
    }
    
    public Double getVatPercent() {
        return vatPercent;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param vatPercent
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Required
     * @param quantity
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setQuantity(int quantity) {
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
    public OrderRowBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public int getVatDiscount() {
        return vatDiscount;
    }
    
    /**
     * Optional
     * @param vatDiscount
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setVatDiscount(int vatDiscount) {
        this.vatDiscount = vatDiscount;
        return this;
    }
    
    public Integer getDiscountPercent() {
        return discountPercent;
    }
    
    /**
     * Optional
     * @param discountPercent
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
    
    public Double getAmountIncVat() {
        return amountIncVat;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param amountIncVat
     * @return OrderRowBuilder
     */
    public OrderRowBuilder setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
}
