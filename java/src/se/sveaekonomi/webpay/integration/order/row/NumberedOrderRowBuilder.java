package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.util.constant.OrderRowStatus;

public class NumberedOrderRowBuilder extends OrderRowBuilder<NumberedOrderRowBuilder> {

    /** reference to invoice to credit */
	private String creditInvoiceId;
    /** if order has been delivered, reference to resulting invoice */
	private String invoiceId;
    /** the order row number, starting with 1 for the first order row */
	private int rowNumber;
    /** one of: "NotDelivered" | "Delivered" | "Cancelled" */
	private OrderRowStatus status;

	public String getCreditInvoiceId() {
		return creditInvoiceId;
		
	}
	public NumberedOrderRowBuilder setCreditInvoiceId(String creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
		return this;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public NumberedOrderRowBuilder setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}
	public int getRowNumber() {
		return rowNumber;
	}
	public NumberedOrderRowBuilder setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
		return this;
	}
	public OrderRowStatus getStatus() {
		return status;
	}
	public NumberedOrderRowBuilder setStatus(OrderRowStatus status) {
		this.status = status;
		return this;
	}	
}
