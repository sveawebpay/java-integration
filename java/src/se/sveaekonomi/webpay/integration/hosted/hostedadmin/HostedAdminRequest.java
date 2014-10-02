/**
 * 
 */
package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadminresponse.HostedAdminResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;


/**
 * @author Kristian Grossman-Madsen
 * @param <T>
 *
 */
public abstract class HostedAdminRequest<T extends OrderBuilder<T>> {

	protected OrderBuilder<T> order;
	protected ConfigurationProvider config;
	protected String method;
	
	/** Used to disambiguate between the various credentials in ConfigurationProvider. */
	private COUNTRYCODE countryCode;
		
	public HostedAdminRequest(OrderBuilder<T> order, String method) {
		this.order = order;
		this.config = order.getConfig();
		this.method = method;
	}
	
    /**
     * Required. 
     */
    public HostedAdminRequest<T> setCountryCode( COUNTRYCODE countryCode ) {
        this.countryCode = countryCode;
        return this;
    }	
    
	public COUNTRYCODE getCountryCode() {
		return countryCode;
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

    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return (textList.item(0)).getNodeValue().trim();
        }        
        return null;
    }		
	
	/**
	 * returns the request fields to post to service
	 */
	public Hashtable<String,String> prepareRequest() {
		Hashtable<String,String> requestFields = new Hashtable<>();

		String merchantId = this.config.getMerchantId(PAYMENTTYPE.HOSTED, this.getCountryCode());
		String secretWord = this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode());		
		
    	String xmlMessage = getRequestMessageXml();
    	String xmlMessageBase64 = Base64Util.encodeBase64String(xmlMessage);
    	String macSha512 =  HashUtil.createHash(xmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512);			

    	requestFields.put("message", xmlMessageBase64);
    	requestFields.put("mac", macSha512);
    	requestFields.put("merchantid", merchantId);
    	
		return requestFields;
	}

	
	public <R extends HostedAdminResponse> R doRequest() throws IllegalStateException, IOException {

		// prepare request fields
    	Hashtable<String, String> requestFields = this.prepareRequest();
    	
    	// do request to Svea
		String endpoint = this.config.getEndPoint(PAYMENTTYPE.HOSTED_ADMIN).toString().concat( this.method );
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(endpoint);				

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("message", requestFields.get("message")));
		params.add(new BasicNameValuePair("mac", requestFields.get("mac")));
		params.add(new BasicNameValuePair("merchantid", requestFields.get("merchantid")));
		
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
				
		return this.parseResponse( messageInBase64 );
	}

	/**
	 * implemented by child classes, should return the request message xml for the method in question
	 */
	abstract String getRequestMessageXml();
	
	/**
	 * implemented by child classes, should return the request message xml for the method in question
	 */
	abstract <R extends HostedAdminResponse> R parseResponse( String response );	
	
}
