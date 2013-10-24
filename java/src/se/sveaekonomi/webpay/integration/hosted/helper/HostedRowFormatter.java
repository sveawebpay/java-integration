package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;

public class HostedRowFormatter {

    private Long totalAmount;
    private Long totalVat;
    private ArrayList<HostedOrderRowBuilder> newRows;
    
    public HostedRowFormatter() {
        totalAmount = 0L;
        totalVat = 0L;
        newRows = new ArrayList<HostedOrderRowBuilder>();
    }
    
    public ArrayList<HostedOrderRowBuilder> formatRows(CreateOrderBuilder order) {
        formatOrderRows(order);
        formatShippingFeeRows(order);
        formatFixedDiscountRows(order);
        formatRelativeDiscountRows(order);
        
        return newRows;
    }

    private <T extends OrderBuilder<T>> void formatOrderRows(OrderBuilder<T> orderBuilder) {
        for (OrderRowBuilder row : orderBuilder.getOrderRows()) {
            HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();
            
            double vatFactor = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;
            
            if (row.getName() != null) {
                tempRow.setName(row.getName());
            }
            
            if (row.getDescription() != null) {
                tempRow.setDescription(row.getDescription());
            }
            
            double amountExVat =  row.getAmountExVat()!= null ? row.getAmountExVat() : 0;
            tempRow.setAmount((long)((amountExVat * 100) * vatFactor));
            
            if (row.getAmountExVat() != null && row.getVatPercent() != null) {
                tempRow.setAmount((long)((row.getAmountExVat() *100) * vatFactor));
                tempRow.setVat((long)(tempRow.getAmount() - (row.getAmountExVat() * 100)));
            } else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
                tempRow.setAmount((long)((row.getAmountIncVat() * 100)));
                tempRow.setVat((long)(tempRow.getAmount() - (tempRow.getAmount() / vatFactor)));
            } else {
                tempRow.setAmount((long)(row.getAmountIncVat() * 100));
                tempRow.setVat((long)((row.getAmountIncVat() - row.getAmountExVat()) * 100));
            }
            
            tempRow.setQuantity((tempRow.getQuantity() == null ? 0.0 : tempRow.getQuantity()));
            if (tempRow.getQuantity() >= 0) {
                tempRow.setQuantity(row.getQuantity());
            }
            
            if (row.getUnit() != null) {
                tempRow.setUnit(row.getUnit());
            }
            
            if (null != row.getArticleNumber()) {
                tempRow.setSku(row.getArticleNumber());
            }
            
            newRows.add(tempRow);
            totalAmount += (long)(tempRow.getAmount() * row.getQuantity());
            totalVat += (long)(tempRow.getVat() * row.getQuantity());
        }
    }
    
    private <T extends OrderBuilder<T>> void formatShippingFeeRows(OrderBuilder<T> orderBuilder) {
        if (orderBuilder.getShippingFeeRows() == null) {
            return;
        }
        
        for (ShippingFeeBuilder row : orderBuilder.getShippingFeeRows()) {
            HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();
            double plusVatCounter = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;
            
            if (row.getName() != null) {
                tempRow.setName(row.getName());
            }
            
            if (row.getDescription() != null) {
                tempRow.setDescription(row.getDescription());
            }
            
            if (row.getAmountExVat() != null && row.getVatPercent() != null) {
                tempRow.setAmount((long)((row.getAmountExVat() * 100) * plusVatCounter));
                tempRow.setVat((long)(tempRow.getAmount() - (row.getAmountExVat() * 100)));
            } else if (row.getAmountIncVat() != null && row.getVatPercent() != null ) {
                tempRow.setAmount((long)(row.getAmountIncVat() * 100));
                tempRow.setVat((long)(tempRow.getAmount() - (tempRow.getAmount() / plusVatCounter)));
            } else {
                Double amountIncVat = row.getAmountIncVat()!= null ? row.getAmountIncVat() : 0;
                tempRow.setAmount((long)(amountIncVat * 100));
                double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
                tempRow.setVat((long)(amountIncVat - amountExVat));
            }
            
            tempRow.setQuantity(1.0);
            
            if (row.getUnit() != null) {
                tempRow.setUnit(row.getUnit());
            }
            
            if (row.getShippingId() != null) {
                tempRow.setSku(row.getShippingId());
            }
            
            newRows.add(tempRow);
        }
    }
    
    private void formatFixedDiscountRows(CreateOrderBuilder orderBuilder) {
        if (orderBuilder.getFixedDiscountRows() == null) {
            return;
        }
        
        for (FixedDiscountBuilder row : orderBuilder.getFixedDiscountRows()) {
            HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();
            
            if (row.getName() != null) {
                tempRow.setName(row.getName());
            }
            
            if (row.getDescription() != null) {
                tempRow.setDescription(row.getDescription());
            }
            
            tempRow.setAmount((long)(-(row.getAmount() * 100)));
            
            totalAmount -= (long)row.getAmount();
            
            double discountFactor = tempRow.getAmount() * 1.0 / totalAmount;
            
            if (totalVat > 0) {
                tempRow.setVat((long)(totalVat * discountFactor));
            }
            
            tempRow.setQuantity(1.0);
            
            if (row.getUnit() != null) {
                tempRow.setUnit(row.getUnit());
            }
            
            if (row.getDiscountId() != null) {
                tempRow.setSku(row.getDiscountId());
            }
            
            newRows.add(tempRow);
        }
    }
    
    private void formatRelativeDiscountRows(CreateOrderBuilder orderBuilder) {
        if (orderBuilder.getRelativeDiscountRows() == null) {
            return;
        }
        
        for (RelativeDiscountBuilder row : orderBuilder.getRelativeDiscountRows()) {
            HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();
            double discountFactor = (row.getDiscountPercent() == null ? 0.0 : row.getDiscountPercent()) * 0.01;
            
            if (row.getName() != null) {
                tempRow.setName(row.getName());
            }
            
            if (row.getDescription() != null) {
                tempRow.setDescription(row.getDescription());
            }
            
            if (row.getDiscountId() != null) {
                tempRow.setSku(row.getDiscountId());
            }
            
            tempRow.setQuantity(1.0);
            
            if (row.getUnit() != null) {
                tempRow.setUnit(row.getUnit());
            }
            
            tempRow.setAmount((long)(-(discountFactor * totalAmount)));
            totalAmount -= tempRow.getAmount();
            
            if (totalVat > 0) {
                tempRow.setVat((long)(-(totalVat * discountFactor)));
            }
            
            newRows.add(tempRow);
        }
    }
    
    public Long formatTotalAmount(ArrayList<HostedOrderRowBuilder> rows) {
        Long amount = 0L;
        
        for (HostedOrderRowBuilder row : rows) {
            amount += (long)(row.getAmount() * (row.getQuantity() == null ? 0.0 : row.getQuantity()) );
        }
        
        return amount;
    }
    
    public Long formatTotalVat(ArrayList<HostedOrderRowBuilder> rows) {
        Long vat = 0L;
        
        for (HostedOrderRowBuilder row : rows) {
            vat += (long)(row.getVat() * (row.getQuantity() == null ? 0.0 : row.getQuantity()));
        }
        
        return vat;
    }
}
