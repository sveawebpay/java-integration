package se.sveaekonomi.webpay.integration.order;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;

/**
 * @author klar-sar
 * @since 2012-12-03
 */
public abstract class OrderBuilder<T extends OrderBuilder<T>> {
    
    protected ArrayList<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>();
    protected ArrayList<InvoiceFeeBuilder> invoiceFeeRows = new ArrayList<InvoiceFeeBuilder>();
    protected ArrayList<ShippingFeeBuilder> shippingFeeRows = new ArrayList<ShippingFeeBuilder>();
    protected ArrayList<FixedDiscountBuilder> fixedDiscountRows = new ArrayList<FixedDiscountBuilder>();
    protected ArrayList<RelativeDiscountBuilder> relativeDiscountRows = new ArrayList<RelativeDiscountBuilder>();
    
    private boolean testmode;

    public OrderBuilder() {
        
    }
    
    public ArrayList<OrderRowBuilder> getOrderRows() {
        return orderRows;
    }  
    
    public ArrayList<InvoiceFeeBuilder> getInvoiceFeeRows() {
        return invoiceFeeRows;
    }
    
    public void setInvoiceFeeRows(ArrayList<InvoiceFeeBuilder> invoiceFeeRows) {
        this.invoiceFeeRows = invoiceFeeRows;
    }
    
    public ArrayList<ShippingFeeBuilder> getShippingFeeRows() {
        return shippingFeeRows;
    }
    
    public void setShippingFeeRows(ArrayList<ShippingFeeBuilder> shippingFeeRows) {
        this.shippingFeeRows = shippingFeeRows;
    }
    public ArrayList<FixedDiscountBuilder> getFixedDiscountRows() {
        return fixedDiscountRows;
    }
    
    public OrderBuilder setFixedDiscountRows(ArrayList<FixedDiscountBuilder> fixedDiscountRows) {
        this.fixedDiscountRows = fixedDiscountRows;
        return this;
    }
    
    public ArrayList<RelativeDiscountBuilder> getRelativeDiscountRows() {
        return relativeDiscountRows;
    }
    
    public OrderBuilder setRelativeDiscountRows(ArrayList<RelativeDiscountBuilder> relativeDiscountRows) {
        this.relativeDiscountRows = relativeDiscountRows;
        return this;
    }
    
    public boolean getTestmode() {
        return testmode;
    }
    
    public T setTestmode() {
        this.testmode = true;
        return (T)this;
    }
    
  /*  public OrderRowBuilder<T> beginOrderRow() {
        OrderRowBuilder<T> rowBuilder = new OrderRowBuilder(this);
        orderRows.add(rowBuilder);
        return rowBuilder;
    }
    
    public InvoiceFeeBuilder<T> beginInvoiceFee() {
        InvoiceFeeBuilder<T> invoiceFee = new InvoiceFeeBuilder(this);
        invoiceFeeRows.add(invoiceFee);
        return invoiceFee;
    }
    
    public ShippingFeeBuilder<T> beginShippingFee() {
        ShippingFeeBuilder<T> shipping = new ShippingFeeBuilder(this);
        shippingFeeRows.add(shipping);
        return shipping;
    }
    
    public FixedDiscountBuilder<T> beginFixedDiscount() {
        FixedDiscountBuilder<T> fixedDiscount = new FixedDiscountBuilder(this);
        fixedDiscountRows.add(fixedDiscount);
        return fixedDiscount;
    }
    
    public RelativeDiscountBuilder<T> beginRelativeDiscount() {
        RelativeDiscountBuilder<T> relativeDisc = new RelativeDiscountBuilder(this);
        relativeDiscountRows.add(relativeDisc);
        return relativeDisc;
    }*/
    
    public T run(BuilderCommand<T> runner) {
        return runner.run(this);
    }
    
    public OrderBuilder addOrderRow(OrderRowBuilder itemOrderRow) {
        this.orderRows.add(itemOrderRow);
        return this;
    }
    
    public OrderBuilder addOrderRows(ArrayList<OrderRowBuilder> itemOrderRow) {
        this.orderRows.addAll(itemOrderRow);
        return this;
    }
    
    public OrderBuilder addDiscount(RowBuilder itemDiscount) {
        if (FixedDiscountBuilder.class.equals(itemDiscount.getClass())) 
            this.fixedDiscountRows.add((FixedDiscountBuilder) itemDiscount);
        else           
            this.relativeDiscountRows.add((RelativeDiscountBuilder)itemDiscount);
        
        return this;
    }
    
    public OrderBuilder addFee(RowBuilder itemFee) {
        if (ShippingFeeBuilder.class.equals(itemFee.getClass()))
            this.shippingFeeRows.add((ShippingFeeBuilder) itemFee);
        else
            this.invoiceFeeRows.add((InvoiceFeeBuilder) itemFee);
        return this;
    }
}
