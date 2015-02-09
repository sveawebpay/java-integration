package se.sveaekonomi.webpay.integration.hosted.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.hosted.helper.HostedRowFormatter;
import se.sveaekonomi.webpay.integration.hosted.helper.HostedXmlBuilder;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentUrl;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HostedOrderValidator;
import se.sveaekonomi.webpay.integration.order.validator.IdentityValidator;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.PreparePaymentResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.constant.SUBSCRIPTIONTYPE;

/*******************************************************************************
 * Description of HostedPayment: Parent to CardPayment, DirectPayment,
 * PayPagePayment and PaymentMethodPayment classes. Prepares an order and
 * creates a payment form to integrate on web page. Uses XmlBuilder to turn
 * formatted order into xml format.
 * 
 * @author klar-sar
 * *****************************************************************************/
public abstract class HostedPayment<T extends HostedPayment<T>> {
	
	protected CreateOrderBuilder createOrderBuilder;
	protected ArrayList<HostedOrderRowBuilder> rowBuilder;
	protected List<String> excludedPaymentMethods;
	private Long amount;
	private Long vat;
	protected String returnUrl;
	protected String cancelUrl;
	protected String callbackUrl;
	protected ExcludePayments excluded;
	protected String languageCode;
	
	protected SUBSCRIPTIONTYPE subscriptionType;

	public HostedPayment(CreateOrderBuilder createOrderBuilder) {
		this.createOrderBuilder = createOrderBuilder;
		rowBuilder = new ArrayList<HostedOrderRowBuilder>();
		excluded = new ExcludePayments();
		excludedPaymentMethods = new ArrayList<String>();
		returnUrl = "";
	}

	public CreateOrderBuilder getCreateOrderBuilder() {
		return createOrderBuilder;
	}

	public ArrayList<HostedOrderRowBuilder> getRowBuilder() {
		return rowBuilder;
	}

	public List<String> getExcludedPaymentMethods() {
		return excludedPaymentMethods;
	}

	public Long getAmount() {
		return amount;
	}

	public Long getVat() {
		return vat;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public T setSubscriptionType( SUBSCRIPTIONTYPE subscriptionType ) {
		this.subscriptionType = subscriptionType;
		return getGenericThis();
	}
	
	public SUBSCRIPTIONTYPE getSubscriptionType() {
		return subscriptionType;
	}
	
	
	public T setReturnUrl(String url) {
		returnUrl = url;
		return getGenericThis();
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public T setCancelUrl(String url) {
		cancelUrl = url;
		return getGenericThis();
	}
	
	public T setCallbacklUrl(String url) {
		callbackUrl = url;
		return getGenericThis();
	}
	
	public String getCallbacklUrl() {
		return callbackUrl;
	}

	public T setPayPageLanguageCode(LANGUAGECODE languageCode) {
		this.languageCode = languageCode.toString();
		return getGenericThis();
	}

	public String getPayPageLanguageCode() {
		return languageCode;
	}

	public String validateOrder() {
		String errors = "";
		if (this.returnUrl.equals("")) {
			errors += "MISSING VALUE - Return url is required, setReturnUrl(...).\n";
		}

		HostedOrderValidator validator = new HostedOrderValidator();
		// Check if payment method is EU country, PaymentMethod: INVOICE or PAYMENTPLAN
		if (this instanceof PaymentMethodPayment) {
			if (((PaymentMethodPayment) this).getPaymentMethod() == PAYMENTMETHOD.INVOICE || 
					((PaymentMethodPayment) this).getPaymentMethod() == PAYMENTMETHOD.PAYMENTPLAN) {
				if (this.createOrderBuilder.getCountryCode().equals(COUNTRYCODE.NL)) {
					errors += new IdentityValidator().validateNLIdentity(createOrderBuilder);
				}
				else if (this.createOrderBuilder.getCountryCode().equals(COUNTRYCODE.DE)) {
					errors += new IdentityValidator().validateDEIdentity(createOrderBuilder);
				}
			}
		}

		errors += validator.validate(this.createOrderBuilder);
		return errors;

	}

	public void calculateRequestValues() {
		String errors = "";
		errors = validateOrder();

		if (!errors.equals("")) {
			throw new SveaWebPayException("Validation failed", new ValidationException(errors));
		}

		HostedRowFormatter formatter = new HostedRowFormatter();

		rowBuilder = formatter.formatRows(createOrderBuilder);
		amount = formatter.getTotalAmount();
		vat = formatter.getTotalVat();
		configureExcludedPaymentMethods();
	}

	public PaymentForm getPaymentForm() {
		calculateRequestValues();
		HostedXmlBuilder xmlBuilder = new HostedXmlBuilder();
		
		// get payment request xml
		String xml = xmlBuilder.getXml(this);

		PaymentForm form = new PaymentForm();
		form.setXmlMessage(xml);

		form.setMerchantId(createOrderBuilder.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));
		form.setSecretWord(createOrderBuilder.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));

		if (this.createOrderBuilder.getCountryCode() != null) {
			form.setSubmitMessage(this.createOrderBuilder.getCountryCode());
		}
		else {
			form.setSubmitMessage(COUNTRYCODE.SE);
		}

		form.setPayPageUrl(createOrderBuilder.getConfig().getEndPoint(PAYMENTTYPE.HOSTED));
		form.setForm();
		form.setHtmlFields();

		return form;
	}
   /**
     * getPaymentURL returns a PaymentUrl object containing the URL to a prepared hosted payment.
     * Use this to to get a link which the customer can use to confirm a prepared payment at a later
     * time, after having received the url via i.e. an email message.
     * 
     * Use function setIpAddress() on the order customer."; 
     * Use function setPayPageLanguageCode()."; 
	 * @throws IOException 		// TODO fix this
	 * @throws IllegalStateException 
     */
	public PaymentUrl getPaymentUrl() throws IllegalStateException, IOException {
		calculateRequestValues();
		HostedXmlBuilder xmlBuilder = new HostedXmlBuilder();

		// get payment request xml including required fields for preparedpayment 
		String xml = xmlBuilder.getPaymentUrlXml(this);

		PaymentForm form = new PaymentForm();
		form.setXmlMessage(xml);

		form.setMerchantId(createOrderBuilder.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));
		form.setSecretWord(createOrderBuilder.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, createOrderBuilder.getCountryCode()));

		if (this.createOrderBuilder.getCountryCode() != null) {
			form.setSubmitMessage(this.createOrderBuilder.getCountryCode());
		}
		else {
			form.setSubmitMessage(COUNTRYCODE.SE);
		}

		form.setPayPageUrl(createOrderBuilder.getConfig().getEndPoint(PAYMENTTYPE.HOSTED));
		form.setForm();
		form.setHtmlFields();

		// send form
		String endpoint = createOrderBuilder.getConfig().getEndPoint(PAYMENTTYPE.HOSTED_ADMIN).toString().concat("preparepayment");		// TODO get from config - använd URIBuilder-klassen
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(endpoint);				

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("message", form.getXmlMessageBase64()));
		params.add(new BasicNameValuePair("mac", form.getMacSha512()));
		params.add(new BasicNameValuePair("merchantid", form.getMerchantId()));
		
		post.setEntity( new UrlEncodedFormEntity(params) );
				
		// receive response
		/**
		 * Used by getPaymentUrl() to parse the HttpClient request response from Svea, returning service the xml response as a string 
		 */
		ResponseHandler<String> rh = new ResponseHandler<String>() {
		
			@Override
			public String handleResponse( final HttpResponse response ) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
		
				if( statusLine.getStatusCode() >= 300 ) {
					throw new HttpResponseException( statusLine.getStatusCode(), statusLine.getReasonPhrase() );
				}
				if( entity == null ) {
					throw new ClientProtocolException("Response contains no centent");
				}
				
				BufferedReader br = new BufferedReader( new InputStreamReader(entity.getContent()) );
		 
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}	    
				return sb.toString();
			};	
		};	
		
		String xmlResponse = client.execute(post, rh);

		String messageInBase64 = getResponseMessageFromXml( xmlResponse );
				
		// parse response message into paymentUrl
		PreparePaymentResponse parsedResponse = 
			new PreparePaymentResponse(
				messageInBase64, 
				createOrderBuilder.getConfig().getSecretWord(PAYMENTTYPE.HOSTED,createOrderBuilder.getCountryCode())
			)
		;
	
		// return paymentUrl		
		PaymentUrl paymentUrl = new PaymentUrl( parsedResponse );
		
		return paymentUrl;		
	}	

	/** extracts <message> node contents from xml string */	
	private String getResponseMessageFromXml(String xml) {
	    
	    String message = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document d1 = builder.parse(new InputSource(new StringReader(xml)));
			NodeList nodeList = d1.getElementsByTagName("response");
			int size = nodeList.getLength();

			for (int i = 0; i < size; i++) {
				Element element = (Element) nodeList.item(i);
							
				message = getTagValue(element, "message");								
			}
		} catch (ParserConfigurationException e) {
			throw new SveaWebPayException("ParserConfigurationException", e);
		} catch (SAXException e) {
			throw new SveaWebPayException("SAXException", e);
		} catch (IOException e) {
			throw new SveaWebPayException("IOException", e);
		}		
		
		return message;
	}
	
    protected String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return (textList.item(0)).getNodeValue().trim();
        }
        
        return null;
    }	

	protected abstract T configureExcludedPaymentMethods();

	public abstract XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) throws Exception;

	protected void writeSimpleElement(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
		if (value != null) {
			xmlw.writeStartElement(name);
			xmlw.writeCharacters(value);
			xmlw.writeEndElement();
		}
	}

	@SuppressWarnings("unchecked")
	private T getGenericThis() {
		return (T) this;
	}
}
