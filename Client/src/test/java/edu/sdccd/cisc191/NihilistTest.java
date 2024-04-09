package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Caesar;
import edu.sdccd.cisc191.ciphers.Nihilist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NihilistTest {
    private String plainText, cipherText;
    private String key, squareKey;
    @Test
    void encode() {
        squareKey = "zebras";
        key = "nihilist";
        plainText = "abcdefghiklmnopqrstuvwxyz";
        assertEquals("56 45 53 55 46 56 46 76 73 65 65 67 75 74 64 89 55 53 76 83 86 85 75 100 52", Nihilist.encode(plainText,key, squareKey));
    }
}