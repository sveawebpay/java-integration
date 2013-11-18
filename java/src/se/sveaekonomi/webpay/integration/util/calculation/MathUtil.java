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

    public static double bankersRound(double value) {
    	return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).round(new MathContext(0)).doubleValue();
    }

    public static double reverseVatRate(double rate) {
    	return (1 - (1 / (1 + rate / 100)));
    }
}
