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
}