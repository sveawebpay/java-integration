package se.sveaekonomi.webpay.integration.hosted.helper;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.hosted.payment.HostedPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;
import se.sveaekonomi.webpay.integration.util.xml.XMLBuilder;

/**
 * Formats request xml in preparation of sending request to hosted webservice.
 * 
 * These methods writes requests to hosted payment & hosted admin services
 * as detailed in "Technical Specification WebPay v 2.6.8" as of 140403.
 * 
 * @author klar-sar
 * @author Kristian Grossman-Madsen
 */
public class HostedXmlBuilder extends XMLBuilder {
	
	
	/**
	 * used by getPaymentUrl() to build hosted service payment with added fields needed for preparedpayment request
	 * 
	 * @param payment
	 * @return
	 */
	public String getPaymentUrlXml(HostedPayment<?> payment) {
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");
			
			// common payment elements
			getPaymentXml(xmlw, payment);

			// preparedpayment required elements
			writeSimpleElement("lang", payment.getPayPageLanguageCode() );
			writeSimpleElement("ipaddress", payment.getCreateOrderBuilder().getCustomerIdentity().getIpAddress() );
			
			xmlw.writeEndDocument();
			xmlw.close();

			return new String(os.toByteArray(), "UTF-8");
		}
		catch (XMLStreamException e) {
			throw new SveaWebPayException("Error when building XML", e);
		}
		catch (UnsupportedEncodingException e) {
			throw new SveaWebPayException("Unsupported encoding UTF-8", e);
		}
		catch (Exception e) {
			throw new SveaWebPayException("Exception", e);
		}
	}
	
	
	/**
	 * used by getPaymentForm() to build hosted service payment request
	 * 
	 * @param payment
	 * @return
	 */
	public String getXml(HostedPayment<?> payment) {
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");
			
			// common payment elements
			getPaymentXml(xmlw, payment);

			xmlw.writeEndDocument();
			xmlw.close();

			return new String(os.toByteArray(), "UTF-8");
		}
		catch (XMLStreamException e) {
			throw new SveaWebPayException("Error when building XML", e);
		}
		catch (UnsupportedEncodingException e) {
			throw new SveaWebPayException("Unsupported encoding UTF-8", e);
		}
		catch (Exception e) {
			throw new SveaWebPayException("Exception", e);
		}
	}

	/**
	 * builds payment request xml, up to where xmlw.writeEndDocument(); xmlw.close(); remains
	 * 
	 * @param xmlw
	 * @param payment
	 * @return xmlw
	 * @throws XMLStreamException
	 * @throws Exception
	 */
	private XMLStreamWriter getPaymentXml(XMLStreamWriter xmlw, HostedPayment<?> payment ) throws XMLStreamException, Exception {
	
		CreateOrderBuilder order = payment.getCreateOrderBuilder();
		ArrayList<HostedOrderRowBuilder> rows = payment.getRowBuilder();
		
		xmlw.writeStartDocument("UTF-8", "1.0");
		xmlw.writeComment( GetRequestProperties.getLibraryAndPlatformPropertiesAsJson( payment.getCreateOrderBuilder().getConfig()) );
		
		xmlw.writeStartElement("payment");
		xmlw = payment.getPaymentSpecificXml(xmlw);
	
		writeSimpleElement("customerrefno", order.getClientOrderNumber());
		writeSimpleElement("currency", order.getCurrency());
		
		if( payment.getSubscriptionType() != null ) {
			writeSimpleElement("subscriptiontype", payment.getSubscriptionType().toString());
		}
		
		writeSimpleElement("amount", payment.getAmount().toString());
	
		if (payment.getVat() != null) {
			writeSimpleElement("vat", payment.getVat().toString());
		}
	
		writeSimpleElement("lang", payment.getPayPageLanguageCode());
		writeSimpleElement("returnurl", payment.getReturnUrl());
		writeSimpleElement("cancelurl", payment.getCancelUrl());
		writeSimpleElement("callbackurl", payment.getCallbacklUrl());
		writeSimpleElement("iscompany", order.getIsCompanyIdentity() ? "true" : "false");
	
		serializeCustomer(order, payment);
		serializeRows(rows);
		serializeExcludedPaymentMethods(payment.getExcludedPaymentMethods());
	
		writeSimpleElement("addinvoicefee", "false");
		
		return xmlw;
	}

	
	private void serializeCustomer(CreateOrderBuilder order, HostedPayment<?> payment) {
        if (order.customerIdentity == null) {
            return;
        }
		
		CustomerIdentity<?> customer;

		try {
			if (order.getIsCompanyIdentity()) {
				customer = order.getCompanyCustomer();
			}
			else {
				customer = order.getIndividualCustomer();
			}

			xmlw.writeStartElement("customer");

			// Nordic country individual customer type
			if (customer.getNationalIdNumber() != null) {
				writeSimpleElement("ssn", customer.getNationalIdNumber());
			}
			// Euro country individual
			else if (!order.getIsCompanyIdentity()) {
				writeSimpleElement("ssn", ((IndividualCustomer) customer).getBirthDate());
			}
			// Euro country, company customer and nationalId not set
			else if (order.getIsCompanyIdentity()) {
				writeSimpleElement("ssn", ((CompanyCustomer) customer).getVatNumber());
			}

			// Set for individual customer
			if (!order.getIsCompanyIdentity()) {
				IndividualCustomer individualCustomer = (IndividualCustomer) customer;

				writeSimpleElement("firstname", individualCustomer.getFirstName());
				writeSimpleElement("lastname", individualCustomer.getLastName());
				writeSimpleElement("initials", individualCustomer.getInitials());
			}
			else { // Set for company customer
				CompanyCustomer companyCustomer = (CompanyCustomer) customer;

				writeSimpleElement("firstname", companyCustomer.getCompanyName());
			}

			writeSimpleElement("phone", customer.getPhoneNumber());
			writeSimpleElement("email", customer.getEmail());
			writeSimpleElement("address", customer.getStreetAddress());
			writeSimpleElement("housenumber", customer.getHouseNumber());
			writeSimpleElement("address2", customer.getCoAddress());
			writeSimpleElement("zip", customer.getZipCode());
			writeSimpleElement("city", customer.getLocality());

			if (order.getCountryCode() != null) {
				writeSimpleElement("country", order.getCountryCode().toString());
			}

			xmlw.writeEndElement();

			writeSimpleElement("ipaddress", customer.getIpAddress());
		}
		catch (XMLStreamException e) {
			throw new SveaWebPayException("Error while building XML", e);
		}
	}

	private void serializeRows(List<HostedOrderRowBuilder> rows) throws XMLStreamException {
		if (rows == null || rows.size() == 0) {
			return;
		}

		xmlw.writeStartElement("orderrows");

		for (HostedOrderRowBuilder row : rows) {
			serializeRow(row);
		}

		xmlw.writeEndElement();
	}

	private void serializeRow(HostedOrderRowBuilder row) throws XMLStreamException {
		xmlw.writeStartElement("row");

		writeSimpleElement("sku", row.getSku());
		writeSimpleElement("name", row.getName());
		writeSimpleElement("description", row.getDescription());

		if (row.getAmount() != null) {
			writeSimpleElement("amount", row.getAmount().toString());
		}

		if (row.getVat() != null) {
			writeSimpleElement("vat", row.getVat().toString());
		}

		if (row.getQuantity() != 0) {
			writeSimpleElement("quantity", Double.toString(row.getQuantity()));
		}

		writeSimpleElement("unit", row.getUnit());

		xmlw.writeEndElement();
	}

	private void serializeExcludedPaymentMethods(List<String> excludedPaymentMethods) throws XMLStreamException {
		if (excludedPaymentMethods != null && !excludedPaymentMethods.isEmpty()) {
			xmlw.writeStartElement("excludepaymentmethods");

			List<String> excludeList = excludedPaymentMethods;

			for (int i = 0; i < excludeList.size(); i++) {
				writeSimpleElement("exclude", excludeList.get(i));
			}

			xmlw.writeEndElement();
		}
	}
}
