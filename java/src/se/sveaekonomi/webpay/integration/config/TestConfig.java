package se.sveaekonomi.webpay.integration.config;

import java.net.MalformedURLException;
import java.net.URL;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public class TestConfig implements Config {

	@Override
	public URL getWebserviceUrl() {
		return getUrlFromString(SWP_TEST_WS_URL);
	}

	@Override
	public URL getPayPageUrl() {
		return getUrlFromString(SWP_TEST_URL);
	}

	private URL getUrlFromString(String swpTestWsUrl) {
		try {
			return new URL(swpTestWsUrl);
		} catch (MalformedURLException e) {
			throw new SveaWebPayException("Should not happen unless someone modified the production URL", e);
		}
	}
}
