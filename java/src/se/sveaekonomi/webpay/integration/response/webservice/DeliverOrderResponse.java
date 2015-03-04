package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;

public class DeliverOrderResponse extends WebServiceResponse implements Respondable {
																			// see WS/DeliverOrdderResult:
    private Double amount;													// Amount (Decimal)
    private ORDERTYPE orderType;											// OrderType (Enum {Invoice|PaymentPlan})
    // below if OrderType = Invoice
    private Long invoiceId;													// InvoiceResultDetails.InvoiceId (Long)
    private String dueDate;													// InvoiceResultDetails.DueDate (DateTime)
    private String invoiceDate;												// InvoiceResultDetails.InvoiceDate (DateTime)
    private DISTRIBUTIONTYPE invoiceDistributionType;						// InvoiceResultDetails.InvoiceDistributionType (Enum {..})
    private String ocr;														// InvoiceResultDetails.Ocr (?)					// TODO check types
    private Double lowestAmountToPay;										// InvoiceResultDetails.LowestAmountToPay (?)
    // below if OrderType = PaymentPlan
    private Long contractNumber;  											// PaymentPlanResultDetails.ContractNumber (Long)		
    
        
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public ORDERTYPE getOrderType() {
        return orderType;
    }    
    public void setOrderType(ORDERTYPE orderType) {
        this.orderType = orderType;
    }
    public Long getInvoiceId() {
        return invoiceId;
    } 
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    public String getInvoiceDate() {
        return invoiceDate;
    }
    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    public DISTRIBUTIONTYPE getInvoiceDistributionType() {
        return invoiceDistributionType;
    }
    public void setInvoiceDistributionType(DISTRIBUTIONTYPE invoiceDistributionType) {
        this.invoiceDistributionType = invoiceDistributionType;
    }
    public String getOcr() {
        return ocr;
    }
    public void setOcr(String ocr) {
        this.ocr = ocr;
    }
    public Double getLowestAmountToPay() {
        return lowestAmountToPay;
    }
    public void setLowestAmountToPay(Double lowestAmountToPay) {
        this.lowestAmountToPay = lowestAmountToPay;
    }
    public Long getContractNumber() {
        return contractNumber;
    }
    public void setContractNumber(Long contractNumber) {
        this.contractNumber = contractNumber;
    }
    
    
    public DeliverOrderResponse(NodeList soapMessage) {
        super(soapMessage);
        this.setValues(soapMessage);
    }
    
    public void setValues(NodeList soapResponse) {
        String tmpOrderType;
        
        int size = soapResponse.getLength();
        
        for (int i = 0; i < size; i++) {
            Element node = (Element) soapResponse.item(i);
            
            if (this.getErrorMessage() == null) {
                this.setAmount(Double.parseDouble(getTagValue(node, "Amount")));
                tmpOrderType = getTagValue(node, "OrderType");
                
                if (tmpOrderType.equals(ORDERTYPE.Invoice.toString())) {
                    // Set child nodes from InvoiceResultDetails
                    setChildNodeValue(node, "InvoiceId");
                    setChildNodeValue(node, "DueDate");
                    setChildNodeValue(node, "InvoiceDate");
                    setChildNodeValue(node, "InvoiceDistributionType");
                    setChildNodeValue(node, "Ocr");
                    setChildNodeValue(node, "LowestAmountToPay");
                } else {
                    setChildNodeValue(node, "ContractNumber");
                }
            }
        }
    }
    
    private void setChildNodeValue(Node n, String tagName) {
        String tagValue = "";
        
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int length = nl.getLength();
            
            for (int j = 0; j < length; j++) {
                Node childNode = nl.item(j);
                String nodeName = childNode.getNodeName();
                
                if (nodeName.equals(tagName)) {
                    tagValue = getTagValue((Element) n, tagName);
                    
                    if (tagValue != null) {
                        this.setValue(tagName, tagValue);
                    }
                }
                
                setChildNodeValue(childNode, tagName);
            }
        }
    }
    
    private void setValue(String tagName, String tagValue) {
        if (tagName.equals("InvoiceId")) {
            this.setInvoiceId(Long.valueOf(tagValue));
        } else if (tagName.equals("DueDate")) {
            this.setDueDate(tagValue);
        } else if (tagName.equals("InvoiceDate")) {
            this.setInvoiceDate(tagValue);
        } else if (tagName.equals("InvoiceDistributionType")) {
            this.setInvoiceDistributionType( DISTRIBUTIONTYPE.fromString(tagValue) );
        } else if (tagName.equals("ContractNumber")) {
            this.setContractNumber(Long.valueOf(tagValue));
        } else if (tagName.equals("Ocr")) {
            this.setOcr(tagValue);
        } else if (tagName.equals("LowestAmountToPay")) {
            this.setLowestAmountToPay(Double.valueOf(tagValue));
        }
    }
}
