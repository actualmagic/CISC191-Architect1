package edu.sdccd.cisc191;

import edu.sdccd.cisc191.hashes.SHA3;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SHA3Test {
    @Test
    void testBasicHash() {
        byte b = 5;
        String input = String.valueOf((char) b);
        SHA3 sha3 = new SHA3(input,512, 256, "SHA");
    }
}