package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.util.constant.OrderRowStatus;

public class NumberedOrderRowBuilder extends OrderRowBuilder {

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
	public void setCreditInvoiceId(String creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	public OrderRowStatus getStatus() {
		return status;
	}
	public void setStatus(OrderRowStatus status) {
		this.status = status;
	}	
}
