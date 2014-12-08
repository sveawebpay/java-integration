package se.sveaekonomi.webpay.integration.response.hosted.hostedadmin;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.OrderRowStatus;

public class QueryTransactionResponse extends HostedAdminResponse implements
		Respondable {

	private String rawResponse;
	/** transactionId -- the order id at Svea */
	private String transactionid;
	/** clientOrderNumber -- the customer reference number, i.e. order number */
	private String clientOrderNumber;
	/** merchantId -- the merchant id */
	private String merchantId;
	/**
	 * status -- Latest transaction status, one of {AUTHORIZED, CONFIRMED,
	 * SUCCESS}
	 */
	private String status;
	/**
	 * amount -- Total amount including VAT, in minor currency (e.g. SEK 10.50 =
	 * 1050)
	 */
	private String amount;
	/** currency -- ISO 4217 alphabetic, e.g. SEK */
	private String currency;
	/** vat -- VAT, in minor currency */
	private String vat;
	/** capturedamount -- Captured amount */
	private String capturedamount;
	/** authorizedamount -- Authorized amount */
	private String authorizedamount;
	/**
	 * created -- Timestamp when transaction was created in Sveas' system, e.g.
	 * 2011-09-27 16:55:01.21
	 */
	private String created;
	/** creditstatus -- Status of the last credit attempt */
	private String creditstatus;
	/** creditedamount -- Total amount that has been credited, in minor currency */
	private String creditedamount;
	/** merchantresponsecode -- Last statuscode response returned to merchant */
	private String merchantresponsecode;
	/** paymentMethod */
	private String paymentMethod;
	/**
	 * NumberedOrderRows w/set Name, Description, ArticleNumber, AmountExVat,
	 * VatPercent, Quantity and Unit, rowNumber. May be null if no order rows 
	 * specified when order was created.
	 */
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
	/** callbackurl */
	private String callbackurl;
	/**
	 * capturedate -- The date the transaction was captured, e.g. 2011-09-2716:55:01.21
	 */
	private String capturedate;
	/** subscriptionId */
	private String subscriptionId;
	/** subscriptiontype */
	private String subscriptiontype;
	/** cardType */
	private String cardType;
	/** maskedCardNumber */
	private String maskedCardNumber;
	/**
	 * eci -- Enrollment status from MPI. If the card is 3Dsecure enabled or
	 * not.
	 */
	private String eci;
	/** mdstatus -- Value calculated from eci as requested by acquiring bank. */
	private String mdstatus;
	/** expiryYear -- Expire year of the card */
	private String expiryYear;
	/** expiryMonth -- Expire month of the month */
	private String expiryMonth;
	/** chname -- Cardholder name as entered by cardholder */
	private String chname;
	/** authCode -- EDB authorization code */
	private String authCode;

	public void setTransactionId(String transactionid) {
		this.transactionid = transactionid;
	}

	public QueryTransactionResponse(String responseXmlBase64, String secretWord) {
		super(responseXmlBase64, secretWord);
		this.rawResponse = this.xml;
		this.setNumberedOrderRows(new ArrayList<NumberedOrderRowBuilder>());
		this.setValues();
	}

	/**
	 * parses response xml and sets response attributes
	 */
	void setValues() {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document d1 = builder.parse(new InputSource(new StringReader(xml)));
			NodeList nodeList = d1.getElementsByTagName("response");
			int size = nodeList.getLength();

			for (int i = 0; i < size; i++) {
				Element element = (Element) nodeList.item(i);

				int status = Integer
						.parseInt(getTagValue(element, "statuscode"));
				if (status == 0) {
					this.setOrderAccepted(true);
					this.setResultCode("0 (ORDER_ACCEPTED)");
				} else {
					this.setOrderAccepted(false);
					setErrorParams(status);
				}

				if (this.isOrderAccepted()) { // don't attempt to parse a bad
												// response

					this.setTransactionId(getTagAttribute(element,
							"transaction", "id"));
					this.setClientOrderNumber(getTagValue(element,
							"customerrefno"));
					this.setMerchantId(getTagValue(element, "merchantid"));
					this.setStatus(getTagValue(element, "status"));
					this.setAmount(getTagValue(element, "amount"));
					this.setCurrency(getTagValue(element, "currency"));
					this.setVat(getTagValue(element, "vat"));
					this.setCapturedamount(getTagValue(element,
							"capturedamount"));
					this.setAuthorizedamount(getTagValue(element,
							"authorizedamount"));
					this.setCreated(getTagValue(element, "created"));
					this.setCreditstatus(getTagValue(element, "creditstatus"));
					this.setCreditedamount(getTagValue(element,
							"creditedamount"));
					this.setMerchantresponsecode(getTagValue(element,
							"merchantresponsecode"));
					this.setPaymentMethod(getTagValue(element, "paymentmethod"));
					this.setCallbackUrl(getTagValue(element, "callbackurl"));
					this.setCapturedate(getTagValue(element, "capturedate"));
					this.setSubscriptionId(getTagValue(element,
							"subscriptionid"));
					this.setSubscriptiontype(getTagValue(element,
							"subscriptiontype"));
					this.setCardType(getTagValue(element, "cardType"));
					this.setMaskedCardNumber(getTagValue(element,
							"maskedcardno"));
					this.setEci(getTagValue(element, "eci"));
					this.setMdstatus(getTagValue(element, "mdstatus"));
					this.setExpiryYear(getTagValue(element, "expiryyear"));
					this.setExpiryMonth(getTagValue(element, "expirymonth"));
					this.setChname(getTagValue(element, "chname"));
					this.setAuthCode(getTagValue(element, "authCode"));



					NodeList orderrows = getTagNodes(element, "orderrows");
					try {
					for (int r = 0; r < orderrows.getLength(); r++) {
						Element row = (Element) orderrows.item(r);
						NumberedOrderRowBuilder nrow = new NumberedOrderRowBuilder();

						nrow.setName(getTagValue(row,"name"));
						
						float row_amount = Float.valueOf( getTagValue(row,"amount") );	// centessimal
						float row_vat = Float.valueOf( getTagValue(row,"vat") ); // centessimal
						float row_amountExVat = (row_amount-row_vat);
						float row_vatPercent = (row_vat/(row_amountExVat));
						
						nrow.setAmountExVat( row_amountExVat /100f ); 
						nrow.setVatPercent( row_vatPercent *100f);
						nrow.setDescription(getTagValue(row,"description"));
						nrow.setQuantity( Double.valueOf( getTagValue(row,"quantity") ) );
						nrow.setArticleNumber(getTagValue(row,"sku"));
						nrow.setUnit(getTagValue(row,"unit"));

						nrow.setCreditInvoiceId(null);
						nrow.setInvoiceId(null);
						nrow.setRowNumber(r+1);
						nrow.setStatus(null);
						
						this.numberedOrderRows.add(nrow);
					}
					}
					catch ( NullPointerException e ) {
						// no order rows in queried order
					}
				}
			}
		} catch (ParserConfigurationException e) {
			throw new SveaWebPayException("ParserConfigurationException", e);
		} catch (SAXException e) {
			throw new SveaWebPayException("SAXException", e);
		} catch (IOException e) {
			throw new SveaWebPayException("IOException", e);
		}
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public String getTransactionId() {
		return transactionid;
	}

	public String getClientOrderNumber() {
		return clientOrderNumber;
	}

	public void setClientOrderNumber(String clientOrderNumber) {
		this.clientOrderNumber = clientOrderNumber;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getCapturedAmount() {
		return capturedamount;
	}

	public void setCapturedamount(String capturedamount) {
		this.capturedamount = capturedamount;
	}

	public String getAuthorizedAmount() {
		return authorizedamount;
	}

	public void setAuthorizedamount(String authorizedamount) {
		this.authorizedamount = authorizedamount;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreditstatus() {
		return creditstatus;
	}

	public void setCreditstatus(String creditstatus) {
		this.creditstatus = creditstatus;
	}

	public String getCreditedAmount() {
		return creditedamount;
	}

	public void setCreditedamount(String creditedamount) {
		this.creditedamount = creditedamount;
	}

	public String getMerchantResponseCode() {
		return merchantresponsecode;
	}

	public void setMerchantresponsecode(String merchantresponsecode) {
		this.merchantresponsecode = merchantresponsecode;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCallbackUrl() {
		return callbackurl;
	}

	public void setCallbackUrl(String callbackurl) {
		this.callbackurl = callbackurl;
	}

	public String getCaptureDate() {
		return capturedate;
	}

	public void setCapturedate(String capturedate) {
		this.capturedate = capturedate;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getSubscriptionType() {
		return subscriptiontype;
	}

	public void setSubscriptiontype(String subscriptiontype) {
		this.subscriptiontype = subscriptiontype;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getMaskedCardNumber() {
		return maskedCardNumber;
	}

	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}

	public String getEci() {
		return eci;
	}

	public void setEci(String eci) {
		this.eci = eci;
	}

	public String getMdstatus() {
		return mdstatus;
	}

	public void setMdstatus(String mdstatus) {
		this.mdstatus = mdstatus;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getChname() {
		return chname;
	}

	public void setChname(String chname) {
		this.chname = chname;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
		return numberedOrderRows;
	}

	public void setNumberedOrderRows(
			ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
		this.numberedOrderRows = numberedOrderRows;
	}

}
