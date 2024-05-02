package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Affine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AffineTest {

    private String plainText, cipherText;
    private String key;

    @Test
    void encode() {
        plainText = "abcdefghijklmnopqrstuvwxyz";
        key = "3,2";
        Assertions.assertEquals("cfiloruxadgjmpsvybehknqtwz", Affine.encode(plainText,key).toLowerCase());
    }

    @Test
    void decode() {
        cipherText = "cfiloruxadgjmpsvybehknqtwz";
        key = "3,2";
        assertEquals("abcdefghijklmnopqrstuvwxyz", Affine.decode(cipherText, key).toLowerCase());
    }
}