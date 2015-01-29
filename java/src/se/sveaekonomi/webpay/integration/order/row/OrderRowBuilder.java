package se.sveaekonomi.webpay.integration.order.row;

/**
 * @author klar-sar, Kristian Grossman-Madsen
 */
public class OrderRowBuilder<T extends OrderRowBuilder<T>> extends RowBuilder {

	private String articleNumber;
	private String name;
	private String description;
	private Double amountExVat;
	private Double amountIncVat;
	private Double vatPercent;
	private Double quantity;
	private String unit;
	private int vatDiscount;
	private double discountPercent;

	public OrderRowBuilder() {
	}

	/**
	 * Optional
	 * 
	 * @param articleNumber
	 * @return OrderRowBuilder
	 */
	public T setArticleNumber(String articleNumber) {
		this.articleNumber = articleNumber;
		return getGenericThis();
	}
	public String getArticleNumber() {
		return articleNumber;
	}

	/**
	 * Optional - short item name 
	 * Note that this will be merged with the item description when the request is sent to Svea
	 * 
	 * @param name
	 * @return OrderRowBuilder
	 */
	public T setName(String name) {
		this.name = name;
		return getGenericThis();
	}

	public String getName() {
		return name;
	}

	/**
	 * Optional - long item description 
	 * Note that this will be merged with the item name when the request is sent to Svea
	 * 
	 * @param description
	 * @return OrderRowBuilder
	 */
	public T setDescription(String description) {
		this.description = description;
		return getGenericThis();
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Recommended - precisely two of these values must be set in the WebPayItem
	 * object: AmountExVat, AmountIncVat or VatPercent for order row. 
	 * 
	 * Use functions setAmountExVat(), setAmountIncVat() or setVatPercent(). The
	 * recommended is to use setAmountExVat() and setVatPercent().
	 * 
	 * Order row item price excluding taxes, expressed as a float value.
	 * 
	 * @param float $AmountAsFloat
	 * @return $this
	 */
	public T setAmountExVat(Double dExVatAmount) {
		this.amountExVat = dExVatAmount;
		return getGenericThis();
	}	
	
	public T setAmountExVat(double dExVatAmount) {
		this.amountExVat = Double.valueOf(dExVatAmount);
		return getGenericThis();
	}

	public Double getAmountExVat() {
		return amountExVat;
	}

	/**
	 * Recommended - precisely two of these values must be set in the WebPayItem
	 * object: AmountExVat, AmountIncVat or VatPercent for order row. 
	 * 
	 * Use functions setAmountExVat(), setAmountIncVat() or setVatPercent(). The
	 * recommended is to use setAmountExVat() and setVatPercent().
	 * 
	 * Order row item price vat rate in percent, expressed as an integer.
	 * 
	 * @param vatPercent
	 * @return OrderRowBuilder
	 */
	public T setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
		return getGenericThis();
	}

	/** When refering to a queried order row, may contain the value NaN if order row was originally specified ex vat. */
	public Double getVatPercent() {
		return vatPercent;
	}

	/**
	 * Required -- item quantity, i.e. how many were ordered of this item 
	 * 
	 * The integration package supports fractions input at any precision, but when
	 * sending the request to Svea numbers are rounded to two decimal places.
	 * 
	 * @param quantity
	 * @return OrderRowBuilder
	 */
	public T setQuantity(Double quantity) {
		this.quantity = quantity;
		return getGenericThis();
	}

	public Double getQuantity() {
		return quantity;
	}

	/**
	 * Optional - the name of the unit used for the order quantity, i.e.
	 * "pieces", "pcs.", "st.", "mb" et al.
	 * 
	 * @return OrderRowBuilder
	 */
	public T setUnit(String unit) {
		this.unit = unit;
		return getGenericThis();
	}
	
	public String getUnit() {
		return unit;
	}

	/**
	 * Optional
	 * 
	 * @param vatDiscount
	 * @return OrderRowBuilder
	 */
	// TODO investigate this, not used in php package (defaults to 0)??
	public T setVatDiscount(int vatDiscount) {
		this.vatDiscount = vatDiscount;
		return getGenericThis();
	}
	
	public int getVatDiscount() {
		return vatDiscount;
	}

	/**
	 * Optional - discount in percent, applies to this order row only
	 * 
	 * @param discountPercent
	 * @return OrderRowBuilder
	 */
	public T setDiscountPercent(Double discountPercent) {
		this.discountPercent = discountPercent;
		return getGenericThis();
	}
	
	public double getDiscountPercent() {
		return discountPercent;
	}

	/**
	 * Optional - precisely two of these values must be set in the WebPayItem
	 * object: AmountExVat, AmountIncVat or VatPercent for order row. 
	 * 
	 * Use functions setAmountExVat(), setAmountIncVat() or setVatPercent(). The
	 * recommended is to use setAmountExVat() and setVatPercent().
	 * 
	 * Order row item price including tax, expressed as a float value.
	 * 
	 * Required to use at least two of the methods setAmountExVat(),
	 * setAmountIncVat() or setVatPercent()
	 * 
	 * @param amountIncVat
	 */
	public T setAmountIncVat(Double amountIncVat) {
		this.amountIncVat = amountIncVat;
		return getGenericThis();
	}
	
	
	public T setAmountIncVat(double amountIncVat) {
		this.amountIncVat = Double.valueOf(amountIncVat);
		return getGenericThis();
	}

	/** When refering to a queried order row, may contain the value NaN if order row was originally specified ex vat. */
	public Double getAmountIncVat() {
		return amountIncVat;
	}
	
	// used to return correct type for fluent methods in this and descendant classes
	@SuppressWarnings("unchecked")
	protected T getGenericThis() {
		return (T) this;
	}
}
