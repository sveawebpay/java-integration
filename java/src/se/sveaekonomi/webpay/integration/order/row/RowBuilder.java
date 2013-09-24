package se.sveaekonomi.webpay.integration.order.row;

public interface RowBuilder {

    public String getName();
    public String getDescription();
    public int getQuantity();
    public String getUnit();
}
