package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Rijndael;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RijndaelTest {
    @Test
    public void basicEncryptionECB() {
        String inputText = "32 43 f6 a8 88 5a 30 8d 31 31 98 a2 e0 37 07 34";
        String cipherKey = "2b 7e 15 16 28 ae d2 a6 ab f7 15 88 09 cf 4f 3c";
        Rijndael aes = new Rijndael(inputText, cipherKey, "ECB");
        aes.encode();
    }

    @Test
    public void testVectorECB() {
        String inputText = "00112233445566778899aabbccddeeffaa";
        String cipherKey = "000102030405060708090a0b0c0d0e0f";
        Rijndael aes = new Rijndael(inputText, cipherKey, "ECB");
        String cipherText = aes.encode();
        //assertEquals("69 C4 E0 D8 6A 7B 04 30 D8 CD B7 80 70 B4 C5 5A ", cipherText);

        aes = new Rijndael(cipherText, cipherKey, "ECB");
        assertEquals("00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF AA 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ", aes.decode());
    }

    @Test
    public void testVectorCTR() {
        String inputText = "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710";
        String cipherKey = "2b7e151628aed2a6abf7158809cf4f3c" + "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff";
        Rijndael aes = new Rijndael(inputText, cipherKey, "CTR");
        String cipherText = aes.encode();
        assertEquals("874d6191b620e3261bef6864990db6ce9806f66b7970fdff8617187bb9fffdff5ae4df3edbd5d35e5b4f09020db03eab1e031dda2fbe03d1792170a0f3009cee".toUpperCase(), cipherText.replaceAll(" ", ""));

        aes = new Rijndael(cipherText, cipherKey, "CTR");
        String plainText = aes.decode();
        assertEquals(inputText.toUpperCase(), plainText.replaceAll(" ", ""));
    }
}