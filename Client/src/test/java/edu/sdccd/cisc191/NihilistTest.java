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
        plainText = "abcdefghijklmnopqrstuvwxyz";
        assertEquals("56 45 53 55 46 56 46 76 73 64 64 66 69 73 63 88 85 46 52 77 85 84 74 99 96 43", Nihilist.encode(plainText,key, squareKey));
    }
}