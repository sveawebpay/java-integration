package se.sveaekonomi.webpay.integration.order.row;

import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;

public class NumberedOrderRowBuilder extends OrderRowBuilder<NumberedOrderRowBuilder> {

    /** reference to invoice to credit */
	private Long creditInvoiceId;
    /** if order has been delivered, reference to resulting invoice */
	private Long invoiceId;
    /** the order row number, starting with 1 for the first order row */
	private Integer rowNumber;
    /** one of: "NotDelivered" | "Delivered" | "Cancelled" */
	private ORDERROWSTATUS status;

	public Long getCreditInvoiceId() {
		return creditInvoiceId;
		
	}
	public NumberedOrderRowBuilder setCreditInvoiceId(Long creditInvoiceId) {
		this.creditInvoiceId = creditInvoiceId;
		return this;
	}
	public Long getInvoiceId() {
		return invoiceId;
	}
	public NumberedOrderRowBuilder setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}
	public Integer getRowNumber() {
		return rowNumber;
	}
	public NumberedOrderRowBuilder setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
		return this;
	}
	public ORDERROWSTATUS getStatus() {
		return status;
	}
	public NumberedOrderRowBuilder setStatus(ORDERROWSTATUS status) {
		this.status = status;
		return this;
	}	
}
