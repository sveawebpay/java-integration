package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.request.GetLibraryProperties;

public class SveaSoapBuilder {

    private static String prefix = "web";
    private static String namespace_soapSchema = "http://schemas.xmlsoap.org/soap/envelope/";
    private static String namespace_soapAction = "https://webservices.sveaekonomi.se/webpay";
    
    private SOAPConnectionFactory soapConnectionFactory;
    private SOAPConnection connection;
    
    public SveaSoapBuilder() {
        try {
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            connection = soapConnectionFactory.createConnection();
        } catch (SOAPException ex) {
            throw new SveaWebPayException("SOAP exception", ex);
        }
    }
    
    public NodeList createOrderEuRequest(String message, ConfigurationProvider config, PAYMENTTYPE orderType ) {
        return sendSoapMessage(message, config, orderType, "CreateOrderEu", "CreateOrderEuResult");
    }
    
    public NodeList closeOrderEuRequest(String message, ConfigurationProvider config, PAYMENTTYPE orderType) {
        return sendSoapMessage(message, config, orderType, "CloseOrderEu", "CloseOrderEuResult");
    }
    
    public NodeList deliverOrderEuRequest(String message, ConfigurationProvider config, PAYMENTTYPE orderType) {
        return sendSoapMessage(message, config, orderType, "DeliverOrderEu", "DeliverOrderEuResult");
    }
        
    public NodeList createGetAddressesEuRequest(String message, ConfigurationProvider config) {
    	PAYMENTTYPE orderType = PAYMENTTYPE.INVOICE;    	
    	@SuppressWarnings("unused")
		URL url;
        try {
        	url = config.getEndPoint( orderType );		// first try invoice credentials
        }
        catch( Exception e) {
        	orderType = PAYMENTTYPE.PAYMENTPLAN;
        	url = config.getEndPoint( orderType );		// will throw Exception if no credentials found
        }
    	    	
        return sendSoapMessage(message, config, orderType, "GetAddresses", "GetAddressesResponse");
    }
    
    public NodeList createGetPaymentPlanParamsEuRequest(String message, ConfigurationProvider config, PAYMENTTYPE orderType) {
        return sendSoapMessage(message, config, orderType, "GetPaymentPlanParamsEu", "GetPaymentPlanParamsEuResponse");
    }
    
    private NodeList sendSoapMessage(String message, ConfigurationProvider config, PAYMENTTYPE orderType, String requestHeader, String responseHeader) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage outgoingMessage = factory.createMessage();
            
            SOAPPart soapPart = outgoingMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            
            // SoapHeader Action
            MimeHeaders headers = outgoingMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", namespace_soapAction + "/" +requestHeader);
            
        	HashMap<String,String> properties = GetLibraryProperties.getSveaLibraryProperties();            
            
            headers.addHeader("X-Svea-Library-Name", properties.get("library_version") + "1" );
            headers.addHeader("X-Svea-Library-Version", properties.get("library_name") + "2" );
            headers.addHeader("X-Svea-Integration-Platform", config.getIntegrationPlatform() );
            headers.addHeader("X-Svea-Integration-Company", config.getIntegrationCompany() );
            headers.addHeader("X-Svea-Integration-Version", config.getIntegrationVersion() );
                        
            envelope.addNamespaceDeclaration(prefix, namespace_soapAction);
            
            byte[] buffer = message.getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
            StreamSource source = new StreamSource(stream);
            soapPart.setContent(source);
            
            // send message
            SOAPMessage response = connection.call(outgoingMessage, config.getEndPoint(orderType) );
            
            connection.close();
            
            return response.getSOAPPart().getEnvelope().getElementsByTagName(responseHeader);
        } catch (SOAPException ex) {
            throw new SveaWebPayException("SOAP exception", ex);
        }
    }
    
    public String makeSoapMessage(String soapMethod, String xmlDocument) {
        StringBuffer result = new StringBuffer();
        result.append("<soapenv:Envelope xmlns:soapenv=\"" + namespace_soapSchema + "\" xmlns:" + prefix + "=\"" + namespace_soapAction + "\">");
        result.append("<soapenv:Header/>");
        result.append("<soapenv:Body>");
        result.append("<" + prefix + ":" + soapMethod + ">");
        result.append(xmlDocument);
        result.append("</" + prefix + ":" + soapMethod + ">");
        result.append("</soapenv:Body>");     
        result.append("</soapenv:Envelope>");
        
        return result.toString();
    }
}
