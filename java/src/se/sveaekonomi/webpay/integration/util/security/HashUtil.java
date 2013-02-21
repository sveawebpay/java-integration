package se.sveaekonomi.webpay.integration.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    
    public enum HASHALGORITHM {
        MD2, MD5, SHA1, SHA_256, SHA_384, SHA_512
    };
    
    public static String createHash(String inputString, HASHALGORITHM algorithm) {
        String hash = null;
        MessageDigest digest;
        
        try {
            digest = MessageDigest.getInstance(algorithm.toString().replaceAll("_", "-"));
            digest.reset();
            byte[] hashBytes = digest.digest(inputString.getBytes());
            hash = new String(javax.xml.bind.DatatypeConverter.printHexBinary(hashBytes).toLowerCase());
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
        
        return hash;
    }
}
