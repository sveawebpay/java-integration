package se.sveaekonomi.webpay.integration.webservice.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class WebserviceRowFormatter {
       
    private OrderBuilder<?> order;
    
    private double totalAmountExVat;
    private double totalAmountInclVat;
    private double totalVatAsAmount;
    private double totalVatAsPercent;
    
    private ArrayList<SveaOrderRow> newRows;
    
    public WebserviceRowFormatter(OrderBuilder<?>  order) {
        this.order = order;    
    }
    
    public ArrayList<SveaOrderRow> formatRows() {
        newRows = new ArrayList<SveaOrderRow>();
        
        calculateTotals();
        
        formatOrderRows();
        formatShippingFeeRows();
        formatInvoiceFeeRows();
        formatFixedDiscountRows();
        formatRelativeDiscountRows();        
        return newRows;
    }
    
    private void calculateTotals() {
        totalAmountExVat = 0;
        totalVatAsAmount = 0;
        List<OrderRowBuilder> orderRows = order.getOrderRows();
        double vatPercentAsHundredth;
        for (OrderRowBuilder existingRow : orderRows) {
            vatPercentAsHundredth = existingRow.getVatPercent() != null ? existingRow.getVatPercent() * 0.01 : 0;
 
            if (existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null) {
                this.totalAmountExVat += existingRow.getAmountExVat();
                this.totalVatAsAmount += vatPercentAsHundredth * existingRow.getAmountExVat();
            } else if (existingRow.getVatPercent() != null && existingRow.getAmountIncVat() != null) {
                this.totalAmountInclVat += existingRow.getAmountIncVat();
                this.totalVatAsAmount += (vatPercentAsHundredth / (1 + vatPercentAsHundredth)) * existingRow.getAmountIncVat();
            } else {
                this.totalAmountInclVat += existingRow.getAmountIncVat();
                this.totalAmountExVat += existingRow.getAmountExVat();
                this.totalVatAsAmount += existingRow.getAmountIncVat() - existingRow.getAmountExVat();
            }       
        }
        totalAmountInclVat = totalAmountExVat + totalVatAsAmount;
        totalAmountExVat = totalAmountInclVat - totalVatAsAmount;
        totalVatAsPercent = totalVatAsAmount / totalAmountInclVat; // e.g. 0,20 if percentage 20
    }
    
    private void formatOrderRows() {
        List<OrderRowBuilder> orderRows = order.getOrderRows();
        for (OrderRowBuilder existingRow : orderRows) {
            SveaOrderRow orderRow = new SveaOrderRow();
            Integer articleNo = existingRow.getArticleNumber();
            orderRow = serializeOrder(articleNo.toString(), existingRow.getDescription(), existingRow.getName(), existingRow.getUnit(), orderRow);          
                        
            orderRow.DiscountPercent = (!(existingRow.getDiscountPercent() == null) ? existingRow.getDiscountPercent() : 0);
            orderRow.NumberOfUnits = existingRow.getQuantity();
            
            orderRow = serializeAmountAndVat(existingRow.getAmountExVat(), existingRow.getVatPercent(), existingRow.getAmountIncVat(), orderRow);
            newRows.add(orderRow);
        }
    }
    
    private void formatShippingFeeRows() {
        if (this.order.getShippingFeeRows() == null) {
            return;
        }
        List<ShippingFeeBuilder> shippingFeeRows = order.getShippingFeeRows();
        for (ShippingFeeBuilder row : shippingFeeRows) {
            SveaOrderRow orderRow = new SveaOrderRow();
            orderRow = serializeOrder(row.getShippingId(), row.getDescription(), row.getName(), row.getUnit(), orderRow);          
                        
            orderRow.DiscountPercent = (row.getDiscountPercent() != null ? row.getDiscountPercent() : 0);
            orderRow.NumberOfUnits = 1;
            orderRow = serializeAmountAndVat(row.getAmountExVat(), row.getVatPercent(), row.getAmountIncVat(), orderRow);
            newRows.add(orderRow);
        }
    }
    
    private void formatInvoiceFeeRows() {
        if (this.order.getInvoiceFeeRows() == null) {
            return;
        }
        List<InvoiceFeeBuilder> invoiceFeeRows = order.getInvoiceFeeRows();
        for (InvoiceFeeBuilder row : invoiceFeeRows) {
            SveaOrderRow orderRow = new SveaOrderRow();     
            orderRow = serializeOrder("", row.getDescription(), row.getName(), row.getUnit(), orderRow);     
            
            orderRow.DiscountPercent = (row.getDiscountPercent() != null ? row.getDiscountPercent() : 0);
            orderRow.NumberOfUnits = 1;
            orderRow = serializeAmountAndVat(row.getAmountExVat(), row.getVatPercent(), row.getAmountIncVat(), orderRow);
           
            newRows.add(orderRow);
        }
    }
    
    private void formatFixedDiscountRows() {
        if (this.order.getFixedDiscountRows() == null) {
            return;
        }
        List<FixedDiscountBuilder> fixedDiscountRows = order.getFixedDiscountRows();
        for (FixedDiscountBuilder row : fixedDiscountRows) {
            double productTotalAfterDiscount = this.totalAmountInclVat - row.getAmount();
            double totalProductVatAsAmountAfterDiscount = this.totalVatAsPercent * productTotalAfterDiscount;
            double discountVatAsAmount = this.totalVatAsAmount - totalProductVatAsAmountAfterDiscount;
            
            SveaOrderRow orderRow = new SveaOrderRow();
            orderRow = serializeOrder(row.getDiscountId(), row.getDescription(), row.getName(), row.getUnit(), orderRow);
           
            orderRow.DiscountPercent = 0; // no discount on discount
            orderRow.NumberOfUnits = 1; // only one discount per row
            double pricePerUnitExMoms = Math.round((row.getAmount() - discountVatAsAmount) * 100.0) / 100.0;
    
            orderRow.PricePerUnit = - pricePerUnitExMoms;      
            orderRow.VatPercent = Math.round((discountVatAsAmount*100.0 / (row.getAmount() - discountVatAsAmount)*100.0)) / 100.0;
            
            newRows.add(orderRow);
        }
    }
    
    private void formatRelativeDiscountRows() {
        if (this.order.getRelativeDiscountRows() == null) {
            return;
        }
        List<RelativeDiscountBuilder> relativeDiscountRows = order.getRelativeDiscountRows();
        for (RelativeDiscountBuilder row : relativeDiscountRows) {
            SveaOrderRow orderRow = new SveaOrderRow();
            
            orderRow = serializeOrder(row.getDiscountId(), row.getDescription(), row.getName(), row.getUnit(), orderRow);
        
            orderRow.DiscountPercent = 0; // no discount on discount
            orderRow.NumberOfUnits = 1; // only one discount per row
          
            double pricePerUnitExMoms = Math.round((this.totalAmountExVat * (row.getDiscountPercent() * 0.01)) * 100.00) / 100.00;
            orderRow.PricePerUnit = - pricePerUnitExMoms;

            BigDecimal bd = new BigDecimal(Math.round((this.totalVatAsAmount*100.0) * ((row.getDiscountPercent()* 0.01)* 100.0)) / pricePerUnitExMoms / 100.0);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            orderRow.VatPercent = bd.doubleValue();
            
            newRows.add(orderRow);
        }
    }
    
    private SveaOrderRow serializeOrder(String articleNumber, String description, String name, String unit, SveaOrderRow orderRow) {
        if (articleNumber != null)
            orderRow.ArticleNumber = articleNumber;
                    
        if (description != null)
            orderRow.Description = (name != null ? name + ": " : "") + "" + description;
        else if (name != null && description == null)
            orderRow.Description = name;
        
        if (unit != null)
            orderRow.Unit = unit;
        return orderRow;
    }
    
    private SveaOrderRow serializeAmountAndVat(Double amountExVat, Double vatPercent, Double amountIncVat, SveaOrderRow orderRow) {
    	
    	if (vatPercent != null && amountExVat != null) {
            orderRow.PricePerUnit = amountExVat;
            orderRow.VatPercent = vatPercent;
        } else if (vatPercent != null && amountIncVat != null) {
            orderRow.PricePerUnit = amountIncVat / ((0.01 * vatPercent) + 1);
            orderRow.VatPercent = vatPercent;
        } else {
            orderRow.PricePerUnit = amountExVat;
            orderRow.VatPercent = ((amountIncVat / amountExVat) - 1) * 100;
        }
       /* if (vatPercent > 0 && amountExVat > 0) {
            orderRow.PricePerUnit = amountExVat;
            orderRow.VatPercent = vatPercent;
        } else if (vatPercent > 0 && amountIncVat > 0) {
            orderRow.PricePerUnit = amountIncVat / ((0.01 * vatPercent) + 1);
            orderRow.VatPercent = vatPercent;
        } else {
            orderRow.PricePerUnit = amountExVat;
            orderRow.VatPercent = ((amountIncVat / amountExVat) - 1) * 100;
        }*/
        return orderRow;       
    }
}
