package se.sveaekonomi.webpay.integration.order.row;


public class InvoiceFeeBuilder implements RowBuilder {

    private String name;
    private String description;
    private double amountExVat;
    private double amountIncVat;
    private int vatPercent;
    private int quantity;
    private String unit;
    private Integer discountPercent;
    
    public String getName() {
        return name;
    }
    
    public InvoiceFeeBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public InvoiceFeeBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public double getAmountExVat() {
        return amountExVat;
    }
    
    public InvoiceFeeBuilder setAmountExVat(int amountExVat) {
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
    public InvoiceFeeBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
    
    public int getVatPercent() {
        return vatPercent;
    }
    
    public InvoiceFeeBuilder setVatPercent(int vatPercent) {
        this.vatPercent = vatPercent;
        return this;
    }
    
    public Integer getDiscountPercent() {
        return discountPercent;
    }
    
    public InvoiceFeeBuilder setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }
       
    public double getAmountIncVat() {
        return amountIncVat;
    }

    public InvoiceFeeBuilder setAmountIncVat(double amountIncVat) {
        this.amountIncVat = amountIncVat;
        return this;
    }
    
   /* public T endRow() {
        return (T)orderBuilder;
    }*/
}
