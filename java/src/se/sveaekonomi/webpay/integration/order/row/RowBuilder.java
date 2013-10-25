package se.sveaekonomi.webpay.integration.order.row;

public interface RowBuilder {

    public String getName();
    public String getDescription();
    public Double getQuantity();
    public String getUnit();
}
