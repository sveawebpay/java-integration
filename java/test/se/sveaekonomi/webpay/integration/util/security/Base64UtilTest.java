package se.sveaekonomi.webpay.integration.util.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base64UtilTest {
    
    @Test
    public void decodeString() throws Exception {
        String expected = "JAs dkjhas djha sdjha jsdh ajhsd jash";
        String encoded = "SkFzIGRramhhcyBkamhhIHNkamhhIGpzZGggYWpoc2QgamFzaA==";
        assertEquals(expected, Base64Util.decodeBase64String(encoded));
    }
    
    @Test
    public void decodeByteArray() throws Exception {
        String expected = "JAs dkjhas djha sdjha jsdh ajhsd jash";
        String encoded = "SkFzIGRramhhcyBkamhhIHNkamhhIGpzZGggYWpoc2QgamFzaA==";
        assertEquals(expected, new String(Base64Util.decodeBase64(encoded), "UTF-8"));
    }
    
    @Test
    public void encodeString() throws Exception {
        String s = "JAs dkjhas djha sdjha jsdh ajhsd jash";
        String encoded = "SkFzIGRramhhcyBkamhhIHNkamhhIGpzZGggYWpoc2QgamFzaA==";
        assertEquals(encoded, Base64Util.encodeBase64String(s));
    }
    
    @Test
    public void encodeByteArray() throws Exception {
        String msg = "JAs dkjhas djha sdjha jsdh ajhsd jash";
        String encoded = "SkFzIGRramhhcyBkamhhIHNkamhhIGpzZGggYWpoc2QgamFzaA==";
        assertEquals(encoded, Base64Util.encodeBase64Bytes(msg.getBytes("UTF-8")));
    }
}
