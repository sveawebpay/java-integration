package se.sveaekonomi.webpay.integration.util.security;

import java.io.UnsupportedEncodingException;

public abstract class Base64Util {
    
    private static byte[] decode(String msg) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(removeWhitespace(msg));
    }
    
    private static String encodeAsString(byte[] msg) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(msg);
    }
    
    private static String encodeAsString(String msg) throws UnsupportedEncodingException {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(msg.getBytes("UTF-8"));
    }
    
    public static String decodeBase64String(String msg) {
        return new String(decode(msg));
    }
    
    public static byte[] decodeBase64(String msg) {
        return decode(msg);
    }
    
    public static String encodeBase64Bytes(byte[] msg) {
        return encodeAsString(msg);
    }
    
    public static String encodeBase64String(String msg) {
        try {
            return encodeAsString(msg);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    public static String removeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        
        String result = "";
        int sz = str.length();
        
        for (int i = 0; i < sz; i++) {
            char curChar = str.charAt(i);
            if (!(Character.isWhitespace(curChar))) {
                result += curChar;
            }
        }
        
        return result;
    }
}
