package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar
 * @since 2012-12-05
 */
public class ShippingFeeBuilder implements RowBuilder {
    
    private String shippingId;
    private String name;
    private String description;
    private Double amountExVat;
    private Double amountIncVat;
    private Double vatPercent;
    private Double quantity;
    private String unit;
    private Double discountPercent;
    
    public String getShippingId() {
        return shippingId;
    }
    
    /**
     * Optional
     * @param id
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setShippingId(String id) {
        this.shippingId = id;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Optional
     * @param name
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Optional
     * @param description
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public Double getAmountExVat() {
        return amountExVat;
    }
    
    /**
     * Optional 
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param amountExVat
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setAmountExVat(double amountExVat) {
        this.amountExVat = amountExVat;
        return this;
    }
    
    public Double getVatPercent() {
        return vatPercent;
    }
    
    /**
     * Optional
     * Required to use at least two of the methods setAmountExVat(), setAmountIncVat() or setVatPercent()
     * @param vatPercent
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public Double getQuantity() {
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
    public ShippingFeeBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public Double getDiscountPercent() {
        return discountPercent;
    }
    
    /**
     * Optional
     * @param discountPercent
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setDiscountPercent(Double discountPercent) {
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
     * @return ShippingFeeBuilder
     */
    public ShippingFeeBuilder setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
}
