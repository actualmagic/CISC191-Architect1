package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Rijndael;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RijndaelTest {
    @Test
    public void basicEncryption() {
        String inputText = "The quick brown fox jumps over the lazy dog.";
        String cipherKey = "e4 d9 09 c2 90 d0 fb 1c a0 68 ff ad df 22 cb d0";
        Rijndael aes = new Rijndael(inputText, cipherKey);
    }
}