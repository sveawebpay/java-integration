package se.sveaekonomi.webpay.integration.webservice.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class WebserviceRowFormatter {

	private OrderBuilder<?> order;

	private double totalAmountExVat;
	private double totalAmountIncVat;
	private double totalVatAsAmount;
	
	private LinkedHashMap<Double,Double> totalAmountPerVatRateIncVat;

	private ArrayList<SveaOrderRow> newRows;

	public WebserviceRowFormatter(OrderBuilder<?> order) {
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
		totalAmountIncVat = 0;
		totalAmountExVat = 0;
		totalVatAsAmount = 0;
		
		totalAmountPerVatRateIncVat = new LinkedHashMap<Double, Double>();
		
		List<OrderRowBuilder> orderRows = order.getOrderRows();
		
		double vatPercent;
		double vatPercentAsHundredth;
		
		double amountExVat;
		double amountIncVat;
		double quantity;

		for (OrderRowBuilder existingRow : orderRows) {
			
			vatPercent = existingRow.getVatPercent() != null ? existingRow.getVatPercent() : 0;
			vatPercentAsHundredth = vatPercent * 0.01;
			
			amountExVat = existingRow.getAmountExVat() != null ? existingRow.getAmountExVat() : 0;
			amountIncVat = existingRow.getAmountIncVat() != null ? existingRow.getAmountIncVat() : 0;
			quantity = existingRow.getQuantity() != null ? existingRow.getQuantity() : 0;
			
			if (existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null) {

                totalAmountExVat += amountExVat * quantity;
                totalVatAsAmount += vatPercentAsHundredth * amountExVat * quantity;
                totalAmountIncVat += (amountExVat + (vatPercentAsHundredth * amountExVat)) * quantity;

                if (totalAmountPerVatRateIncVat.containsKey(vatPercent)) {
                	totalAmountPerVatRateIncVat.put(vatPercent, 
                									(amountExVat * quantity * (1 + vatPercentAsHundredth)) + 
                									totalAmountPerVatRateIncVat.get(vatPercent)
            									);
                }
                else {
                    totalAmountPerVatRateIncVat.put(vatPercent, amountExVat * quantity * (1 + vatPercentAsHundredth));
                }
			}
			else if (existingRow.getVatPercent() != null && existingRow.getAmountIncVat() != null) {
                totalAmountIncVat += amountIncVat * quantity;
                totalVatAsAmount += (vatPercentAsHundredth / (1 + vatPercentAsHundredth)) * amountIncVat * quantity;
                totalAmountExVat += (amountIncVat - ((vatPercentAsHundredth / (1 + vatPercentAsHundredth)) * amountIncVat)) * quantity;

                if (totalAmountPerVatRateIncVat.containsKey(vatPercent)) {
                	totalAmountPerVatRateIncVat.put(vatPercent, (amountIncVat * quantity));
                }
                else {
                    totalAmountPerVatRateIncVat.put(vatPercent, amountIncVat * quantity);
                }
			}
			else {
                totalAmountIncVat += amountIncVat * quantity;
                totalAmountExVat += amountExVat * quantity;
                totalVatAsAmount += (amountIncVat - amountExVat) * quantity;

                double vatRate = (amountIncVat == 0.0 || amountExVat == 0.0) ? 0 : 
                    ((amountIncVat / amountExVat) - 1) * 100;

                if (totalAmountPerVatRateIncVat.containsKey(vatRate)) {
                	totalAmountPerVatRateIncVat.put(vatPercent, (amountExVat * quantity * (1 + vatRate / 100)));
                }
                else {
                    totalAmountPerVatRateIncVat.put(vatRate, amountExVat * quantity * (1 + vatRate / 100));
                }
			}
		}
	}
	
	private <T extends RowBuilder> void formatRowLists(List<T> rows) {
		for (RowBuilder existingRow : rows) {

			// if fixedDiscount row, calculate vat, split over several rows if needed.
			if (FixedDiscountBuilder.class.equals(existingRow.getClass())) {
				
				// incvat set only
				if (existingRow.getAmountIncVat() != null && existingRow.getVatPercent() == null && existingRow.getAmountExVat() == null) {

                	for (double vatRate : totalAmountPerVatRateIncVat.keySet()) {
                    	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                        double amountAtThisVatRateIncVat = totalAmountPerVatRateIncVat.get(vatRate);

                        if (totalAmountPerVatRateIncVat.size() > 1) {
                            String name = existingRow.getName();
                            String description = existingRow.getDescription();

                            orderRow.Description = formatDiscountRowDescription(name, description, (long) vatRate);
                        }

                        double discountAtThisVatRateIncVat = existingRow.getAmountIncVat() * (amountAtThisVatRateIncVat / totalAmountIncVat);
                        double discountAtThisVatRateExVat = discountAtThisVatRateIncVat - discountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);

                        orderRow.PricePerUnit = -MathUtil.bankersRound(discountAtThisVatRateExVat);
                        orderRow.VatPercent = vatRate;

                        newRows.add(orderRow);
                    }
                }
				
				// incvat, vatpercent set
                else if (existingRow.getAmountIncVat() != null && existingRow.getVatPercent() != null && existingRow.getAmountExVat() == null)
                {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                	double vatRate = existingRow.getVatPercent();
                	double discountAtThisVatRateIncVat = existingRow.getAmountIncVat();
                	double discountAtThisVatRateExVat = discountAtThisVatRateIncVat - discountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);

                    orderRow.PricePerUnit = -MathUtil.bankersRound(discountAtThisVatRateExVat);
                    orderRow.VatPercent = vatRate;

                    newRows.add(orderRow);
                }
				
				// exvat, vatpercent set
                else if (existingRow.getAmountIncVat() == null && existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null)
                {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                    orderRow.PricePerUnit = -MathUtil.bankersRound(existingRow.getAmountExVat());
                    orderRow.VatPercent = existingRow.getVatPercent();

                    newRows.add(orderRow);
                }
			}
			
			// if relativeDiscount row, calculate vat, split over several rows if needed.
			else if (RelativeDiscountBuilder.class.equals(existingRow.getClass())) {
				for (double vatRate : totalAmountPerVatRateIncVat.keySet()) {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                    double amountAtThisVatRateIncVat = totalAmountPerVatRateIncVat.get(vatRate);

                    if (totalAmountPerVatRateIncVat.size() > 1)
                    {
                        String name = existingRow.getName();
                        String description = existingRow.getDescription();

                        orderRow.Description = formatDiscountRowDescription(name, description, (long) vatRate);
                    }

                    double amountAtThisVatRateExVat = amountAtThisVatRateIncVat - amountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);
                    double discountExVat = amountAtThisVatRateExVat * (existingRow.getDiscountPercent() / 100);

                    orderRow.PricePerUnit = -MathUtil.bankersRound(discountExVat);
                    orderRow.VatPercent = vatRate;

                    //Relative discounts is a special case where we want to use the discount percent in calculations
                    //but not display it on the order row. Since that would imply that it's a discount on a discount.
                    //So that is why we force the value to 0 below.
                    orderRow.DiscountPercent = 0;

                    newRows.add(orderRow);
                }
			}
			
			// other row types (order row, shipping fee, invoice fee)
			else {
                newRows.add(serializeAmountAndVat(existingRow.getAmountExVat(), existingRow.getVatPercent(),
                        existingRow.getAmountIncVat(), newRowBasedOnExisting(existingRow)));
			}
		}
	}
	
    private SveaOrderRow newRowBasedOnExisting(RowBuilder existingRow) {
        SveaOrderRow newOrderRow = new SveaOrderRow();
        newOrderRow = serializeOrder(existingRow.getArticleNumber(), existingRow.getDescription(),
                                     existingRow.getName(), existingRow.getUnit(), newOrderRow);

        newOrderRow.DiscountPercent = existingRow.getDiscountPercent();
        newOrderRow.NumberOfUnits = existingRow.getQuantity();

        return newOrderRow;
    }
    
    private String formatDiscountRowDescription(String name, String description, long vatRate) {
        String formattedDescription;
        if (name != null) {
            formattedDescription = name + (description == null ? "" : ": " + description);
        }
        else {
            formattedDescription = description != null ? description : "";
        }

        formattedDescription += " (" + vatRate + "%)";

        return formattedDescription;
    }

	private void formatOrderRows() {
		formatRowLists(order.getOrderRows());
	}

	private void formatShippingFeeRows() {
		if (this.order.getShippingFeeRows() == null) {
			return;
		}

		formatRowLists(order.getShippingFeeRows());
	}

	private void formatInvoiceFeeRows() {
		if (this.order.getInvoiceFeeRows() == null) {
			return;
		}

		formatRowLists(order.getInvoiceFeeRows());
	}

	private void formatFixedDiscountRows() {
		if (this.order.getFixedDiscountRows() == null) {
			return;
		}

		formatRowLists(order.getFixedDiscountRows());
	}

	private void formatRelativeDiscountRows() {
		if (this.order.getRelativeDiscountRows() == null) {
			return;
		}

		formatRowLists(order.getRelativeDiscountRows());
	}

	// populate SveaOrderRow from passed arguments (taken from OrderRow)
	private SveaOrderRow serializeOrder(String articleNumber, String description, String name, String unit, SveaOrderRow orderRow) {
		if (articleNumber != null) {
			orderRow.ArticleNumber = articleNumber;
		}

		if (name != null) {
			orderRow.Description = name + (description == null ? "" : ": " + description);
		}
		else {
			orderRow.Description = description == null ? "" : description;
		}

		if (unit != null) {
			orderRow.Unit = unit;
		}	

		return orderRow;
	}

	private SveaOrderRow serializeAmountAndVat(Double amountExVat, Double vatPercent, Double amountIncVat, SveaOrderRow orderRow) {
		if (vatPercent != null && amountExVat != null) {
			orderRow.PricePerUnit = amountExVat;
			orderRow.VatPercent = vatPercent;
			orderRow.PriceIncludingVat = false;
		}
		else if (vatPercent != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountIncVat;
			orderRow.VatPercent = vatPercent;
			orderRow.PriceIncludingVat = true;
		}
		else if (amountExVat != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountIncVat;
			orderRow.VatPercent = ((amountIncVat / amountExVat) - 1) * 100;
			orderRow.PriceIncludingVat = true;
		}

		return orderRow;
	}
}
