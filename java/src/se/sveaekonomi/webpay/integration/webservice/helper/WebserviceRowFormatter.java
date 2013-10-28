package se.sveaekonomi.webpay.integration.webservice.helper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Formatter.BigDecimalLayoutForm;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class WebserviceRowFormatter {
	
	private OrderBuilder<?> order;

	private double totalAmountExVat;
	private double totalAmountInclVat;
	private double totalVatAsAmount;
	private double totalVatAsPercent;

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
		totalAmountExVat = 0;
		totalVatAsAmount = 0;
		List<OrderRowBuilder> orderRows = order.getOrderRows();
		double vatPercentAsHundredth;

		for (OrderRowBuilder existingRow : orderRows) {
			vatPercentAsHundredth = existingRow.getVatPercent() != null ? existingRow.getVatPercent() * 0.01 : 0;

			if (existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null) {
				this.totalAmountExVat += existingRow.getAmountExVat();
				this.totalVatAsAmount += vatPercentAsHundredth * existingRow.getAmountExVat();
			}
			else if (existingRow.getVatPercent() != null && existingRow.getAmountIncVat() != null) {
				this.totalAmountInclVat += existingRow.getAmountIncVat();
				this.totalVatAsAmount += (vatPercentAsHundredth / (1 + vatPercentAsHundredth)) * existingRow.getAmountIncVat();
			}
			else {
				this.totalAmountInclVat += existingRow.getAmountIncVat();
				this.totalAmountExVat += existingRow.getAmountExVat();
				this.totalVatAsAmount += existingRow.getAmountIncVat() - existingRow.getAmountExVat();
			}
		}

		totalAmountInclVat = totalAmountExVat + totalVatAsAmount;
		totalAmountExVat = totalAmountInclVat - totalVatAsAmount;
		totalVatAsPercent = totalVatAsAmount / totalAmountInclVat; // e.g. 0,20  if percentage 20
	}
	
	private double bankersRounding(double value)
	{
		return new BigDecimal(value)
		.setScale(2, RoundingMode.HALF_EVEN)
		.round(new MathContext(0))
		.doubleValue();
	}

	private <T extends RowBuilder> void formatRowLists(List<T> rows) {
		for (RowBuilder existingRow : rows) {
			SveaOrderRow orderRow = new SveaOrderRow();
			orderRow = serializeOrder(existingRow.getArticleNumber(), existingRow.getDescription(), existingRow.getName(), existingRow.getUnit(), orderRow);

			orderRow.DiscountPercent = existingRow.getDiscountPercent();
			orderRow.NumberOfUnits = existingRow.getQuantity();

			if (FixedDiscountBuilder.class.equals(existingRow.getClass())) {
				double amount = ((FixedDiscountBuilder) existingRow).getAmount();
				double productTotalAfterDiscount = totalAmountInclVat - amount;
				double totalProductVatAsAmountAfterDiscount = totalVatAsPercent * productTotalAfterDiscount;
				double discountVatAsAmount = totalVatAsAmount - totalProductVatAsAmountAfterDiscount;

				double pricePerUnitExVat = bankersRounding(((amount - discountVatAsAmount) * 100.0) / 100.0);
				
				orderRow.PricePerUnit = -pricePerUnitExVat;
				orderRow.VatPercent = bankersRounding(((discountVatAsAmount * 100.0 / (amount - discountVatAsAmount) * 100.0)) / 100.0);
			}
			else if (RelativeDiscountBuilder.class.equals(existingRow.getClass())) {
				double pricePerUnitExVat = bankersRounding(((totalAmountExVat * (existingRow.getDiscountPercent() * 0.01)) * 100.00) / 100.00);

				orderRow.PricePerUnit = -pricePerUnitExVat;

				orderRow.VatPercent = bankersRounding(totalVatAsAmount * 100.0 * (existingRow.getDiscountPercent() / pricePerUnitExVat / 100.0));

				// Relative discounts is a special case where we want to use the
				// discount percent in calculations
				// but not display it on the order row. Since that would imply
				// that it's a discount on a discount.
				// So that is why we force the value to 0 below.
				orderRow.DiscountPercent = 0;
			}
			else {
				orderRow = serializeAmountAndVat(existingRow.getAmountExVat(), existingRow.getVatPercent(), existingRow.getAmountIncVat(), orderRow);
			}

			newRows.add(orderRow);
		}
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
		}
		else if (vatPercent != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountIncVat / ((0.01 * vatPercent) + 1);
			orderRow.VatPercent = vatPercent;
		}
		else if (amountExVat != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountExVat;
			orderRow.VatPercent = ((amountIncVat / amountExVat) - 1) * 100;
		}

		return orderRow;
	}
}
