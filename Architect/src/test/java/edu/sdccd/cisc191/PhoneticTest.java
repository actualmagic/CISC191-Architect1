package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Phonetic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhoneticTest {
    private String cipherText;
    @Test
    void printPhoneticEncoded() {
        cipherText = "hello there";
        assertEquals("Hotel Echo Lima Lima Oscar  Tango Hotel Echo Romeo Echo ", Phonetic.printPhoneticEncoded(cipherText));
    }

    @Test
    void printPhoneticDecoded() {
        cipherText = "Hotel Echo Lima Lima Oscar  Tango Hotel Echo Romeo Echo";
        assertEquals("hello there", Phonetic.printPhoneticDecoded(cipherText).toLowerCase());
    }
}