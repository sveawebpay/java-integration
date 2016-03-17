package se.sveaekonomi.webpay.integration.util.calculation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtil {
    public static long convertFromDecimalToCentesimal(double value) {
		value = Math.floor(value * 10000) / 10000; // Remove insignificant decimals
		value = bankersRound(value); // Bankers rounding to two decimals
		value = value * 100; // Convert to centesimal value (kr -> öre, eur -> cent, and so on)
		return (long) bankersRound(value); // Return as long and take rounded decimals in to account
    }

    public static double bankersRound( double value ) {
    	return bankersRound( value, 2 );
    }
    
    public static double bankersRound(double value, int precision) {
    	BigDecimal rounded = new BigDecimal( String.valueOf(value) ); 
    	rounded = rounded.setScale(precision, RoundingMode.HALF_EVEN);
    	rounded = rounded.round( new MathContext(0,RoundingMode.HALF_EVEN) );
    	double retval = rounded.doubleValue();
		return retval;
    }

    public static double reverseVatRate(double rate) {
    	return (1 - (1 / (1 + rate / 100)));
    }
}
