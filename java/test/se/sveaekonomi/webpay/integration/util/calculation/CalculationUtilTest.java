package se.sveaekonomi.webpay.integration.util.calculation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

public class CalculationUtilTest {
    
    @Test
    public void testRoundsLessThanFiveDown() {
    	
    	double input = 0.051D;
    	double expected = 0.05D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testRoundsMoreThanFiveUp() {
    	
    	double input = 0.056D;
    	double expected = 0.06D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }
    
    @Test
    public void testRoundsFiveUpToEven() {
    	
    	double input = 0.075D;
    	double expected = 0.08D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }    

    @Test
    public void testRoundsFiveDownToEven() {
    	
    	double input = 0.065D;
    	double expected = 0.06D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }      

    @Test
    public void testRoundsFiveDownToEvenWhenZeroDecimals() {
    	
    	double input = 2.5D;
    	double expected = 2.0D;
    	
    	double actual = MathUtil.bankersRound(input,0);
    	
    	assertEquals(expected, actual, 0.0001);
    }       

    @Test
    public void testDontRoundFiveDownToEvenWhenOneDecimal() {
    	
    	double input = 2.5D;
    	double expected = 2.5D;
    	
    	double actual = MathUtil.bankersRound(input,1);
    	
    	assertEquals(expected, actual, 0.0001);
    }           

    @Test
    public void testRoundsLessThanFiveToEvenWhenManyDecimals() {
    	
    	double input = 13.5849D;
    	double expected = 13.58D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }           

    @Test
    public void testRoundsLessThanFiveToEvenWhenNegative() {
    	
    	double input = -13.5849D;
    	double expected = -13.58D;
    	
    	double actual = MathUtil.bankersRound(input);
    	
    	assertEquals(expected, actual, 0.0001);
    }      
}
