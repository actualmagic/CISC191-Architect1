package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Rijndael;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RijndaelTest {
    @Test
    public void basicEncryption() {
        String inputText = "32 43 f6 a8 88 5a 30 8d 31 31 98 a2 e0 37 07 34";
        String cipherKey = "2b 7e 15 16 28 ae d2 a6 ab f7 15 88 09 cf 4f 3c";
        Rijndael aes = new Rijndael(inputText, cipherKey);
        aes.encode();
    }

    @Test
    public void testVector() {
        String inputText = "00112233445566778899aabbccddeeff";
        String cipherKey = "000102030405060708090a0b0c0d0e0f";
        Rijndael aes = new Rijndael(inputText, cipherKey);
        String cipherText = aes.encode();
        assertEquals("69 C4 E0 D8 6A 7B 04 30 D8 CD B7 80 70 B4 C5 5A ", cipherText);

        aes = new Rijndael(cipherText, cipherKey);
        assertEquals("00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF ", aes.decode());
    }
}