package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.NodeList;

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public NodeList createOrderEuRequest(String message, String urlEndpoint) {    
            return sendSoapMessage(message, urlEndpoint, "CreateOrderEu", "CreateOrderEuResult");   
    }
    
    public NodeList closeOrderEuRequest(String message, String urlEndpoint) {
        return sendSoapMessage(message, urlEndpoint, "CloseOrderEu", "CloseOrderEuResult");       
    }
    
    public NodeList deliverOrderEuRequest(String message, String urlEndpoint) {
        return sendSoapMessage(message, urlEndpoint, "DeliverOrderEu", "DeliverOrderEuResult");      
    }
        
    public NodeList createGetAddressesEuRequest(String message, String urlEndpoint) {
        return sendSoapMessage(message, urlEndpoint, "GetAddresses", "GetAddressesResponse");
    }
    
    public NodeList createGetPaymentPlanParamsEuRequest(String message, String urlEndpoint) {
        return sendSoapMessage(message, urlEndpoint, "GetPaymentPlanParamsEu", "GetPaymentPlanParamsEuResponse");
    }
    
    private NodeList sendSoapMessage(String message, String urlEndpoint, String requestHeader, String responseHeader) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage outgoingMessage = factory.createMessage();
            
            SOAPPart soapPart = outgoingMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            
            // SoapHeader Action
            MimeHeaders headers = outgoingMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", namespace_soapAction + "/" +requestHeader);
            
            envelope.addNamespaceDeclaration(prefix, namespace_soapAction);
            
            byte[] buffer = message.getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
            StreamSource source = new StreamSource(stream);
            soapPart.setContent(source);
            
            // send message
            URL endpoint = new URL(urlEndpoint);
            SOAPMessage response = connection.call(outgoingMessage, endpoint);

            connection.close();            
            return response.getSOAPPart().getEnvelope().getElementsByTagName(responseHeader);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
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
