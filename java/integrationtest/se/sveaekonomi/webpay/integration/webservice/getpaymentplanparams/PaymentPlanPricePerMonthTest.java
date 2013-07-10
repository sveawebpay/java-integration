package se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class PaymentPlanPricePerMonthTest {
	
	private PaymentPlanParamsResponse getParamsForTesting() throws Exception {
		GetPaymentPlanParams request = WebPay.getPaymentPlanParams();
		PaymentPlanParamsResponse response = request
				.setCountryCode(COUNTRYCODE.SE)
				.doRequest();
		return response;
	}
}
