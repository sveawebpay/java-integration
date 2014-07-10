package se.sveaekonomi.webpay.integration.example;

import java.io.IOException;

//import servlet classes (provided in /lib/servlet-api.jar)
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaTestConfigurationProvider;
import se.sveaekonomi.webpay.integration.response.hosted.SveaResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

//import Svea integration package (provided in /lib/sveawebpay.jar)


/**
 * example file, how to process a card order response at the return url
 * 
 * @author Kristian Grossman-madsen for Svea WebPay
 */
public class LandingPageServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public LandingPageServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
//	    // Must set the content type first
//	    response.setContentType("text/html");
//	    // Now obtain a PrintWriter to insert HTML into
//	    PrintWriter out = response.getWriter();
//		
//		// dump the response post data returned by Svea
//		Enumeration<String> params = request.getParameterNames();
//		String paramName = null;
//		String[] paramValues = null;
//		
//		while (params.hasMoreElements()) {
//			paramName = (String) params.nextElement();
//			paramValues = request.getParameterValues(paramName);
//			
//		    out.println("<pre>\nParameter name is " + paramName);
//		    for (int i = 0; i < paramValues.length; i++) {
//		      out.println("value " + i + " is " +
//		                         paramValues[i].toString());
//		    }
//			
//		}
		
		// get configuration object holding the Svea service login credentials and the secret word corresponding to our merchant id
		ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
		String mySecretWord = myConfig.getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE); // countrycode should correspond to the request country code
		
		// process the response post data returned by Svea		
	    SveaResponse myResponse = 
	    		new SveaResponse( 
		    		request.getParameter("response"), 								// POST "response", the base64 encoded response message
//		    		request.getParameter("mac"),  									// POST "mac", the mac token
		    		mySecretWord		// the secretword corresponding to the request merchant id
				)
	    ;		
						
		// Pass service response to landingpage.jsp view as attribute in HttpServletRequest
	    request.setAttribute("cardorder_response", myResponse);
		request.getRequestDispatcher("/landingpage.jsp").forward(request, response);				
	}
}
