package se.sveaekonomi.webpay.integration.response.hosted.hostedadmin;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;

public class QueryTransactionResponse extends HostedAdminResponse implements Respondable {
																						// see HostedService/query response:
	private String rawResponse;
	/** transactionId -- the order id at Svea */
	private Long transactionid;															// transaction attribute id (Numeric, N)
	/** clientOrderNumber -- the customer reference number, i.e. order number */			
	private String clientOrderNumber;													// customerrefno (AN(64))
	/** merchantId -- the merchant id */
	private String merchantId;															// merchantid (N)				
	/** status -- Latest transaction status, one of {AUTHORIZED, CONFIRMED, SUCCESS} */
	private String status;																// status (AN(32))
	/** amount -- Total amount including VAT */						
	private Double amount;																// amount (minor currency), N
	/** currency -- ISO 4217 alphabetic, e.g. SEK */
	private String currency;															// currency, (AN(3))
	/** vat -- VAT amount */
	private Double vat;																	// vat (minor currency), N
	/** capturedamount -- Captured amount */
	private Double capturedamount;														// capturedamount (minor currency), N
	/** authorizedamount -- Authorized amount */
	private Double authorizedamount;													// authorizedamount (minor currency), N
	/** created -- Timestamp when transaction was created in Sveas' system, e.g. 2011-09-27 16:55:01.21 */
	private String created;																// created, timestamp (AN?)
	/** creditstatus -- Status of the last credit attempt */
	private String creditstatus;														// creditstatus, (AN(32))
	/** creditedamount -- Total amount that has been credited */
	private Double creditedamount;														// creditedamount (minor currency), N	
	/** merchantresponsecode -- Last statuscode response returned to merchant */
	private String merchantresponsecode;												// merchantresponsecode (N)
	/** paymentMethod */
	private String paymentMethod;														// paymentmethod (AN(32))	
	/**
	 * NumberedOrderRows w/set Name, Description, ArticleNumber, AmountExVat,
	 * VatPercent, Quantity and Unit, rowNumber. May be null if no order rows 
	 * specified when order was created.
	 */
	private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;						// orderrows (complex type)		
	/** callbackurl */
	private String callbackurl;															// callbackurl (AN?)
	/** capturedate -- The date the transaction was captured, e.g. 2011-09-2716:55:01.21 */
	private String capturedate;															// capturedate, timestamp (AN?)
	/** subscriptionId */
	private Long subscriptionId;														// iff subscription set in payment request, ? (?)
	/** subscriptiontype */															
	private String subscriptiontype;													// iff subscription set in payment request, ? (?)
	/** cardType */
	private String cardType;															// ?
	/** maskedCardNumber */
	private String maskedCardNumber;													// ?
	/** eci -- Enrollment status from MPI. If the card is 3Dsecure enabled or not. */
	private String eci;																	// eci (?)
	/** mdstatus -- Value calculated from eci as requested by acquiring bank. */
	private String mdstatus;															// mdstatus (?)
	/** expiryYear -- Expire year of the card */
	private String expiryYear;															// expiryyear (?)	
	/** expiryMonth -- Expire month of the month */
	private String expiryMonth;															// expirymonth (?)
	/** chname -- Cardholder name as entered by cardholder */
	private String chname;																// chname (?)
	/** authCode -- EDB authorization code */
	private String authCode;															// authcode (?)

	public void setTransactionId(Long transactionid) {
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

				int status = Integer.parseInt(getTagValue(element, "statuscode"));
				if (status == 0) {
					this.setOrderAccepted(true);
					this.setResultCode("0 (ORDER_ACCEPTED)");
				} else {
					this.setOrderAccepted(false);
					setErrorParams(status);
				}

				if (this.isOrderAccepted()) { // don't attempt to parse a bad response

					this.setTransactionId( Long.valueOf( getTagAttribute(element,"transaction", "id")) );
					this.setClientOrderNumber(getTagValue(element,"customerrefno"));
					this.setMerchantId(getTagValue(element, "merchantid"));
					this.setStatus(getTagValue(element, "status"));
					
					String strAmount = getTagValue(element, "amount");					
					this.setAmount(minorAmountToDouble(strAmount));
					this.setCurrency(getTagValue(element, "currency"));
					
					String strVat = getTagValue(element, "vat");
					this.setVat(minorAmountToDouble(strVat));
					
					String strCapturedAmount = getTagValue(element, "capturedamount");
					this.setCapturedamount(minorAmountToDouble(strCapturedAmount));
					
					String strAuthorizedAmount = getTagValue(element, "authorizedamount");
					this.setAuthorizedamount(minorAmountToDouble(strAuthorizedAmount));
					this.setCreated(getTagValue(element, "created"));
					this.setCreditstatus(getTagValue(element, "creditstatus"));
					
					String strCreditedAmount = getTagValue(element, "creditedamount");
					this.setCreditedamount(minorAmountToDouble(strCreditedAmount));
					this.setMerchantresponsecode(getTagValue(element,"merchantresponsecode"));
					this.setPaymentMethod(getTagValue(element, "paymentmethod"));
					this.setCallbackUrl(getTagValue(element, "callbackurl"));
					this.setCapturedate(getTagValue(element, "capturedate"));
					this.setSubscriptionId(getTagValue(element,"subscriptionid")==null ? null : Long.valueOf(getTagValue(element,"subscriptionid")));
					this.setSubscriptiontype(getTagValue(element,"subscriptiontype"));
					this.setCardType(getTagValue(element, "cardType"));
					this.setMaskedCardNumber(getTagValue(element,"maskedcardno"));
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

	public Long getTransactionId() {
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Double getCapturedAmount() {
		return capturedamount;
	}

	public void setCapturedamount(Double capturedamount) {
		this.capturedamount = capturedamount;
	}

	public Double getAuthorizedAmount() {
		return authorizedamount;
	}

	public void setAuthorizedamount(Double authorizedamount) {
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

	public Double getCreditedAmount() {
		return creditedamount;
	}

	public void setCreditedamount(Double creditedamount) {
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

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
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
	
	private Double minorAmountToDouble(String amount) {		
		return (amount == null) ? 0.0 : Double.valueOf(amount)/100.0;
	}

}
