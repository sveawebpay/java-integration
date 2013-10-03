package se.sveaekonomi.webpay.integration.util.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HashUtilTest {
    
    @Test
    public void MD2() throws Exception {
        String hash = "e26c02e43d0e68b42955da531c9197fe";
        
        assertEquals(hash, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.MD2));
    }
    
    @Test
    public void MD5() throws Exception {
        String hash = "b61dc9eca536bcd38d01bcfbbd1283db";
        
        assertEquals(hash, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.MD5));
    }
    
    @Test
    public void SHA1() throws Exception {
        String hash = "bd76b50ae8567613cbfd4bba43221bd889694da7";
        
        assertEquals(hash, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.SHA1));
    }
    
    @Test
    public void SHA_256() throws Exception {
        String sha256 = "b2b2c90ab2a35e280729bd1b326ad10f19b584e22bfb4e5b2e1599d73da30370";
        
        assertEquals(sha256, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.SHA_256));
    }
    
    @Test
    public void SHA_384() throws Exception {
        String hash = "8b362350c8fb2ef8a6ba571c7fa24ea0e4ac355b5e059d166769e53c7b945e064d1c0f1076e86def5d3cd6bca58757b7";
        
        assertEquals(hash, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.SHA_384));
    }
    
    @Test
    public void SHA_512() throws Exception {
        String hash = "fe54c6e8727e9f8bf5f4f8e47a05567d694f68049cd1f116c19d9a6fbd066a742305d23da164291bca8869c34e7b8ff3bee15ab2da011d4ddc57adc736bc12ba";
        
        assertEquals(hash, HashUtil.createHash("Hsjhasj djahs djahs d", HashUtil.HASHALGORITHM.SHA_512));
    }
}
