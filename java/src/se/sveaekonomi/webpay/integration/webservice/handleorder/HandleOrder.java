package se.sveaekonomi.webpay.integration.webservice.handleorder;

import javax.xml.bind.ValidationException;

import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HandleOrderValidator;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceRowFormatter;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverInvoiceDetails;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrderInformation;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;


public class HandleOrder {
    
    private DeliverOrderBuilder order;
    private SveaDeliverOrder sveaDeliverOrder;
    private SveaDeliverOrderInformation orderInformation;
    
    private final SveaConfig conf = new SveaConfig();
    
    public HandleOrder(DeliverOrderBuilder orderBuilder) {
        this.order =  orderBuilder;
    }    
        
    protected SveaAuth getStoreAuthorization() {
        return conf.getAuthorizationForWebServicePayments(order.getOrderType());
    }
    
    public String validateOrder() {
        try{
        HandleOrderValidator validator = new HandleOrderValidator();
        return validator.validate(this.order);
        }
        catch (NullPointerException e){
            return "NullPointer in validaton of HandleOrder";
        }
    }
    public SveaRequest<SveaDeliverOrder> prepareRequest() throws ValidationException {        
        String errors = "";
        try{
            errors = validateOrder();
        } catch (Exception e) {
            throw e;
        }        
        if(errors.length() > 0)
            throw new ValidationException(errors);
        
        sveaDeliverOrder = new SveaDeliverOrder();
        sveaDeliverOrder.auth = getStoreAuthorization(); 
        orderInformation = new SveaDeliverOrderInformation(order.getOrderType());
        orderInformation.setOrderId(String.valueOf(order.getOrderId()));
        orderInformation.setOrderType(order.getOrderType());
        
        if(order.getOrderType() == "Invoice") {
            SveaDeliverInvoiceDetails invoiceDetails = new SveaDeliverInvoiceDetails();
            invoiceDetails.InvoiceDistributionType = order.getInvoiceDistributionType();
            invoiceDetails.IsCreditInvoice = (order.getInvoiceIdToCredit()!=null ? true : false);
            if(order.getInvoiceIdToCredit()!=null)
                invoiceDetails.InvoiceIdToCredit = order.getInvoiceIdToCredit();
            invoiceDetails.NumberofCreditDays = (order.getNumberOfCreditDays()!=null 
                    ? order.getNumberOfCreditDays() : 0);
            
            WebServiceRowFormatter formatter = new WebServiceRowFormatter(order);           
            invoiceDetails.OrderRows  = formatter.formatRows(); 
            orderInformation.deliverInvoiceDetails = invoiceDetails;            
        }
        sveaDeliverOrder.deliverOrderInformation = orderInformation;
        SveaRequest<SveaDeliverOrder> request = new SveaRequest<SveaDeliverOrder>();
        request.request = sveaDeliverOrder;
        return request;
    }
    
    public DeliverOrderResponse doRequest() throws Exception {        
        String url = order.getTestmode() ? SveaConfig.SWP_TEST_WS_URL : SveaConfig.SWP_PROD_WS_URL;      
        
        SveaRequest<SveaDeliverOrder> request = this.prepareRequest();
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        String xml;
        
        try {
            xml = xmlBuilder.getDeliverOrderEuXml((SveaDeliverOrder) request.request);
        } catch (Exception e) {
            throw e;
        }
        
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        String soapMessage = soapBuilder.makeSoapMessage("DeliverOrderEu", xml);
        NodeList soapResponse = soapBuilder.deliverOrderEuRequest(soapMessage, url);
        DeliverOrderResponse response = new DeliverOrderResponse(soapResponse);      
        return response;               
    }
}
