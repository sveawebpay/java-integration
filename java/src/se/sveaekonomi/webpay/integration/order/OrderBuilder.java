package se.sveaekonomi.webpay.integration.order;

import java.util.ArrayList;
import java.util.List;

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
    
    protected List<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>();
    protected List<InvoiceFeeBuilder> invoiceFeeRows = new ArrayList<InvoiceFeeBuilder>();
    protected List<ShippingFeeBuilder> shippingFeeRows = new ArrayList<ShippingFeeBuilder>();
    protected List<FixedDiscountBuilder> fixedDiscountRows = new ArrayList<FixedDiscountBuilder>();
    protected List<RelativeDiscountBuilder> relativeDiscountRows = new ArrayList<RelativeDiscountBuilder>();
    
    public OrderBuilder() {
        
    }
    
    public List<OrderRowBuilder> getOrderRows() {
        return orderRows;
    }  
    
    public List<InvoiceFeeBuilder> getInvoiceFeeRows() {
        return invoiceFeeRows;
    }
    
    public void setInvoiceFeeRows(ArrayList<InvoiceFeeBuilder> invoiceFeeRows) {
        this.invoiceFeeRows = invoiceFeeRows;
    }
    
    public List<ShippingFeeBuilder> getShippingFeeRows() {
        return shippingFeeRows;
    }
    
    public void setShippingFeeRows(ArrayList<ShippingFeeBuilder> shippingFeeRows) {
        this.shippingFeeRows = shippingFeeRows;
    }
    public List<FixedDiscountBuilder> getFixedDiscountRows() {
        return fixedDiscountRows;
    }
    
    public T setFixedDiscountRows(ArrayList<FixedDiscountBuilder> fixedDiscountRows) {
        this.fixedDiscountRows = fixedDiscountRows;
        return getGenericThis();
    }
    
    public List<RelativeDiscountBuilder> getRelativeDiscountRows() {
        return relativeDiscountRows;
    }
    
    public OrderBuilder<T> setRelativeDiscountRows(ArrayList<RelativeDiscountBuilder> relativeDiscountRows) {
        this.relativeDiscountRows = relativeDiscountRows;
        return this;
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
    
    public T addOrderRow(OrderRowBuilder itemOrderRow) {
        this.orderRows.add(itemOrderRow);
        return getGenericThis();
    }

	@SuppressWarnings("unchecked")
	protected T getGenericThis() {
		return (T) this;
	}
    
    public T addOrderRows(List<OrderRowBuilder> itemOrderRow) {
        this.orderRows.addAll(itemOrderRow);
        return getGenericThis();
    }
    
    public T addDiscount(RowBuilder itemDiscount) {
        if (FixedDiscountBuilder.class.equals(itemDiscount.getClass())) 
            this.fixedDiscountRows.add((FixedDiscountBuilder) itemDiscount);
        else           
            this.relativeDiscountRows.add((RelativeDiscountBuilder)itemDiscount);
        
        return getGenericThis();
    }
    
    public T addFee(RowBuilder itemFee) {
        if (ShippingFeeBuilder.class.equals(itemFee.getClass()))
            this.shippingFeeRows.add((ShippingFeeBuilder) itemFee);
        else
            this.invoiceFeeRows.add((InvoiceFeeBuilder) itemFee);
        return getGenericThis();
    }
}
