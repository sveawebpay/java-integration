package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.ArrayList;
import java.util.Set;

import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class HostedRowFormatter {

	private double totalAmount;
	private double totalVat;

	private double totalOrderAmount;
	private double totalOrderVat;
	
	private double totalShippingAmount;
	private double totalShippingVat;

	private double totalInvoiceAmount;
	private double totalInvoiceVat;	
	
	private ArrayList<HostedOrderRowBuilder> newRows;

	public HostedRowFormatter() {
		totalAmount = 0.0;
		totalVat = 0.0;

		totalOrderAmount = 0.0;
		totalOrderVat = 0.0;
		
		totalShippingAmount = 0.0;
		totalShippingVat = 0.0;

		totalInvoiceAmount = 0.0;
		totalInvoiceVat = 0.0;
		
		newRows = new ArrayList<HostedOrderRowBuilder>();
	}

	public ArrayList<HostedOrderRowBuilder> formatRows(CreateOrderBuilder order) {
		formatOrderRows(order);
		formatShippingFeeRows(order);
		formatInvoiceFeeRows(order);
		formatFixedDiscountRows(order);
		formatRelativeDiscountRows(order);

		return newRows;
	}

	private <T extends OrderBuilder<T>> void formatOrderRows(OrderBuilder<T> orderBuilder) {
		for (OrderRowBuilder row : orderBuilder.getOrderRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getArticleNumber() == null ? "" : row.getArticleNumber());

			double vatFactor = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			double tempAmount;
			double tempVat;
			double quantity = row.getQuantity();

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * vatFactor;
				tempVat = tempAmount - amountExVat;
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = tempAmount - (tempAmount / vatFactor);
			}
			else {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}

			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(tempAmount));
			tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(tempVat));
			tempRow.setQuantity(quantity);

			totalOrderAmount += tempAmount * quantity;
			totalOrderVat += tempVat * quantity;
			
			totalAmount += tempAmount * quantity;
			totalVat += tempVat * quantity;

			newRows.add(tempRow);
		}
	}

	private <T extends OrderBuilder<T>> void formatShippingFeeRows(OrderBuilder<T> orderBuilder) {
		if (orderBuilder.getShippingFeeRows() == null) {
			return;
		}

		for (ShippingFeeBuilder row : orderBuilder.getShippingFeeRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getShippingId());

			double plusVatCounter = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			double tempAmount;
			double tempVat;
			double quantity = row.getQuantity();

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * plusVatCounter;
				tempVat = tempAmount - amountExVat;
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = tempAmount - (tempAmount / plusVatCounter);
			}
			else {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}

			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(tempAmount));
			tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(tempVat));
			tempRow.setQuantity(quantity);

			totalShippingAmount += tempAmount * quantity;
			totalShippingVat += tempVat * quantity;

			totalAmount += tempAmount * quantity;
			totalVat += tempVat * quantity;

			newRows.add(tempRow);
		}
	}
	
	private <T extends OrderBuilder<T>> void formatInvoiceFeeRows(OrderBuilder<T> orderBuilder) {
		if (orderBuilder.getInvoiceFeeRows() == null) {
			return;
		}

		for (InvoiceFeeBuilder row : orderBuilder.getInvoiceFeeRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, null);

			double plusVatCounter = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			double tempAmount;
			double tempVat;
			double quantity = row.getQuantity();

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * plusVatCounter;
				tempVat = tempAmount - amountExVat;
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = tempAmount - (tempAmount / plusVatCounter);
			}
			else {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}

			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(tempAmount));
			tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(tempVat));
			tempRow.setQuantity(quantity);

			totalInvoiceAmount += tempAmount * quantity;
			totalInvoiceVat += tempVat * quantity;

			totalAmount += tempAmount * quantity;
			totalVat += tempVat * quantity;

			newRows.add(tempRow);
		}
	}
	

	private void formatFixedDiscountRows(CreateOrderBuilder orderBuilder) {
		if (orderBuilder.getFixedDiscountRows() == null) {
			return;
		}

		for (FixedDiscountBuilder row : orderBuilder.getFixedDiscountRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getDiscountId());

			double vatFactor = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double tempAmount = 0.0;
			double tempVat = 0.0;
			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			// exvat + vatpercent
			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * vatFactor;
				tempVat = amountExVat * (row.getVatPercent() / 100);
			}
			// incvat + vatpercent
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - (amountIncVat / vatFactor);
			}
			// incvat + exvat
			else if (row.getAmountIncVat() != null && row.getAmountExVat() != null) {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}
							
			// no vatpercent given
			// incvat only
			else if (row.getVatPercent() == null && row.getAmountIncVat() != null ) {
				tempAmount = amountIncVat;
				tempVat = totalAmount * totalVat == 0 ? amountIncVat : amountIncVat / totalAmount * totalVat;
			}
			
			// exvat only
			else if (row.getVatPercent() == null && row.getAmountExVat() != null ) { 
				tempVat = amountExVat * (getOrderMeanVatRateBasedOnPriceIncVat( totalOrderAmount, totalOrderVat )/100);
				tempAmount = amountExVat + tempVat;
			}

			double discountedAmount = -tempAmount;
			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(discountedAmount));

			totalAmount += discountedAmount;

			if (totalVat > 0) {
				double discountedVat = -tempVat;
				tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(discountedVat));
				totalVat += discountedVat;
			}

			newRows.add(tempRow);
		}
	}
	
	Double getOrderMeanVatRateBasedOnPriceIncVat( Double inc, Double vat ) {	
		
		// algorithm: 100=inc (20=vat) => (inc-vat) * 1.v = inc => 1.v = inc/(inc-vat) = vatrate = round((1.v-1*100),2)
		return MathUtil.bankersRound(((inc/(inc-vat))-1.0)*100);		
	}

	private void formatRelativeDiscountRows(CreateOrderBuilder orderBuilder) {
		if (orderBuilder.getRelativeDiscountRows() == null) {
			return;
		}

		for (RelativeDiscountBuilder row : orderBuilder.getRelativeDiscountRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getDiscountId());

			double discountFactor = row.getDiscountPercent() / 100;

			double discountAmount = (totalAmount - totalShippingAmount) * discountFactor;
			double discountVat = 0;

			totalAmount = totalAmount - discountAmount;

			if (totalVat > 0) {
				discountVat = (totalVat - totalShippingVat) * discountFactor;
				totalVat = totalVat - discountVat;
			}

			tempRow.setAmount(-MathUtil.convertFromDecimalToCentesimal(discountAmount));
			tempRow.setVat(-MathUtil.convertFromDecimalToCentesimal(discountVat));

			newRows.add(tempRow);
		}
	}

	private HostedOrderRowBuilder getNewTempRow(RowBuilder row, String sku) {
		HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();

		if (row.getName() != null) {
			tempRow.setName(row.getName());
		}

		if (row.getDescription() != null) {
			tempRow.setDescription(row.getDescription());
		}

		tempRow.setQuantity(1.0);
		tempRow.setUnit(row.getUnit());
		tempRow.setSku(sku);

		return tempRow;
	}

	/**
	 * This method is considered imprecise and may return values effected by
	 * rounding errors.
	 * 
	 * @deprecated Use {@link getTotalAmount()} instead...
	 */
	@Deprecated
	public long formatTotalAmount(ArrayList<HostedOrderRowBuilder> rows) {
		long amount = 0L;

		for (HostedOrderRowBuilder row : rows) {
			amount += (long)(row.getAmount() * (row.getQuantity() == null ? 0.0 : row.getQuantity()));
		}

		return amount;
	}

	/**
	 * This method is considered imprecise and may return values effected by
	 * rounding errors.
	 * 
	 * @deprecated Use {@link getTotalVat()} instead...
	 */
	@Deprecated
	public long formatTotalVat(ArrayList<HostedOrderRowBuilder> rows) {
		long vat = 0L;

		for (HostedOrderRowBuilder row : rows) {
			vat += (long)(row.getVat() * (row.getQuantity() == null ? 0.0 : row.getQuantity()));
		}

		return vat;
	}

	public long getTotalAmount() {
		return MathUtil.convertFromDecimalToCentesimal(totalAmount);
	}

	public long getTotalVat() {
		return MathUtil.convertFromDecimalToCentesimal(totalVat);
	}
}
