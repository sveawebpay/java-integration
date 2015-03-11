package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public abstract class HostedAdminRequest<T extends HostedAdminRequest<T>> {

	protected ConfigurationProvider config;
	protected String method;
	
	/** Used to disambiguate between the various credentials in ConfigurationProvider. */
	private COUNTRYCODE countryCode;
		
	public HostedAdminRequest(ConfigurationProvider config, String method) {
		this.config = config;
		this.method = method;
	}    
    
	public T setCountryCode( COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return (T) this;
	}
	public COUNTRYCODE getCountryCode() {
		return countryCode;
	}

	protected String sendHostedAdminRequest( Hashtable<String, String> requestFields ) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		// send request to Svea
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
		 * Used by getPaymentUrl() to parse the HttpClient request response from Svea, returning the service xml response as a string 
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
		return xmlResponse;
	}
	

	/** extracts <message> node contents from xml string */	
	protected String getResponseMessageFromXml(String xml) {
	    
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
	
}
