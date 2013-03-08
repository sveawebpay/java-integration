package se.sveaekonomi.webpay.integration.config;

import java.net.MalformedURLException;
import java.net.URL;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public class ProductionConfig implements Config {

	@Override
	public URL getWebserviceUrl() {
		return getUrlFromString(SWP_PROD_WS_URL);
	}

	@Override
	public URL getPayPageUrl() {
		return getUrlFromString(SWP_PROD_URL);
	}

	private URL getUrlFromString(String swpProdWsUrl) {
		try {
			return new URL(swpProdWsUrl);
		} catch (MalformedURLException e) {
			throw new SveaWebPayException("Should not happen unless someone modified the production URL", e);
		}
	}

}
