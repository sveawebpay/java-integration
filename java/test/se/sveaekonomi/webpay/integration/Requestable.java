package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.Respondable;

public interface Requestable {

	public <T extends Respondable> T doRequest();
}
