package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import java.util.ArrayList;


public class SveaDeliverInvoiceDetails {
    
    public int NumberofCreditDays;
    public String InvoiceDistributionType;
    public boolean IsCreditInvoice;
    public String InvoiceIdToCredit;
    public ArrayList<SveaOrderRow> OrderRows; 
    
    public SveaDeliverInvoiceDetails() {
        this.OrderRows = new ArrayList<SveaOrderRow> (); 
    }
        
    public void addOrderRow(SveaOrderRow orderRow) {
        OrderRows.add(orderRow);
    }
}
