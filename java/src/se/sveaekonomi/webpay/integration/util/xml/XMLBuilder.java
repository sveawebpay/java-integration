package se.sveaekonomi.webpay.integration.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Class used to generate XML for our requests
 */
public abstract class XMLBuilder {
    
    protected XMLStreamWriter xmlw;
    
    protected void writeSimpleElement(String name, String value) throws XMLStreamException {
        if (value != null) {
            xmlw.writeStartElement(name);
            xmlw.writeCharacters(value);
            xmlw.writeEndElement();
        }
    }
}
